package projects;

import java.sql.Date;
import java.util.List;

public class ExpenseManager {
    private ExpenseDAO dao = new ExpenseDAO();

    public boolean recordExpense(double amount, String category, Date date, int userId) {
        Expense expense = new Expense(amount, category, date, userId);
        return dao.addExpense(expense);
    }

    public List<Expense> getExpensesForUser(int userId) {
        return dao.getExpensesByUserId(userId);
    }
}
