package org.Utils;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class ConfigReader {

    private static Properties properties;

    static {
        try {
            String configFilePath = System.getProperty("user.dir") + "/src/main/java/org/resources/config.properties";
            FileInputStream fis = new FileInputStream(configFilePath);
            properties = new Properties();
            properties.load(fis);
            fis.close();
        } catch (IOException e) {
            System.err.println("❌ Failed to load configuration file: " + e.getMessage());
            throw new RuntimeException(e);
        }
    }

    // Method to get any property by key
    public static String getProperty(String key) {
        String value = properties.getProperty(key);
        if (value == null) {
            throw new RuntimeException("❌ Property '" + key + "' not found in config.properties");
        }
        return value;
    }

    // Shortcut methods
    public static String getBrowser() {
        return getProperty("browser");
    }

    public static String getTesterName() {
        return getProperty("testerName");
    }

    public static String getBaseUrl() {
        return getProperty("baseUrl");
    }

    public static String getEnvironment() {
        return getProperty("environment");
    }

    public static String getReportPath() {
        return System.getProperty("reportPath" );
    }
    public static int getTimeout() {
        String timeoutStr = getProperty("timeout");
        try {
            return Integer.parseInt(timeoutStr);
        } catch (NumberFormatException e) {
            throw new RuntimeException("❌ Invalid timeout value in config.properties: " + timeoutStr, e);
        }
    }
}
