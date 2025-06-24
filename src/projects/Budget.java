package projects;

import java.time.LocalDate;

/**
 * Represents a budget record in the personal budget manager.
 * 
 * @author Amielia
 */
public class Budget {
    private int id;
    private double amount;
    private String timePeriod; // e.g., "Monthly", "Weekly", "Yearly"
    private LocalDate startDate;
    private LocalDate endDate;
    private String description;
    private static int nextId = 1;
    
    public Budget(double amount, String timePeriod, LocalDate startDate, LocalDate endDate, String description) {
        this.id = nextId++;
        this.amount = amount;
        this.timePeriod = timePeriod;
        this.startDate = startDate;
        this.endDate = endDate;
        this.description = description;
    }
    
    public int getId() { return id; }
    public double getAmount() { return amount; }
    public String getTimePeriod() { return timePeriod; }
    public LocalDate getStartDate() { return startDate; }
    public LocalDate getEndDate() { return endDate; }
    public String getDescription() { return description; }
    
    public void setAmount(double amount) { this.amount = amount; }
    public void setTimePeriod(String timePeriod) { this.timePeriod = timePeriod; }
    public void setStartDate(LocalDate startDate) { this.startDate = startDate; }
    public void setEndDate(LocalDate endDate) { this.endDate = endDate; }
    public void setDescription(String description) { this.description = description; }
    
    @Override
    public String toString() {
        return String.format("Budget #%d: $%.2f (%s) - %s", id, amount, timePeriod, description);
    }
} 