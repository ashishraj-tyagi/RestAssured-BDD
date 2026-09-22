package org.poc.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public final class ConfigManager {

    private static final Properties PROPERTIES = new Properties();

    static {
        try (InputStream stream = ConfigManager.class.getClassLoader()
                .getResourceAsStream("config/config.properties")) {
            if (stream == null) {
                throw new IllegalStateException("config/config.properties not found on classpath");
            }
            PROPERTIES.load(stream);
        } catch (IOException e) {
            throw new ExceptionInInitializerError("Failed to load configuration: " + e.getMessage());
        }
    }

    private ConfigManager() {
    }

    public static String get(String key) {
        String value = resolve(key, PROPERTIES.getProperty(key));
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Missing or empty config property: " + key);
        }
        return value;
    }

    public static String get(String key, String defaultValue) {
        String value = resolve(key, PROPERTIES.getProperty(key, defaultValue));
        if (value == null || value.isBlank()) {
            return defaultValue;
        }
        return value;
    }

    public static int getInt(String key, int defaultValue) {
        String value = resolve(key, PROPERTIES.getProperty(key));
        if (value == null || value.isBlank()) {
            return defaultValue;
        }
        return Integer.parseInt(value.trim());
    }

    /**
     * Resolution order: -D system property → env (KEY / KEY_WITH_UNDERSCORES) →
     * file value with ${ENV} / ${ENV:default} placeholders.
     */
    private static String resolve(String key, String fromFile) {
        String sys = System.getProperty(key);
        if (sys != null && !sys.isBlank()) {
            return sys.trim();
        }

        String envDirect = System.getenv(key);
        if (envDirect != null && !envDirect.isBlank()) {
            return envDirect.trim();
        }

        String envKey = key.toUpperCase().replace('.', '_');
        String envMapped = System.getenv(envKey);
        if (envMapped != null && !envMapped.isBlank()) {
            return envMapped.trim();
        }

        // Common aliases for base URL
        if ("base.url".equals(key)) {
            String stockroom = System.getenv("STOCKROOM_BASE_URL");
            if (stockroom != null && !stockroom.isBlank()) {
                return stockroom.trim().replaceAll("/$", "");
            }
            String base = System.getenv("BASE_URL");
            if (base != null && !base.isBlank()) {
                return base.trim().replaceAll("/$", "");
            }
        }

        if (fromFile == null) {
            return null;
        }
        return resolvePlaceholders(fromFile.trim());
    }

    private static String resolvePlaceholders(String value) {
        if (!value.startsWith("${") || !value.endsWith("}")) {
            return value;
        }
        String inner = value.substring(2, value.length() - 1);
        String envKey = inner;
        String defaultValue = null;
        int colon = inner.indexOf(':');
        if (colon >= 0) {
            envKey = inner.substring(0, colon);
            defaultValue = inner.substring(colon + 1);
        }
        String envValue = System.getenv(envKey);
        if (envValue != null && !envValue.isBlank()) {
            return envValue.trim();
        }
        if (defaultValue != null) {
            return defaultValue;
        }
        return value;
    }
}
