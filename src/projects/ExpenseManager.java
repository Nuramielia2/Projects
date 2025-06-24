package projects;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class ExpenseManager {
    private List<Expense> expenses = new ArrayList<>();
    private Scanner scanner = new Scanner(System.in);

    public void recordExpense() {
        System.out.print("Enter expense amount: $");
        double amount = getValidAmount();
        System.out.print("Enter category: ");
        String category = scanner.nextLine().trim();
        System.out.print("Enter date (YYYY-MM-DD) or press Enter for today: ");
        LocalDate date = getValidDate();
        expenses.add(new Expense(amount, category, date));
        // Save to database
        ExpenseDAO expenseDAO = new ExpenseDAO();
        boolean dbSuccess = expenseDAO.addExpense(amount, category, date);
        if (dbSuccess) {
            System.out.println("✓ Expense recorded and saved to database.");
        } else {
            System.out.println("✗ Error saving expense to database.");
        }
    }

    public void viewAllExpenses() {
        if (expenses.isEmpty()) {
            System.out.println("No expenses recorded.");
            return;
        }
        for (Expense e : expenses) System.out.println(e);
    }

    public void editExpense() {
        viewAllExpenses();
        System.out.print("Enter ID to edit: ");
        int id = getValidId();
        Expense e = findById(id);
        if (e == null) {
            System.out.println("Not found.");
            return;
        }
        System.out.print("New amount (Enter to keep): ");
        String amt = scanner.nextLine().trim();
        if (!amt.isEmpty()) e.setAmount(Double.parseDouble(amt));
        System.out.print("New category (Enter to keep): ");
        String cat = scanner.nextLine().trim();
        if (!cat.isEmpty()) e.setCategory(cat);
        System.out.print("New date (YYYY-MM-DD, Enter to keep): ");
        String dt = scanner.nextLine().trim();
        if (!dt.isEmpty()) e.setDate(LocalDate.parse(dt));
        System.out.println("✓ Expense updated.");
    }

    private double getValidAmount() {
        while (true) {
            try {
                double a = Double.parseDouble(scanner.nextLine().trim());
                if (a > 0) return a;
            } catch (Exception ignored) {}
            System.out.print("Invalid. Enter amount: $");
        }
    }
    private LocalDate getValidDate() {
        String in = scanner.nextLine().trim();
        if (in.isEmpty()) return LocalDate.now();
        try { return LocalDate.parse(in); } catch (Exception e) { return LocalDate.now(); }
    }
    private int getValidId() {
        while (true) {
            try { return Integer.parseInt(scanner.nextLine().trim()); } catch (Exception ignored) {}
            System.out.print("Invalid. Enter ID: ");
        }
    }
    private Expense findById(int id) {
        for (Expense e : expenses) if (e.getId() == id) return e;
        return null;
    }
    
    /**
     * Returns the list of all expenses
     * @return List of expenses
     */
    public List<Expense> getExpenses() {
        return expenses;
    }
} 