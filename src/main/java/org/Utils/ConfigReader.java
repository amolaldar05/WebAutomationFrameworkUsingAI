package org.Utils;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class ConfigReader {

    private static Properties properties;

    static {
        properties = new Properties();
        try {
            String configFilePath = System.getProperty("user.dir") + "/src/main/java/org/resources/config.properties";
            FileInputStream fis = new FileInputStream(configFilePath);
            properties.load(fis);
            fis.close();
        } catch (IOException e) {
            System.err.println("⚠️ Could not load config.properties file: " + e.getMessage());
            System.err.println("👉 Assuming environment variables will be used for configuration.");
        }
    }

    // Helper: Get value from ENV or fallback to config.properties
    private static String getValue(String key, boolean isSensitive) {
        String envValue = System.getenv(key); // Jenkins ENV variable
        if (envValue != null && !envValue.trim().isEmpty()) {
            if (!isSensitive) {
                System.out.println("🔑 Loaded from ENV: " + key + " = " + envValue);
            } else {
                System.out.println("🔐 Loaded sensitive value from ENV: " + key);
            }
            return envValue;
        }

        String propValue = properties.getProperty(key);
        if (propValue != null && !propValue.trim().isEmpty()) {
            if (!isSensitive) {
                System.out.println("📖 Loaded from config.properties: " + key + " = " + propValue);
            } else {
                System.out.println("🔐 Loaded sensitive value from config.properties: " + key);
            }
            return propValue;
        }

        throw new RuntimeException("❌ Configuration key '" + key + "' not found in ENV or config.properties");
    }

    // Browser config
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

    // Extent report
    public static String getTesterName() {
        return getValue("testerName", false);
    }

    public static String getReportPath() {
        return getValue("reportPath", false);
    }

    public static String getEnvironment() {
        return getValue("environment", false);
    }

    // Sensitive data
    public static String getLoginEmail() {
        return getValue("loginUserEmail", true);
    }

    public static String getLoginPass() {
        return getValue("loginPassword", true);
    }

    public static String getDbUrl() {
        return getValue("dbURL", false);
    }

    public static String getDbUsername() {
        return getValue("dbUsername", true);
    }

    public static String getDbPassword() {
        return getValue("dbPassword", true);
    }

    public static boolean isHeadless() {
        return Boolean.parseBoolean(getValue("headless", false));
    }
}
