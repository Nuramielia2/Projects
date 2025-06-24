package projects;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Manages financial goals for the personal budget manager.
 * 
 * @author Amielia
 */
public class GoalManager {
    private List<Goal> goals = new ArrayList<>();
    
    public void createGoal(String description, double targetAmount, LocalDate deadline) {
        Goal goal = new Goal(description, targetAmount, deadline);
        goals.add(goal);
        // Save to database
        GoalDAO goalDAO = new GoalDAO();
        boolean dbSuccess = goalDAO.addGoal(description, targetAmount, deadline);
        if (dbSuccess) {
            System.out.println("✅ Goal created and saved to database: " + goal);
        } else {
            System.out.println("❌ Error saving goal to database.");
        }
    }
    
    public List<Goal> getAllGoals() {
        return goals;
    }
    
    public Goal findById(int id) {
        for (Goal goal : goals) {
            if (goal.getId() == id) return goal;
        }
        return null;
    }
    
    public void editGoal(int id, String description, double targetAmount, LocalDate deadline) {
        Goal goal = findById(id);
        if (goal != null) {
            goal.setDescription(description);
            goal.setTargetAmount(targetAmount);
            goal.setDeadline(deadline);
            System.out.println("✅ Goal updated: " + goal);
        } else {
            System.out.println("❌ Goal not found with ID: " + id);
        }
    }
    
    public void updateSavings(int id, double newSavings) {
        Goal goal = findById(id);
        if (goal != null) {
            goal.setCurrentSavings(newSavings);
            System.out.println("✅ Savings updated for goal #" + id + ": $" + newSavings);
        } else {
            System.out.println("❌ Goal not found with ID: " + id);
        }
    }
    
    public void deleteGoal(int id) {
        Goal goal = findById(id);
        if (goal != null) {
            goals.remove(goal);
            System.out.println("✅ Goal deleted: " + goal);
        } else {
            System.out.println("❌ Goal not found with ID: " + id);
        }
    }
    
    public double getTotalTargetAmount() {
        return goals.stream().mapToDouble(Goal::getTargetAmount).sum();
    }
    
    public double getTotalCurrentSavings() {
        return goals.stream().mapToDouble(Goal::getCurrentSavings).sum();
    }
} 