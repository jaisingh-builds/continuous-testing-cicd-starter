package com.training.ctcicd.lab2.tests;

import com.training.ctcicd.lab2.config.FrameworkConfig;
import com.training.ctcicd.lab2.pages.LoginPage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvFileSource;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Data-driven login checks — same test logic, multiple rows from CSV (CI runs all variants).
 */
class LoginDataDrivenTest {

    private LoginPage loginPage;

    @BeforeEach
    void setUp() {
        var cfg = new FrameworkConfig().loadDefaults();
        loginPage = new LoginPage(cfg.baseUrl(), cfg.testUsers());
    }

    @ParameterizedTest
    @CsvFileSource(resources = "/testdata/login-scenarios.csv", numLinesToSkip = 1)
    @Tag("smoke")
    void login_scenarios_from_csv(String username, String password, boolean expectSuccess) {
        var result = loginPage.submitCredentials(username, password);
        assertThat(result.success()).isEqualTo(expectSuccess);
    }
}
