package org.Utils;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class ConfigReader {

    private static final Properties configProperties = new Properties();  // non-sensitive
    private static final Properties secretProperties = new Properties();  // sensitive

    static {
        try {
            // Load non-sensitive config
            String configFilePath = System.getProperty("user.dir") + "/src/main/java/org/resources/config.properties";
            FileInputStream configFis = new FileInputStream(configFilePath);
            configProperties.load(configFis);
            configFis.close();
            System.out.println("✅ Loaded non-sensitive config.properties");

            // Load sensitive config
            String secretsConfigFilePath = System.getProperty("user.dir") + "/src/main/java/org/resources/secretsConfig.properties";
            FileInputStream secretsConfigFis = new FileInputStream(secretsConfigFilePath);
            secretProperties.load(secretsConfigFis);
            secretsConfigFis.close();
            System.out.println("✅ Loaded sensitive secretsConfig.properties");

        } catch (IOException e) {
            System.err.println("⚠️ Could not load config or secrets file: " + e.getMessage());
            System.err.println("👉 Assuming environment variables or Jenkins injected secrets will be used.");
        }
    }

    /**
     * Helper method to fetch key from:
     * 1️⃣ Jenkins Environment Variable (highest priority)
     * 2️⃣ secrets.properties (for sensitive)
     * 3️⃣ config.properties (non-sensitive)
     */
    private static String getValue(String key, boolean isSensitive) {
        // Priority 1: ENV/Jenkins injected variables
        String envValue = System.getenv(key);
        if (envValue != null && !envValue.trim().isEmpty()) {
            System.out.println((isSensitive ? "🔐" : "🔑") + " Loaded from ENV: " + key);
            return envValue;
        }

        // Priority 2: secrets.properties (if sensitive)
        if (isSensitive) {
            String secretValue = secretProperties.getProperty(key);
            if (secretValue != null && !secretValue.trim().isEmpty()) {
                System.out.println("🔐 Loaded from secretsConfig.properties: " + key);
                return secretValue;
            }
        }

        // Priority 3: config.properties (non-sensitive)
        String configValue = configProperties.getProperty(key);
        if (configValue != null && !configValue.trim().isEmpty()) {
            System.out.println((isSensitive ? "🔐" : "📖") + " Loaded from config.properties: " + key);
            return configValue;
        }

        throw new RuntimeException("❌ Configuration key '" + key + "' not found in ENV, secretsConfig.properties, or config.properties");
    }

    // Non-sensitive getters
    public static String getBrowser() {
        return getValue("browser", false);
    }

    public static String getBaseUrl() {
        return getValue("baseUrl", false);
    }

    public static int getTimeout() {
        return Integer.parseInt(getValue("timeout", false));
    }

    public static String getBrowserSize() {
        return getValue("browserSize", false);
    }

    public static String getBrowserVersion() {
        return getValue("browserVersion", false);
    }

    public static String getTesterName() {
        return getValue("testerName", false);
    }

    public static String getReportPath() {
        return getValue("reportPath", false);
    }

    public static String getEnvironment() {
        return getValue("environment", false);
    }

    public static boolean isHeadless() {
        return Boolean.parseBoolean(getValue("headless", false));
    }

    // Sensitive getters
    public static String getLoginEmail() {
        return getValue("loginUserEmail", true);
    }

    public static String getLoginPass() {
        return getValue("loginPassword", true);
    }

    public static String getDbUrl() {
        return getValue("dbURL", true);
    }

    public static String getDbUsername() {
        return getValue("dbUsername", true);
    }

    public static String getDbPassword() {
        return getValue("dbPassword", true);
    }
}
