package org.Utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Paths;
import java.util.Map;

public class SeleniumGridManager {

    private static Process hubProcess;
    private static Process nodeProcess;

    // Auto-detect paths relative to project root
    private static final String PROJECT_ROOT = System.getProperty("user.dir"); // Project base folder
    private static final String DRIVERS_FOLDER = PROJECT_ROOT + "/src/main/java/org/drivers";
    private static final String SELENIUM_JAR_NAME = "selenium-server-4.34.0.jar";
    private static final String SELENIUM_JAR_PATH = Paths.get(DRIVERS_FOLDER, SELENIUM_JAR_NAME).toString();

    private static final String HUB_PORT = "4444";

    /**
     * Start Selenium Grid Hub locally
     */
    public static void startHub() {
        try {
            System.out.println("🚀 Starting Selenium Grid Hub on port " + HUB_PORT + "...");
            System.out.println("📂 Selenium JAR Path: " + SELENIUM_JAR_PATH);
            System.out.println("📂 Drivers Folder Path: " + DRIVERS_FOLDER);

            ProcessBuilder pb = new ProcessBuilder(
                    "java", "-jar", SELENIUM_JAR_PATH, "hub",
                    "--port", HUB_PORT
            );

            // Add drivers folder to system PATH
            addDriversToSystemPath(pb);

            pb.redirectErrorStream(true);
            hubProcess = pb.start();

            printProcessLogs(hubProcess);
            System.out.println("✅ Selenium Grid Hub started at http://localhost:" + HUB_PORT);
        } catch (Exception e) {
            throw new RuntimeException("❌ Failed to start Selenium Grid Hub: " + e.getMessage(), e);
        }
    }

    /**
     * Start Selenium Grid Node locally and register to Hub
     * Decides dynamically whether to use Selenium Manager based on internet availability
     */
    public static void startNode() {
        try {
            System.out.println("🚀 Starting Selenium Grid Node with driver detection...");
            System.out.println("📂 Selenium JAR Path: " + SELENIUM_JAR_PATH);
            System.out.println("📂 Drivers Folder Path: " + DRIVERS_FOLDER);

            boolean internetAvailable = isInternetAvailable();
            String seleniumManagerFlag = internetAvailable ? "true" : "false";

            System.out.println(internetAvailable
                    ? "🌐 Internet available: Using Selenium Manager for dynamic driver management."
                    : "📴 No internet: Falling back to local drivers in offline mode.");

            ProcessBuilder pb = new ProcessBuilder(
                    "java", "-jar", SELENIUM_JAR_PATH, "node",
                    "--detect-drivers", "true",
                    "--selenium-manager", seleniumManagerFlag
            );

            // Always add drivers folder to PATH for offline mode
            addDriversToSystemPath(pb);

            pb.redirectErrorStream(true);
            nodeProcess = pb.start();

            printProcessLogs(nodeProcess);
            System.out.println("✅ Selenium Grid Node registered to Hub.");
        } catch (Exception e) {
            throw new RuntimeException("❌ Failed to start Selenium Grid Node: " + e.getMessage(), e);
        }
    }

    /**
     * Check if the system has internet connectivity
     */
    private static boolean isInternetAvailable() {
        try {
            URL url = new URL("https://www.google.com/");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setConnectTimeout(2000); // 2 seconds timeout
            connection.connect();
            return connection.getResponseCode() == 200;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Add drivers folder to the system PATH variable
     */
    private static void addDriversToSystemPath(ProcessBuilder pb) {
        File driversDir = new File(DRIVERS_FOLDER);
        if (driversDir.exists() && driversDir.isDirectory()) {
            Map<String, String> env = pb.environment();
            String currentPath = env.get("PATH");
            env.put("PATH", DRIVERS_FOLDER + File.pathSeparator + currentPath);
            System.out.println("✅ Drivers folder added to system PATH.");
        } else {
            throw new RuntimeException("❌ Drivers folder not found at: " + DRIVERS_FOLDER);
        }
    }

    /**
     * Stop Selenium Grid Hub
     */
    public static void stopHub() {
        if (hubProcess != null && hubProcess.isAlive()) {
            System.out.println("🛑 Stopping Selenium Grid Hub...");
            hubProcess.destroy();
            System.out.println("✅ Selenium Grid Hub stopped.");
        } else {
            System.out.println("⚠️ Hub process is not running.");
        }
    }

    /**
     * Stop Selenium Grid Node
     */
    public static void stopNode() {
        if (nodeProcess != null && nodeProcess.isAlive()) {
            System.out.println("🛑 Stopping Selenium Grid Node...");
            nodeProcess.destroy();
            System.out.println("✅ Selenium Grid Node stopped.");
        } else {
            System.out.println("⚠️ Node process is not running.");
        }
    }

    /**
     * Print logs from the process
     */
    private static void printProcessLogs(Process process) {
        new Thread(() -> {
            try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(process.getInputStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    System.out.println("[Grid] " + line);
                }
            } catch (Exception ignored) {
            }
        }).start();
    }
}
