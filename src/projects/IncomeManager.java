package projects;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

/**
 * Manages income records for the personal budget manager.
 * Handles adding, viewing, editing, and deleting income records.
 * 
 * @author Amielia
 */
public class IncomeManager {
    private List<Income> incomeRecords;
    private Scanner scanner;
    
    /**
     * Constructor initializes the income manager with an empty list of records.
     */
    public IncomeManager() {
        this.incomeRecords = new ArrayList<>();
        this.scanner = new Scanner(System.in);
    }
    
    /**
     * Records a new income entry.
     * Prompts user for amount, source, and date, then saves the record.
     * 
     * @return true if income was successfully recorded, false otherwise
     */
    public boolean recordIncome() {
        System.out.println("\n=== Record Income ===");
        
        try {
            // Get amount
            System.out.print("Enter income amount: $");
            double amount = getValidAmount();
            
            // Get source
            System.out.print("Enter income source: ");
            String source = getValidSource();
            
            // Get date
            System.out.print("Enter income date (YYYY-MM-DD) or press Enter for today: ");
            LocalDate date = getValidDate();
            
            // Create and save income record
            Income income = new Income(amount, source, date);
            incomeRecords.add(income);
            
            System.out.println("✓ Income recorded successfully!");
            System.out.println(income);
            
            return true;
            
        } catch (Exception e) {
            System.out.println("✗ Error recording income: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Displays all income records.
     */
    public void viewAllIncome() {
        System.out.println("\n=== All Income Records ===");
        
        if (incomeRecords.isEmpty()) {
            System.out.println("No income records found.");
            return;
        }
        
        for (Income income : incomeRecords) {
            System.out.println(income);
        }
        
        // Show total
        double total = incomeRecords.stream()
                                   .mapToDouble(Income::getAmount)
                                   .sum();
        System.out.printf("\nTotal Income: $%.2f\n", total);
    }
    
    /**
     * Allows user to edit an existing income record.
     */
    public void editIncome() {
        System.out.println("\n=== Edit Income Record ===");
        
        if (incomeRecords.isEmpty()) {
            System.out.println("No income records to edit.");
            return;
        }
        
        // Show all records for selection
        viewAllIncome();
        
        System.out.print("\nEnter the ID of the income record to edit: ");
        int id = getValidId();
        
        Income incomeToEdit = findIncomeById(id);
        if (incomeToEdit == null) {
            System.out.println("✗ Income record with ID " + id + " not found.");
            return;
        }
        
        System.out.println("Editing: " + incomeToEdit);
        
        try {
            // Edit amount
            System.out.print("Enter new amount (or press Enter to keep current): ");
            String amountInput = scanner.nextLine().trim();
            if (!amountInput.isEmpty()) {
                double newAmount = Double.parseDouble(amountInput);
                if (newAmount > 0) {
                    incomeToEdit.setAmount(newAmount);
                }
            }
            
            // Edit source
            System.out.print("Enter new source (or press Enter to keep current): ");
            String newSource = scanner.nextLine().trim();
            if (!newSource.isEmpty()) {
                incomeToEdit.setSource(newSource);
            }
            
            // Edit date
            System.out.print("Enter new date (YYYY-MM-DD) (or press Enter to keep current): ");
            String dateInput = scanner.nextLine().trim();
            if (!dateInput.isEmpty()) {
                LocalDate newDate = LocalDate.parse(dateInput);
                incomeToEdit.setDate(newDate);
            }
            
            System.out.println("✓ Income record updated successfully!");
            System.out.println(incomeToEdit);
            
        } catch (NumberFormatException e) {
            System.out.println("✗ Invalid amount format.");
        } catch (DateTimeParseException e) {
            System.out.println("✗ Invalid date format. Use YYYY-MM-DD.");
        } catch (Exception e) {
            System.out.println("✗ Error updating income record: " + e.getMessage());
        }
    }
    
    /**
     * Allows user to delete an income record.
     */
    public void deleteIncome() {
        System.out.println("\n=== Delete Income Record ===");
        
        if (incomeRecords.isEmpty()) {
            System.out.println("No income records to delete.");
            return;
        }
        
        // Show all records for selection
        viewAllIncome();
        
        System.out.print("\nEnter the ID of the income record to delete: ");
        int id = getValidId();
        
        Income incomeToDelete = findIncomeById(id);
        if (incomeToDelete == null) {
            System.out.println("✗ Income record with ID " + id + " not found.");
            return;
        }
        
        System.out.println("Are you sure you want to delete: " + incomeToDelete + "?");
        System.out.print("Enter 'yes' to confirm: ");
        String confirmation = scanner.nextLine().trim().toLowerCase();
        
        if (confirmation.equals("yes")) {
            incomeRecords.remove(incomeToDelete);
            System.out.println("✓ Income record deleted successfully!");
        } else {
            System.out.println("Deletion cancelled.");
        }
    }
    
    /**
     * Shows income records for a specific date range.
     */
    public void viewIncomeByDateRange() {
        System.out.println("\n=== View Income by Date Range ===");
        
        if (incomeRecords.isEmpty()) {
            System.out.println("No income records found.");
            return;
        }
        
        try {
            System.out.print("Enter start date (YYYY-MM-DD): ");
            LocalDate startDate = LocalDate.parse(scanner.nextLine().trim());
            
            System.out.print("Enter end date (YYYY-MM-DD): ");
            LocalDate endDate = LocalDate.parse(scanner.nextLine().trim());
            
            List<Income> filteredIncome = incomeRecords.stream()
                .filter(income -> !income.getDate().isBefore(startDate) && 
                                !income.getDate().isAfter(endDate))
                .collect(Collectors.toList());
            
            if (filteredIncome.isEmpty()) {
                System.out.println("No income records found for the specified date range.");
                return;
            }
            
            System.out.println("\nIncome records from " + startDate + " to " + endDate + ":");
            for (Income income : filteredIncome) {
                System.out.println(income);
            }
            
            double total = filteredIncome.stream()
                                       .mapToDouble(Income::getAmount)
                                       .sum();
            System.out.printf("\nTotal Income for period: $%.2f\n", total);
            
        } catch (DateTimeParseException e) {
            System.out.println("✗ Invalid date format. Use YYYY-MM-DD.");
        }
    }
    
    // Helper methods
    private double getValidAmount() {
        while (true) {
            try {
                String input = scanner.nextLine().trim();
                double amount = Double.parseDouble(input);
                if (amount > 0) {
                    return amount;
                } else {
                    System.out.print("Amount must be positive. Enter amount: $");
                }
            } catch (NumberFormatException e) {
                System.out.print("Invalid amount. Enter amount: $");
            }
        }
    }
    
    private String getValidSource() {
        while (true) {
            String source = scanner.nextLine().trim();
            if (!source.isEmpty()) {
                return source;
            } else {
                System.out.print("Source cannot be empty. Enter source: ");
            }
        }
    }
    
    private LocalDate getValidDate() {
        while (true) {
            String input = scanner.nextLine().trim();
            if (input.isEmpty()) {
                return LocalDate.now();
            }
            
            try {
                return LocalDate.parse(input);
            } catch (DateTimeParseException e) {
                System.out.print("Invalid date format. Use YYYY-MM-DD or press Enter for today: ");
            }
        }
    }
    
    private int getValidId() {
        while (true) {
            try {
                return Integer.parseInt(scanner.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.print("Invalid ID. Enter a number: ");
            }
        }
    }
    
    private Income findIncomeById(int id) {
        return incomeRecords.stream()
                           .filter(income -> income.getId() == id)
                           .findFirst()
                           .orElse(null);
    }
    
    /**
     * Returns the list of all income records.
     * 
     * @return List of income records
     */
    public List<Income> getIncomeRecords() {
        return new ArrayList<>(incomeRecords);
    }
    
    /**
     * Returns the total income amount.
     * 
     * @return Total income amount
     */
    public double getTotalIncome() {
        return incomeRecords.stream()
                           .mapToDouble(Income::getAmount)
                           .sum();
    }
} 