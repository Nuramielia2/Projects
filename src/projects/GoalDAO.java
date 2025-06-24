package projects;

import connection.SQLConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDate;

public class GoalDAO {
    public boolean addGoal(String description, double targetAmount, LocalDate deadline) {
        String sql = "INSERT INTO goals (description, targetamount, deadline) VALUES (?, ?, ?)";
        try (Connection conn = SQLConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, description);
            stmt.setDouble(2, targetAmount);
            stmt.setDate(3, java.sql.Date.valueOf(deadline));
            int rowsInserted = stmt.executeUpdate();
            return rowsInserted > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
} 