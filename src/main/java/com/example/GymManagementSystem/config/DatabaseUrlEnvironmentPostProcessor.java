package com.example.GymManagementSystem.config;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.env.EnvironmentPostProcessor;
import org.springframework.core.Ordered;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;

import java.net.URI;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class DatabaseUrlEnvironmentPostProcessor implements EnvironmentPostProcessor, Ordered {

    private static final String PROPERTY_SOURCE_NAME = "platformDatabaseUrl";

    @Override
    public void postProcessEnvironment(ConfigurableEnvironment environment, SpringApplication application) {
        String databaseUrl = firstPresent(
                System.getenv("SPRING_DATASOURCE_URL"),
                System.getenv("JDBC_DATABASE_URL"),
                System.getenv("MYSQL_URL"),
                System.getenv("DATABASE_URL")
        );

        if (databaseUrl == null) {
            return;
        }

        Map<String, Object> properties = databaseUrl.startsWith("jdbc:")
                ? Map.of("spring.datasource.url", databaseUrl)
                : convert(databaseUrl);
        if (!properties.isEmpty()) {
            environment.getPropertySources().addFirst(new MapPropertySource(PROPERTY_SOURCE_NAME, properties));
        }
    }

    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE;
    }

    private Map<String, Object> convert(String databaseUrl) {
        Map<String, Object> properties = new HashMap<>();
        URI uri = URI.create(databaseUrl);
        String scheme = uri.getScheme();
        if (scheme == null) {
            return properties;
        }

        if (scheme.equals("postgres") || scheme.equals("postgresql")) {
            properties.put("spring.datasource.url", jdbcUrl("postgresql", uri, 5432, uri.getRawQuery()));
            putCredentials(properties, uri);
        } else if (scheme.equals("mysql")) {
            String query = uri.getRawQuery();
            if (query == null || query.isBlank()) {
                query = "useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";
            }
            properties.put("spring.datasource.url", jdbcUrl("mysql", uri, 3306, query));
            putCredentials(properties, uri);
        }
        return properties;
    }

    private String jdbcUrl(String jdbcScheme, URI uri, int defaultPort, String query) {
        String path = uri.getRawPath() == null || uri.getRawPath().isBlank() ? "/" : uri.getRawPath();
        StringBuilder builder = new StringBuilder("jdbc:")
                .append(jdbcScheme)
                .append("://")
                .append(uri.getHost())
                .append(":")
                .append(uri.getPort() == -1 ? defaultPort : uri.getPort())
                .append(path);
        if (query != null && !query.isBlank()) {
            builder.append("?").append(query);
        }
        return builder.toString();
    }

    private void putCredentials(Map<String, Object> properties, URI uri) {
        String userInfo = uri.getRawUserInfo();
        if (userInfo == null || userInfo.isBlank()) {
            return;
        }
        String[] parts = userInfo.split(":", 2);
        properties.put("spring.datasource.username", decode(parts[0]));
        if (parts.length > 1) {
            properties.put("spring.datasource.password", decode(parts[1]));
        }
    }

    private String decode(String value) {
        return URLDecoder.decode(value, StandardCharsets.UTF_8);
    }

    private String firstPresent(String... values) {
        for (String value : values) {
            if (value != null && !value.isBlank()) {
                return value;
            }
        }
        return null;
    }
}


