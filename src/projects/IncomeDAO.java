package projects;

import connection.SQLConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDate;

public class IncomeDAO {
    public boolean addIncome(double amount, String source, LocalDate date) {
        String sql = "INSERT INTO income (amount, source, date) VALUES (?, ?, ?)";
        try (Connection conn = SQLConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setDouble(1, amount);
            stmt.setString(2, source);
            stmt.setDate(3, java.sql.Date.valueOf(date));
            int rowsInserted = stmt.executeUpdate();
            return rowsInserted > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
} 