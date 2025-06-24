package projects;

import connection.SQLConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDate;

public class ExpenseDAO {
    public boolean addExpense(double amount, String category, LocalDate date) {
        String sql = "INSERT INTO expense (amount, category, date) VALUES (?, ?, ?)";
        try (Connection conn = SQLConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setDouble(1, amount);
            stmt.setString(2, category);
            stmt.setDate(3, java.sql.Date.valueOf(date));
            int rowsInserted = stmt.executeUpdate();
            return rowsInserted > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
} 