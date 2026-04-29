package com.training.ctcicd.lab2.tests;

import com.training.ctcicd.lab2.config.FrameworkConfig;
import com.training.ctcicd.lab2.pages.LoginPage;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/** Fast “smoke” checks — use for selective CI runs (tags). */
@Tag("smoke")
class SmokeRoutingTest {

    @Test
    void login_page_exposes_expected_path() {
        var cfg = new FrameworkConfig().loadDefaults();
        var page = new LoginPage(cfg.baseUrl(), cfg.testUsers());
        assertThat(page.url()).endsWith("/login");
    }

    @Test
    void happy_path_login_for_alice() {
        var cfg = new FrameworkConfig().loadDefaults();
        var page = new LoginPage(cfg.baseUrl(), cfg.testUsers());
        assertThat(page.submitCredentials("alice", "alice123").success()).isTrue();
    }
}
