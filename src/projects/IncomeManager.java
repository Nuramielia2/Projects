package projects;

import java.sql.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Scanner;

public class IncomeManager {
    private Scanner scanner;
    private IncomeDAO incomeDAO;

    public IncomeManager() {
        this.scanner = new Scanner(System.in);
        this.incomeDAO = new IncomeDAO();
    }

    public boolean recordIncome(int userId) {
        System.out.println("\n=== Record Income ===");

        try {
            System.out.print("Enter income amount: $");
            double amount = getValidAmount();

            System.out.print("Enter income source: ");
            String source = getValidSource();

            System.out.print("Enter income date (YYYY-MM-DD) or press Enter for today: ");
            Date date = getValidDate();

            Income income = new Income(amount, source, date, userId);
            boolean dbSuccess = incomeDAO.addIncome(income);
            if (dbSuccess) {
                System.out.println("✓ Income recorded successfully!");
            } else {
                System.out.println("✗ Error saving income to database.");
            }

            return dbSuccess;
        } catch (Exception e) {
            System.out.println("✗ Error recording income: " + e.getMessage());
            return false;
        }
    }

    public void viewAllIncome(int userId) {
        System.out.println("\n=== All Income Records ===");
        List<Income> incomeRecords = incomeDAO.getAllIncomeForUser(userId);

        if (incomeRecords.isEmpty()) {
            System.out.println("No income records found.");
            return;
        }

        double total = 0;
        for (Income income : incomeRecords) {
            System.out.println(income);
            total += income.getAmount();
        }

        System.out.printf("\nTotal Income: $%.2f\n", total);
    }

    private double getValidAmount() {
        while (true) {
            try {
                String input = scanner.nextLine().trim();
                double amount = Double.parseDouble(input);
                if (amount > 0) return amount;
                System.out.print("Amount must be positive. Enter amount: $");
            } catch (NumberFormatException e) {
                System.out.print("Invalid amount. Enter amount: $");
            }
        }
    }

    private String getValidSource() {
        while (true) {
            String source = scanner.nextLine().trim();
            if (!source.isEmpty()) return source;
            System.out.print("Source cannot be empty. Enter source: ");
        }
    }

    private Date getValidDate() {
        while (true) {
            String input = scanner.nextLine().trim();
            if (input.isEmpty()) return new Date(System.currentTimeMillis());
            try {
                SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
                return new Date(formatter.parse(input).getTime());
            } catch (ParseException e) {
                System.out.print("Invalid date format. Use YYYY-MM-DD or press Enter for today: ");
            }
        }
    }
}
