package org.Utils;

import java.sql.*;
import java.util.*;

public class DatabaseUtils {

    public static List<Map<String, Object>> readDataFromDatabase(
            String jdbcUrl, String username, String password, String query) {
        List<Map<String, Object>> resultList = new ArrayList<>();
        try (Connection conn = DriverManager.getConnection(jdbcUrl, username, password);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            ResultSetMetaData metaData = rs.getMetaData();
            int columnCount = metaData.getColumnCount();

            while (rs.next()) {
                Map<String, Object> row = new HashMap<>();
                for (int i = 1; i <= columnCount; i++) {
                    row.put(metaData.getColumnName(i), rs.getObject(i));
                }
                resultList.add(row);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return resultList;
    }
}
