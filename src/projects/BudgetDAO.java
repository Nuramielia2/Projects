package projects;

import connection.SQLConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDate;

public class BudgetDAO {
    public boolean addBudget(double amount, String period, LocalDate start, LocalDate end, String description) {
        String sql = "INSERT INTO budget (amount, period, start, end, description) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = SQLConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setDouble(1, amount);
            stmt.setString(2, period);
            stmt.setDate(3, java.sql.Date.valueOf(start));
            stmt.setDate(4, java.sql.Date.valueOf(end));
            stmt.setString(5, description);
            int rowsInserted = stmt.executeUpdate();
            return rowsInserted > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
} 