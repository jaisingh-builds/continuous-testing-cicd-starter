package com.training.ctcicd.lab5.api;

import com.example.demo.controller.UserController;
import io.qameta.allure.Feature;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import io.qameta.allure.Story;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@Feature("User API controller behavior")
class UserControllerUnitTest {

    private final UserController controller = new UserController();

    @Test
    @Story("Known user id returns Alice profile")
    @Severity(SeverityLevel.NORMAL)
    void get_user_id_1_returns_alice() {
        var response = controller.getUser(1);
        assertThat(response.getStatusCode().value()).isEqualTo(200);
        assertThat(response.getBody()).containsEntry("name", "Alice");
        assertThat(response.getBody()).containsEntry("email", "alice@example.com");
    }

    @Test
    @Story("Unknown user id returns generic profile")
    @Severity(SeverityLevel.NORMAL)
    void get_user_non_special_id_returns_generic_user() {
        var response = controller.getUser(2);
        assertThat(response.getStatusCode().value()).isEqualTo(200);
        assertThat(response.getBody()).containsEntry("name", "TestUser");
        assertThat(response.getBody()).containsEntry("email", "test@example.com");
    }

    @Test
    @Story("Reserved id returns not found")
    @Severity(SeverityLevel.NORMAL)
    void get_user_not_found_branch() {
        var response = controller.getUser(99999);
        assertThat(response.getStatusCode().value()).isEqualTo(404);
    }

    @Test
    @Story("Canonical Bob payload returns id 42 and location header")
    @Severity(SeverityLevel.CRITICAL)
    void create_user_bob_branch() {
        var response = controller.createUser(Map.of("name", "Bob", "email", "bob@example.com"));
        assertThat(response.getStatusCode().value()).isEqualTo(201);
        assertThat(response.getHeaders().getFirst("Location")).isEqualTo("/users/42");
        assertThat(response.getBody()).containsEntry("id", 42);
    }

    @Test
    @Story("Other payloads return default id branch")
    @Severity(SeverityLevel.NORMAL)
    void create_user_default_branch() {
        var response = controller.createUser(Map.of("name", "Eve", "email", "eve@example.com"));
        assertThat(response.getStatusCode().value()).isEqualTo(201);
        assertThat(response.getBody()).containsEntry("id", 100);
    }
}
