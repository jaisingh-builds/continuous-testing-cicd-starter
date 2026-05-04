package com.training.ctcicd.lab5.api;

import io.restassured.http.ContentType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import com.github.tomakehurst.wiremock.junit5.WireMockExtension;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.equalToJson;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;
import static io.restassured.RestAssured.given;
import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

/**
 * Day 5 API tests (Failsafe {@code *IT}, tag {@code api}). WireMock is the default so CI needs no
 * live service. Set {@code API_BASE_URL} to your running starter app (e.g. {@code http://localhost:8080})
 * to exercise the real {@code UserController}.
 */
@Tag("api")
class UserApiIT {

    @RegisterExtension
    static WireMockExtension wm = WireMockExtension.newInstance()
            .options(wireMockConfig().dynamicPort())
            .build();

    private String baseUri() {
        String override = System.getenv("API_BASE_URL");
        if (override != null && !override.isBlank()) {
            return override.replaceAll("/$", "");
        }
        return wm.getRuntimeInfo().getHttpBaseUrl().replaceAll("/$", "");
    }

    private boolean useWireMock() {
        String override = System.getenv("API_BASE_URL");
        return override == null || override.isBlank();
    }

    @BeforeEach
    void configureStubs() {
        if (!useWireMock()) {
            return;
        }
        wm.stubFor(get(urlEqualTo("/users/1"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody("{\"id\":1,\"name\":\"Alice\",\"email\":\"alice@example.com\"}")));

        wm.stubFor(get(urlEqualTo("/users/99999"))
                .willReturn(aResponse()
                        .withStatus(404)
                        .withHeader("Content-Type", "application/json")
                        .withBody("{\"error\":\"not found\"}")));

        wm.stubFor(post(urlEqualTo("/users"))
                .withRequestBody(equalToJson("{\"name\":\"Bob\",\"email\":\"bob@example.com\"}", true, true))
                .willReturn(aResponse()
                        .withStatus(201)
                        .withHeader("Content-Type", "application/json")
                        .withHeader("Location", "/users/42")
                        .withBody("{\"id\":42,\"name\":\"Bob\",\"email\":\"bob@example.com\"}")));
    }

    @Test
    void getUser_returns200_withExpectedBodyAndSchema() {
        given()
                .baseUri(baseUri())
                .header("Accept", "application/json")
                .when()
                .get("/users/1")
                .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body("name", equalTo("Alice"))
                .body("email", notNullValue())
                .body(matchesJsonSchemaInClasspath("schemas/user-schema.json"));
    }

    @Test
    void getUser_unknownId_returns404() {
        given()
                .baseUri(baseUri())
                .when()
                .get("/users/99999")
                .then()
                .statusCode(404);
    }

    @Test
    void createUser_returns201_withLocationHeader() {
        given()
                .baseUri(baseUri())
                .header("Content-Type", "application/json")
                .body("{\"name\":\"Bob\",\"email\":\"bob@example.com\"}")
                .when()
                .post("/users")
                .then()
                .statusCode(201)
                .header("Location", notNullValue())
                .body("id", equalTo(42))
                .body(matchesJsonSchemaInClasspath("schemas/user-schema.json"));
    }

    @ParameterizedTest
    @CsvSource({
            "1, Alice, 200",
            "99999, _, 404"
    })
    void getUser_variousIds(String id, String nameOrIgnore, int expectedStatus) {
        var spec = given()
                .baseUri(baseUri())
                .header("Accept", "application/json")
                .when()
                .get("/users/{id}", id)
                .then()
                .statusCode(expectedStatus);
        if (expectedStatus == 200) {
            spec.body("name", equalTo(nameOrIgnore));
        }
    }
}
