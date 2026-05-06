package com.training.ctcicd.lab6;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;
import org.junit.jupiter.api.parallel.ResourceLock;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Runs when {@code DB_URL} points at PostgreSQL (Docker Compose / CI). Skipped locally if unset — avoids
 * "connection refused" on laptops without Postgres.
 */
@ResourceLock("jdbc.postgres.shared")
@EnabledIfEnvironmentVariable(named = "DB_URL", matches = ".*postgresql.*")
class PostgresSeedDataTest {

    @Test
    void seed_sql_users_are_queryable() throws Exception {
        String url = requiredEnv("DB_URL");
        String user = requiredEnv("DB_USER");
        String pass = envOrEmpty("DB_PASS");

        try (Connection c = DriverManager.getConnection(url, user, pass);
                Statement st = c.createStatement();
                ResultSet rs =
                        st.executeQuery(
                                "SELECT email FROM users WHERE id = 'test_alice'")) {
            assertThat(rs.next()).isTrue();
            assertThat(rs.getString(1)).isEqualTo("alice_test@example.com");
        }
    }

    @Test
    void uuid_prefixed_row_roundTrip() throws Exception {
        String url = requiredEnv("DB_URL");
        String user = requiredEnv("DB_USER");
        String pass = envOrEmpty("DB_PASS");
        String id = "user_test_" + UUID.randomUUID();

        try (Connection c = DriverManager.getConnection(url, user, pass)) {
            try (PreparedStatement ins =
                    c.prepareStatement(
                            "INSERT INTO users (id, name, email) VALUES (?, ?, ?)")) {
                ins.setString(1, id);
                ins.setString(2, "Temp");
                ins.setString(3, "temp_test@example.com");
                ins.executeUpdate();
            }
            try (PreparedStatement q =
                    c.prepareStatement("SELECT name FROM users WHERE id = ?")) {
                q.setString(1, id);
                try (ResultSet rs = q.executeQuery()) {
                    assertThat(rs.next()).isTrue();
                    assertThat(rs.getString(1)).isEqualTo("Temp");
                }
            }
            try (PreparedStatement del = c.prepareStatement("DELETE FROM users WHERE id = ?")) {
                del.setString(1, id);
                del.executeUpdate();
            }
        }
    }

    private static String requiredEnv(String name) {
        String v = System.getenv(name);
        if (v == null || v.isBlank()) {
            throw new IllegalStateException("Missing env: " + name);
        }
        return v;
    }

    private static String envOrEmpty(String name) {
        String v = System.getenv(name);
        return v == null ? "" : v;
    }
}
