package com.training.ctcicd.lab6;

import org.junit.jupiter.api.Test;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Slide-aligned Testcontainers example: real Postgres, mapped ports, Ryuk cleanup.
 * <p>
 * Excluded from default Surefire (Compose {@code test-runner} has no Docker socket).
 * Run on the host: {@code ./mvnw test -Pwith-testcontainers}
 */
@Testcontainers(disabledWithoutDocker = true)
class PostgresTcTest {

    @Container
    static PostgreSQLContainer<?> postgres =
            new PostgreSQLContainer<>("postgres:16-alpine")
                    .withDatabaseName("testdb")
                    .withUsername("testuser")
                    .withPassword("testpass")
                    .withInitScript("data/seed.sql");

    @Test
    void seed_sql_users_are_queryable() throws Exception {
        try (Connection c =
                        DriverManager.getConnection(
                                postgres.getJdbcUrl(),
                                postgres.getUsername(),
                                postgres.getPassword());
                ResultSet rs =
                        c.createStatement()
                                .executeQuery(
                                        "SELECT email FROM users WHERE id = 'test_alice'")) {
            assertThat(rs.next()).isTrue();
            assertThat(rs.getString(1)).isEqualTo("alice_test@example.com");
        }
    }

    @Test
    void uuid_prefixed_row_roundTrip() throws Exception {
        String id = "user_test_" + UUID.randomUUID();
        try (Connection c =
                DriverManager.getConnection(
                        postgres.getJdbcUrl(),
                        postgres.getUsername(),
                        postgres.getPassword())) {
            try (PreparedStatement ins =
                    c.prepareStatement(
                            "INSERT INTO users (id, name, email) VALUES (?, ?, ?)")) {
                ins.setString(1, id);
                ins.setString(2, "TcTemp");
                ins.setString(3, "tc_temp_test@example.com");
                ins.executeUpdate();
            }
            try (PreparedStatement q =
                    c.prepareStatement("SELECT name FROM users WHERE id = ?")) {
                q.setString(1, id);
                try (ResultSet rs = q.executeQuery()) {
                    assertThat(rs.next()).isTrue();
                    assertThat(rs.getString(1)).isEqualTo("TcTemp");
                }
            }
            try (PreparedStatement del =
                    c.prepareStatement("DELETE FROM users WHERE id = ?")) {
                del.setString(1, id);
                del.executeUpdate();
            }
        }
    }
}
