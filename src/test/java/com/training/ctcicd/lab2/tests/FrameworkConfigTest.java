package com.training.ctcicd.lab2.tests;

import com.training.ctcicd.lab2.config.FrameworkConfig;
import io.qameta.allure.Feature;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import io.qameta.allure.Story;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@Feature("Configuration loading")
class FrameworkConfigTest {

    @Test
    @Story("Defaults are loaded from classpath config")
    @Severity(SeverityLevel.NORMAL)
    void loads_base_url_from_defaults() {
        var config = new FrameworkConfig().loadDefaults();
        assertThat(config.baseUrl()).isEqualTo("http://localhost:8080");
    }

    @Test
    @Story("Test users are extracted from user.* keys")
    @Severity(SeverityLevel.NORMAL)
    void loads_test_users_map() {
        var users = new FrameworkConfig().loadDefaults().testUsers();
        assertThat(users).containsEntry("alice", "alice123");
        assertThat(users).containsEntry("bob", "bob456");
    }
}
