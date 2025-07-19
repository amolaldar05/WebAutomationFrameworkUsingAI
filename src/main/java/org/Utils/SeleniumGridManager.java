package org.Utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class SeleniumGridManager {

    private static Process hubProcess;
    private static Process nodeProcess;

    /**
     * Starts the Selenium Grid Hub
     *
     * @param seleniumServerJarPath Path to selenium-server-<version>.jar
     * @param hubPort               Port where Hub will run
     */
    public static void startHub(String seleniumServerJarPath, int hubPort) {
        try {
            System.out.println("🟢 Starting Selenium Grid Hub on port " + hubPort + "...");
            ProcessBuilder hubBuilder = new ProcessBuilder(
                    "java", "-jar", seleniumServerJarPath,
                    "hub", "--port", String.valueOf(hubPort)
            );
            hubBuilder.redirectOutput(ProcessBuilder.Redirect.INHERIT);
            hubBuilder.redirectError(ProcessBuilder.Redirect.INHERIT);
            hubProcess = hubBuilder.start();

            // Wait for Hub to become ready
            String hubUrl = "http://localhost:" + hubPort + "/status";
            waitForGridReady(hubUrl, 30); // Wait up to 30 seconds

        } catch (IOException | InterruptedException e) {
            throw new RuntimeException("❌ Failed to start Hub on port " + hubPort, e);
        }
    }

    /**
     * Starts the Selenium Grid Node
     *
     * @param seleniumServerJarPath Path to selenium-server-<version>.jar
     * @param hubIp                 IP Address of the Hub (e.g., localhost or 192.168.x.x)
     * @param hubPort               Port of the Hub
     */
    public static void startNode(String seleniumServerJarPath, String hubIp, int hubPort) {
        try {
            String hubUrl = "http://" + hubIp + ":" + hubPort;
            System.out.println("🟡 Starting Selenium Grid Node connecting to Hub at " + hubUrl + "...");
            ProcessBuilder nodeBuilder = new ProcessBuilder(
                    "java", "-jar", seleniumServerJarPath,
                    "node",
                    "--detect-drivers", "true",
                    "--hub", hubUrl
            );
            nodeBuilder.redirectOutput(ProcessBuilder.Redirect.INHERIT);
            nodeBuilder.redirectError(ProcessBuilder.Redirect.INHERIT);
            nodeProcess = nodeBuilder.start();

            // Wait for Node to register with Hub
            waitForNodeReady(hubUrl + "/status", 30); // Wait up to 30 seconds

        } catch (IOException | InterruptedException e) {
            throw new RuntimeException("❌ Failed to start Node for Hub at " + hubIp + ":" + hubPort, e);
        }
    }

    /**
     * Stops the Selenium Grid Hub and Node processes if running.
     */
    public static void stopGrid() {
        if (nodeProcess != null && nodeProcess.isAlive()) {
            System.out.println("🔴 Stopping Selenium Grid Node...");
            nodeProcess.destroy();
        }
        if (hubProcess != null && hubProcess.isAlive()) {
            System.out.println("🔴 Stopping Selenium Grid Hub...");
            hubProcess.destroy();
        }
    }

    /**
     * Waits until the Selenium Grid Hub is ready.
     *
     * @param statusUrl URL to check (e.g., http://localhost:4444/status)
     * @param timeoutSeconds Timeout in seconds
     */
    private static void waitForGridReady(String statusUrl, int timeoutSeconds) throws InterruptedException {
        System.out.println("⏳ Waiting for Selenium Grid Hub to be ready...");
        boolean isReady = false;
        long startTime = System.currentTimeMillis();

        while ((System.currentTimeMillis() - startTime) < timeoutSeconds * 1000) {
            if (checkGridStatus(statusUrl)) {
                isReady = true;
                break;
            }
            Thread.sleep(2000); // Wait 2 seconds before retry
        }

        if (!isReady) {
            throw new RuntimeException("❌ Selenium Grid Hub did not become ready within " + timeoutSeconds + " seconds.");
        }
        System.out.println("✅ Selenium Grid Hub is ready.");
    }

    /**
     * Waits until the Selenium Grid Node registers with the Hub.
     *
     * @param statusUrl URL to check (e.g., http://localhost:4444/status)
     * @param timeoutSeconds Timeout in seconds
     */
    private static void waitForNodeReady(String statusUrl, int timeoutSeconds) throws InterruptedException {
        System.out.println("⏳ Waiting for Selenium Grid Node to register...");
        boolean isReady = false;
        long startTime = System.currentTimeMillis();

        while ((System.currentTimeMillis() - startTime) < timeoutSeconds * 1000) {
            if (checkGridStatus(statusUrl)) {
                isReady = true;
                break;
            }
            Thread.sleep(2000); // Wait 2 seconds before retry
        }

        if (!isReady) {
            throw new RuntimeException("❌ Selenium Grid Node did not register within " + timeoutSeconds + " seconds.");
        }
        System.out.println("✅ Selenium Grid Node is registered.");
    }

    /**
     * Checks if the Selenium Grid is ready.
     *
     * @param statusUrl URL to check
     * @return true if Grid reports ready, false otherwise
     */
    private static boolean checkGridStatus(String statusUrl) {
        try {
            URL url = new URL(statusUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(2000);
            connection.connect();

            if (connection.getResponseCode() == 200) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String line;
                StringBuilder response = new StringBuilder();

                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
                reader.close();

                return response.toString().contains("\"ready\":true");
            }
        } catch (IOException ignored) {
            // Ignore connection errors during retries
        }
        return false;
    }
}