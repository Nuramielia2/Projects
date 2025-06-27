package projects;

import connection.SQLConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class IncomeDAO {

    public boolean addIncome(Income income) {
        String sql = "INSERT INTO income (amount, source, date, userId) VALUES (?, ?, ?, ?)";
        try (Connection conn = SQLConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setDouble(1, income.getAmount());
            stmt.setString(2, income.getSource());
            stmt.setDate(3, income.getDate());
            stmt.setInt(4, income.getUserId());
            int rowsInserted = stmt.executeUpdate();
            return rowsInserted > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<Income> getAllIncomeForUser(int userId) {
        List<Income> incomeList = new ArrayList<>();
        String sql = "SELECT id, amount, source, date, userId FROM income WHERE userId = ?";
        try (Connection conn = SQLConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    int id = rs.getInt("id");
                    double amount = rs.getDouble("amount");
                    String source = rs.getString("source");
                    Date date = rs.getDate("date");
                    int uid = rs.getInt("userId");

                    incomeList.add(new Income(id, amount, source, date, uid));
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return incomeList;
    }

    // ✅ New method to calculate total income for a specific user
    public double getTotalIncomeByUser(int userId) {
        double total = 0;
        String sql = "SELECT SUM(amount) FROM income WHERE userId = ?";
        try (Connection conn = SQLConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    total = rs.getDouble(1);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return total;
    }
}
