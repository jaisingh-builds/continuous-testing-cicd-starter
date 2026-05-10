package com.training.ctcicd.lab2.tests;

import com.training.ctcicd.lab2.config.FrameworkConfig;
import com.training.ctcicd.lab2.pages.LoginPage;
import io.qameta.allure.Feature;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import io.qameta.allure.Story;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Not tagged {@code smoke} — included in full suite only; demonstrates selective execution.
 */
@Feature("Login negative checks")
class RegressionExtrasTest {

    @Test
    @Story("Blank username should fail authentication")
    @Severity(SeverityLevel.NORMAL)
    void blank_username_fails() {
        var cfg = new FrameworkConfig().loadDefaults();
        var page = new LoginPage(cfg.baseUrl(), cfg.testUsers());
        assertThat(page.submitCredentials("", "x").success()).isFalse();
    }

    @Test
    @Story("Blank password should fail authentication")
    @Severity(SeverityLevel.NORMAL)
    void blank_password_fails() {
        var cfg = new FrameworkConfig().loadDefaults();
        var page = new LoginPage(cfg.baseUrl(), cfg.testUsers());
        assertThat(page.submitCredentials("alice", "").success()).isFalse();
    }
}
