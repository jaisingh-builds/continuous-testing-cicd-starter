package com.training.ctcicd.lab2.tests;

import com.training.ctcicd.lab2.config.FrameworkConfig;
import com.training.ctcicd.lab2.pages.LoginPage;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Marked for Day 4 CI: run selectively with {@code -Dgroups=integration} (Surefire / JUnit Platform).
 * Represents slower or service-touching checks in a real project.
 */
@Tag("integration")
class IntegrationFlowSampleTest {

    @Test
    void end_to_end_style_login_happy_paths() {
        var cfg = new FrameworkConfig().loadDefaults();
        var page = new LoginPage(cfg.baseUrl(), cfg.testUsers());
        assertThat(page.submitCredentials("alice", "alice123").success()).isTrue();
        assertThat(page.submitCredentials("bob", "bob456").success()).isTrue();
    }
}
