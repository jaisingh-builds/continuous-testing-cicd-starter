package com.training.ctcicd.lab2.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Properties;
import java.util.stream.Collectors;

/**
 * Loads {@code config.properties} from the classpath; environment variables override keys
 * (e.g. {@code BASE_URL} overrides {@code base.url}) — CI-friendly, no hardcoded secrets in code.
 */
public final class FrameworkConfig {

    private final Properties props = new Properties();

    public FrameworkConfig loadDefaults() {
        try (InputStream in = FrameworkConfig.class.getResourceAsStream("/config.properties")) {
            if (in != null) {
                props.load(in);
            }
        } catch (IOException e) {
            throw new IllegalStateException("Failed to load config.properties", e);
        }
        return this;
    }

    public String baseUrl() {
        return firstNonBlank(env("BASE_URL"), props.getProperty("base.url", "http://localhost:8080"));
    }

    public Map<String, String> testUsers() {
        return props.stringPropertyNames().stream()
                .filter(k -> k.startsWith("user."))
                .collect(Collectors.toMap(
                        k -> k.substring("user.".length()),
                        props::getProperty));
    }

    private static String env(String name) {
        String v = System.getenv(name);
        return v == null || v.isBlank() ? null : v;
    }

    private static String firstNonBlank(String a, String b) {
        if (a != null && !a.isBlank()) {
            return a;
        }
        return b;
    }
}
