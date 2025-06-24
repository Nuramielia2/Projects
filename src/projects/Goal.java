package projects;

import java.time.LocalDate;

/**
 * Represents a financial goal in the personal budget manager.
 * 
 * @author Amielia
 */
public class Goal {
    private int id;
    private String description;
    private double targetAmount;
    private LocalDate deadline;
    private double currentSavings;
    private static int nextId = 1;
    
    public Goal(String description, double targetAmount, LocalDate deadline) {
        this.id = nextId++;
        this.description = description;
        this.targetAmount = targetAmount;
        this.deadline = deadline;
        this.currentSavings = 0.0;
    }
    
    public int getId() { return id; }
    public String getDescription() { return description; }
    public double getTargetAmount() { return targetAmount; }
    public LocalDate getDeadline() { return deadline; }
    public double getCurrentSavings() { return currentSavings; }
    
    public void setDescription(String description) { this.description = description; }
    public void setTargetAmount(double targetAmount) { this.targetAmount = targetAmount; }
    public void setDeadline(LocalDate deadline) { this.deadline = deadline; }
    public void setCurrentSavings(double currentSavings) { this.currentSavings = currentSavings; }
    
    public double getProgress() {
        if (targetAmount == 0) return 0;
        return (currentSavings / targetAmount) * 100;
    }
    
    public boolean isCompleted() {
        return currentSavings >= targetAmount;
    }
    
    public boolean isOverdue() {
        return LocalDate.now().isAfter(deadline) && !isCompleted();
    }
    
    @Override
    public String toString() {
        return String.format("Goal #%d: %s - $%.2f/$%.2f (%.1f%%)", 
                           id, description, currentSavings, targetAmount, getProgress());
    }
} 