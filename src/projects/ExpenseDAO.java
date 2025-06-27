package projects;

import connection.SQLConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ExpenseDAO {

    public boolean addExpense(Expense expense) {
        String sql = "INSERT INTO expense (amount, category, date, userId) VALUES (?, ?, ?, ?)";
        try (Connection conn = SQLConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setDouble(1, expense.getAmount());
            stmt.setString(2, expense.getCategory());
            stmt.setDate(3, expense.getDate());
            stmt.setInt(4, expense.getUserId());

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<Expense> getExpensesByUserId(int userId) {
        List<Expense> expenses = new ArrayList<>();
        String sql = "SELECT id, amount, category, date FROM expense WHERE userId = ?";

        try (Connection conn = SQLConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                int id = rs.getInt("id");
                double amount = rs.getDouble("amount");
                String category = rs.getString("category");
                Date date = rs.getDate("date");

                expenses.add(new Expense(id, amount, category, date, userId));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return expenses;
    }
}
