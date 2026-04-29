package com.training.ctcicd.lab2.pages;

import java.util.Map;
import java.util.Objects;

/**
 * Page Object–style API without a real browser: encapsulates “actions” and “assertions”
 * for the login screen so tests stay readable and locators/rules live in one place.
 */
public final class LoginPage {

    private final String baseUrl;
    private final Map<String, String> validUsers;

    public LoginPage(String baseUrl, Map<String, String> validUsers) {
        this.baseUrl = Objects.requireNonNull(baseUrl, "baseUrl");
        this.validUsers = Map.copyOf(validUsers);
    }

    public String url() {
        return baseUrl + "/login";
    }

    public LoginResult submitCredentials(String username, String password) {
        if (username == null || username.isBlank()) {
            return LoginResult.fail("username required");
        }
        if (password == null || password.isBlank()) {
            return LoginResult.fail("password required");
        }
        String expected = validUsers.get(username);
        if (expected != null && expected.equals(password)) {
            return LoginResult.ok();
        }
        return LoginResult.fail("invalid credentials");
    }

    public record LoginResult(boolean success, String message) {
        public static LoginResult ok() {
            return new LoginResult(true, "ok");
        }

        public static LoginResult fail(String message) {
            return new LoginResult(false, message);
        }
    }
}
