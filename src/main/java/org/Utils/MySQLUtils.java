package org.Utils;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class MySQLUtils {

    private static HikariDataSource dataSource;

    static {
        try {
            HikariConfig config = new HikariConfig();
            config.setJdbcUrl(ConfigReader.getDbUrl());
            config.setUsername(ConfigReader.getDbUsername());
            config.setPassword(ConfigReader.getDbPassword());
            config.setMaximumPoolSize(10); // Allows parallel threads
            config.setConnectionTimeout(5000); // 5 sec timeout
            config.setIdleTimeout(60000);
            config.setMaxLifetime(300000);

            dataSource = new HikariDataSource(config);
            System.out.println("✅ HikariCP datasource initialized successfully.");
        } catch (Exception e) {
            System.err.println("❌ Failed to initialize HikariCP datasource: " + e.getMessage());
            e.printStackTrace();
            throw new ExceptionInInitializerError(e);
        }
    }

    public static Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }

    public static ResultSet executeQuery(String query) throws SQLException {
        Connection conn = getConnection();
        Statement stmt = conn.createStatement();
        return stmt.executeQuery(query);
    }

    public static void close(Connection conn, Statement stmt, ResultSet rs) {
        try {
            if (rs != null) rs.close();
        } catch (Exception ignored) {}
        try {
            if (stmt != null) stmt.close();
        } catch (Exception ignored) {}
        try {
            if (conn != null) conn.close();
        } catch (Exception ignored) {}
    }

    public static void shutdownPool() {
        if (dataSource != null) {
            dataSource.close();
            System.out.println("🔻 HikariCP datasource pool shut down.");
        }
    }

    public static void createUsersTableAndInsertSample(String firstName, String lastName, String email, String phone,
                                                       String occupation, String gender, String password,
                                                       boolean registrationSuccess) throws SQLException {
        String createUserTableSQL = """
                CREATE TABLE IF NOT EXISTS users (
                    id INT AUTO_INCREMENT PRIMARY KEY,
                    first_name VARCHAR(50),
                    last_name VARCHAR(50),
                    email VARCHAR(100),
                    phone VARCHAR(20),
                    occupation VARCHAR(50),
                    gender VARCHAR(10),
                    password VARCHAR(100)
                )
                """;

        String createCredTableSQL = """
                CREATE TABLE IF NOT EXISTS valid_credentials (
                    id INT AUTO_INCREMENT PRIMARY KEY,
                    username VARCHAR(100),
                    password VARCHAR(100)
                )
                """;

        String insertUserSQL = """
                INSERT INTO users (first_name, last_name, email, phone, occupation, gender, password)
                VALUES (?, ?, ?, ?, ?, ?, ?)
                """;

        String insertCredSQL = """
                INSERT INTO valid_credentials (username, password)
                VALUES (?, ?)
                """;

        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {

            // Create tables if not exist
            stmt.execute(createUserTableSQL);
            stmt.execute(createCredTableSQL);

            if (registrationSuccess) {
                // Insert user record
                try (PreparedStatement psUser = conn.prepareStatement(insertUserSQL);
                     PreparedStatement psCred = conn.prepareStatement(insertCredSQL)) {

                    psUser.setString(1, firstName);
                    psUser.setString(2, lastName);
                    psUser.setString(3, email);
                    psUser.setString(4, phone);
                    psUser.setString(5, occupation);
                    psUser.setString(6, gender);
                    psUser.setString(7, password);
                    psUser.executeUpdate();

                    psCred.setString(1, email);
                    psCred.setString(2, password);
                    psCred.executeUpdate();
                }
                System.out.println("✅ User and credentials inserted successfully.");
            }
        }
    }
}
