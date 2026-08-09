package configs;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public final class ConfigReader {

    private static final Properties PROPERTIES = new Properties();

    static {
        loadProperties();
    }

    private ConfigReader() {
        // Prevent object creation
    }

    private static void loadProperties() {

        try (InputStream input =
                     ConfigReader.class
                             .getClassLoader()
                             .getResourceAsStream("config.properties")) {

            if (input == null) {
                throw new RuntimeException(
                        "config.properties not found"
                );
            }

            PROPERTIES.load(input);

        } catch (IOException e) {
            throw new RuntimeException(
                    "Failed to load config.properties", e
            );
        }
    }

    private static String get(String key) {

        String value = PROPERTIES.getProperty(key);

        if (value == null || value.isBlank()) {
            throw new RuntimeException(
                    "Missing configuration property: " + key
            );
        }

        // Resolve environment variables: ${ENV_VARIABLE}
        if (value.startsWith("${") && value.endsWith("}")) {

            String envVariable =
                    value.substring(2, value.length() - 1);

            String envValue =
                    System.getenv(envVariable);

            if (envValue == null || envValue.isBlank()) {
                throw new RuntimeException(
                        "Environment variable not set: "
                                + envVariable
                );
            }

            return envValue;
        }

        return value;
    }

    public static String getBaseUrl() {
        return get("base.url");
    }

    public static String getBasePath() {
        return get("base.path");
    }

    public static String getContentType() {
        return get("content.type");
    }

    public static String getAcceptType() {
        return get("accept.type");
    }

    public static long getResponseTimeout() {
        return Long.parseLong(
                get("response.timeout")
        );
    }
    public static String getBaseEnvironment() {
        return get("base.env");
    }
    public static String getProjectId() {
        return get("project.id");
    }
    public static String getAPIKey() {
        return get("api.key");
    }
    public static String getBaseEndpoint() {
        return get("base.endpoint");
    }
}