/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package connection;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class SQLConnection {
    private static SQLConnection instance;

    private final String url = "jdbc:mysql://localhost:3306/budgetmandb?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";
    private final String username = "root";
    private final String password = "NFAxpsql15";

    private SQLConnection() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver"); // Load MySQL driver
        } catch (ClassNotFoundException e) {
            System.err.println("❌ Driver not found: " + e);
            throw new RuntimeException("Failed to load database driver", e);
        }
    }

    public static SQLConnection getInstance() {
        if (instance == null) {
            instance = new SQLConnection();
        }
        return instance;
    }

    public Connection getConnection() {
        try {
            return DriverManager.getConnection(url, username, password); // Always return a fresh connection
        } catch (SQLException e) {
            System.err.println("❌ Failed to connect to database: " + e);
            throw new RuntimeException("Failed to connect to database", e);
        }
    }
}
