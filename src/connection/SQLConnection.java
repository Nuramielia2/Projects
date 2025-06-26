/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package connection;

import java.sql.DriverManager;
import java.sql.SQLException;

/**
 *
 * @author Nikeisha
 */
public class SQLConnection {
    private static SQLConnection instance;
    private String url = "jdbc:mysql://localhost:3306/budgetmandb?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";
 // Full URL with database name
    private String username = "root";
    private String password = "Upm20222023!";
    private java.sql.Connection connection;    
    
    private SQLConnection() { // Private constructor
        try {
            Class.forName("com.mysql.cj.jdbc.Driver"); // MySQL driver
            connection = DriverManager.getConnection(url, username, password);
        } catch (ClassNotFoundException e) {
            System.err.println("Driver not found: " + e); // Use System.err for errors
            e.printStackTrace();
            // Consider throwing a custom exception here to handle this more gracefully
            throw new RuntimeException("Failed to load database driver", e);
        } catch (SQLException e) {
            System.err.println("Connection error: " + e);
            e.printStackTrace();
            // Consider throwing a custom exception here
            throw new RuntimeException("Failed to connect to database", e);
        }
    }
    public java.sql.Connection getConnection() {
    return connection;
}


    public static SQLConnection getInstance() {
        if (instance == null) {
            instance = new SQLConnection();
        }
        return instance;
    }
    
    public void closeConnection() {
        try {
            if (connection != null) {
                connection.close();
            }
        } catch (SQLException e) {
            System.err.println("Error closing connection: " + e);
            e.printStackTrace();
        } finally {
            instance = null; // Reset instance on close to allow re-connection
        }
    }
}