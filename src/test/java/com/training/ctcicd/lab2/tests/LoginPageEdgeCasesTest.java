package com.training.ctcicd.lab2.tests;

import com.training.ctcicd.lab2.pages.LoginPage;
import io.qameta.allure.Feature;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import io.qameta.allure.Story;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@Feature("Login negative checks")
class LoginPageEdgeCasesTest {

    private final LoginPage loginPage = new LoginPage("http://localhost:8080", Map.of("alice", "alice123"));

    @Test
    @Story("Null username is rejected")
    @Severity(SeverityLevel.NORMAL)
    void null_username_fails() {
        var result = loginPage.submitCredentials(null, "alice123");
        assertThat(result.success()).isFalse();
        assertThat(result.message()).isEqualTo("username required");
    }

    @Test
    @Story("Null password is rejected")
    @Severity(SeverityLevel.NORMAL)
    void null_password_fails() {
        var result = loginPage.submitCredentials("alice", null);
        assertThat(result.success()).isFalse();
        assertThat(result.message()).isEqualTo("password required");
    }

    @Test
    @Story("Unknown users are rejected")
    @Severity(SeverityLevel.NORMAL)
    void unknown_user_fails() {
        var result = loginPage.submitCredentials("unknown", "any");
        assertThat(result.success()).isFalse();
        assertThat(result.message()).isEqualTo("invalid credentials");
    }
}
