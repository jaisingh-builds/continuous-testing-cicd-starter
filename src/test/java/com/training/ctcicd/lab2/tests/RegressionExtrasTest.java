package com.training.ctcicd.lab2.tests;

import com.training.ctcicd.lab2.config.FrameworkConfig;
import com.training.ctcicd.lab2.pages.LoginPage;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Not tagged {@code smoke} — included in full suite only; demonstrates selective execution.
 */
class RegressionExtrasTest {

    @Test
    void blank_username_fails() {
        var cfg = new FrameworkConfig().loadDefaults();
        var page = new LoginPage(cfg.baseUrl(), cfg.testUsers());
        assertThat(page.submitCredentials("", "x").success()).isFalse();
    }

    @Test
    void blank_password_fails() {
        var cfg = new FrameworkConfig().loadDefaults();
        var page = new LoginPage(cfg.baseUrl(), cfg.testUsers());
        assertThat(page.submitCredentials("alice", "").success()).isFalse();
    }
}
