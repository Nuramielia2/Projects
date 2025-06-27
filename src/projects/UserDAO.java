package projects;

import connection.SQLConnection;
import java.sql.*;

public class UserDAO {

    /**
     * Registers a new user in the database.
     */
    public boolean addUser(String username, String password, String email) {
        String sql = "INSERT INTO users (username, password, email) VALUES (?, ?, ?)";
        try (Connection conn = SQLConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, username);
            stmt.setString(2, password);
            stmt.setString(3, email);

            int rowsInserted = stmt.executeUpdate();
            return rowsInserted > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Finds and returns a user from the database using username and password.
     */
    public User findUser(String username, String password) {
        String sql = "SELECT userid, username, password, email FROM users WHERE username = ? AND password = ?";
        try (Connection conn = SQLConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, username);
            stmt.setString(2, password);

            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                int userId = rs.getInt("userid");
                String email = rs.getString("email");

                return new User(userId, username, password, email);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}
