package com.training.ctcicd.lab6;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;
import org.junit.jupiter.api.parallel.ResourceLock;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * JDBC analogue of Spring {@code @Transactional} rollback: uncommitted work must not persist after rollback.
 * Runs when {@code DB_URL} points at PostgreSQL (Compose / CI).
 */
@ResourceLock("jdbc.postgres.shared")
@EnabledIfEnvironmentVariable(named = "DB_URL", matches = ".*postgresql.*")
class PostgresTransactionRollbackTest {

    @Test
    void rolled_back_insert_is_not_visible() throws Exception {
        String url = requiredEnv("DB_URL");
        String user = requiredEnv("DB_USER");
        String pass = envOrEmpty("DB_PASS");
        String id = "txn_test_" + UUID.randomUUID();

        try (Connection c = DriverManager.getConnection(url, user, pass)) {
            c.setAutoCommit(false);
            try (PreparedStatement ins =
                    c.prepareStatement(
                            "INSERT INTO users (id, name, email) VALUES (?, ?, ?)")) {
                ins.setString(1, id);
                ins.setString(2, "Rollback Demo");
                ins.setString(3, "rollback_test@example.com");
                ins.executeUpdate();
            }
            c.rollback();
            c.setAutoCommit(true);
        }

        try (Connection c = DriverManager.getConnection(url, user, pass);
                PreparedStatement q =
                        c.prepareStatement("SELECT 1 FROM users WHERE id = ?")) {
            q.setString(1, id);
            try (ResultSet rs = q.executeQuery()) {
                assertThat(rs.next()).isFalse();
            }
        }
    }

    @Test
    void committed_insert_survives_new_connection() throws Exception {
        String url = requiredEnv("DB_URL");
        String user = requiredEnv("DB_USER");
        String pass = envOrEmpty("DB_PASS");
        String id = "txn_commit_" + UUID.randomUUID();

        try (Connection c = DriverManager.getConnection(url, user, pass)) {
            c.setAutoCommit(false);
            try (PreparedStatement ins =
                    c.prepareStatement(
                            "INSERT INTO users (id, name, email) VALUES (?, ?, ?)")) {
                ins.setString(1, id);
                ins.setString(2, "Commit Demo");
                ins.setString(3, "commit_test@example.com");
                ins.executeUpdate();
            }
            c.commit();
            c.setAutoCommit(true);
        }

        try (Connection c = DriverManager.getConnection(url, user, pass);
                PreparedStatement q = c.prepareStatement("SELECT name FROM users WHERE id = ?")) {
            q.setString(1, id);
            try (ResultSet rs = q.executeQuery()) {
                assertThat(rs.next()).isTrue();
                assertThat(rs.getString(1)).isEqualTo("Commit Demo");
            }
        }

        try (Connection c = DriverManager.getConnection(url, user, pass);
                PreparedStatement del = c.prepareStatement("DELETE FROM users WHERE id = ?")) {
            del.setString(1, id);
            del.executeUpdate();
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
