package org.Utils;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class MySQLUtils {
    /*private static final String URL = ConfigReader.getDbUrl();
    private static final String USER = ConfigReader.getDbUsername();
    private static final String PASSWORD = ConfigReader.getDbPassword();

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    public static ResultSet executeQuery(String query) throws SQLException {
        Connection conn = getConnection();
        Statement stmt = conn.createStatement();
        return stmt.executeQuery(query);
    }

    public static void close(Connection conn, Statement stmt, ResultSet rs) {
        try { if (rs != null) rs.close(); } catch (Exception e) {}
        try { if (stmt != null) stmt.close(); } catch (Exception e) {}
        try { if (conn != null) conn.close(); } catch (Exception e) {}
    }*/

    private static HikariDataSource dataSource;

    static {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(ConfigReader.getDbUrl());
        config.setUsername(ConfigReader.getDbUsername());
        config.setPassword(ConfigReader.getDbPassword());
        config.setMaximumPoolSize(10); // Allows parallel threads
        config.setConnectionTimeout(5000); // 5 sec timeout
        config.setIdleTimeout(60000);
        config.setMaxLifetime(300000);

        dataSource = new HikariDataSource(config);
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
        try { if (rs != null) rs.close(); } catch (Exception ignored) {}
        try { if (stmt != null) stmt.close(); } catch (Exception ignored) {}
        try { if (conn != null) conn.close(); } catch (Exception ignored) {}
    }

    public static void shutdownPool() {
        if (dataSource != null) {
            dataSource.close();
        }
    }


    public static void createUsersTableAndInsertSample(String firstName, String lastName, String email, String phone, String occupation, String gender, String password, boolean registrationSuccess) throws SQLException {
        String createUserTableSQL = "CREATE TABLE IF NOT EXISTS users (" +
                "id INT AUTO_INCREMENT PRIMARY KEY," +
                "first_name VARCHAR(50)," +
                "last_name VARCHAR(50)," +
                "email VARCHAR(100)," +
                "phone VARCHAR(20)," +
                "occupation VARCHAR(50)," +
                "gender VARCHAR(10)," +
                "password VARCHAR(100)" +
                ")";
        String createCredTableSQL = "CREATE TABLE IF NOT EXISTS valid_credentials (" +
                "id INT AUTO_INCREMENT PRIMARY KEY," +
                "username VARCHAR(100)," +
                "password VARCHAR(100)" +
                ")";
        String insertUserSQL = String.format(
            "INSERT INTO users (first_name, last_name, email, phone, occupation, gender, password) VALUES ('%s', '%s', '%s', '%s', '%s', '%s', '%s')",
            firstName, lastName, email, phone, occupation, gender, password
        );
        String insertCredSQL = String.format(
            "INSERT INTO valid_credentials (username, password) VALUES ('%s', '%s')",
            email, password
        );
        try (Connection conn = getConnection(); Statement stmt = conn.createStatement()) {
            stmt.execute(createUserTableSQL);
            stmt.execute(createCredTableSQL);
            if (registrationSuccess) {
                stmt.execute(insertUserSQL);
                stmt.execute(insertCredSQL);
//                truncateTableIfTenDatas("users");
//                truncateTableIfTenDatas("valid_credentials");
            }
        }
    }

    /*public static void truncateTableIfTenDatas(String tableName) throws SQLException {
        String countQuery = "SELECT COUNT(*) FROM " + tableName;
        String truncateQuery = "TRUNCATE TABLE " + tableName;
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(countQuery)) {
            if (rs.next() && rs.getInt(1) >= 10) {
                stmt.execute(truncateQuery);
            }
        }
    }*/
}
