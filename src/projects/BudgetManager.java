package projects;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Manages budget records for the personal budget manager.
 * 
 * @author Amielia
 */
public class BudgetManager {
    private List<Budget> budgets = new ArrayList<>();
    
    public void createBudget(double amount, String timePeriod, LocalDate startDate, LocalDate endDate, String description) {
        Budget budget = new Budget(amount, timePeriod, startDate, endDate, description);
        budgets.add(budget);
        System.out.println("✅ Budget created: " + budget);
    }
    
    public List<Budget> getAllBudgets() {
        return budgets;
    }
    
    public Budget findById(int id) {
        for (Budget budget : budgets) {
            if (budget.getId() == id) return budget;
        }
        return null;
    }
    
    public void editBudget(int id, double amount, String timePeriod, LocalDate startDate, LocalDate endDate, String description) {
        Budget budget = findById(id);
        if (budget != null) {
            budget.setAmount(amount);
            budget.setTimePeriod(timePeriod);
            budget.setStartDate(startDate);
            budget.setEndDate(endDate);
            budget.setDescription(description);
            System.out.println("✅ Budget updated: " + budget);
        } else {
            System.out.println("❌ Budget not found with ID: " + id);
        }
    }
    
    public void deleteBudget(int id) {
        Budget budget = findById(id);
        if (budget != null) {
            budgets.remove(budget);
            System.out.println("✅ Budget deleted: " + budget);
        } else {
            System.out.println("❌ Budget not found with ID: " + id);
        }
    }
} 