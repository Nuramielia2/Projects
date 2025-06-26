package projects;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

/**
 * Represents an income record in the personal budget manager.
 * 
 * @author Amielia
 */
public class Income {
    private int idincome;
    private double amount;
    private String source;
    private LocalDate date;
    private static int nextId = 1;
    
    /**
     * Constructor for creating a new income record.
     * 
     * @param amount The income amount
     * @param source The source of income
     * @param date The date of income
     */
    public Income(double amount, String source, LocalDate date) {
        this.idincome = nextId++;
        this.amount = amount;
        this.source = source;
        this.date = date;
    }
    
    /**
     * Constructor for creating an income record with a specific ID.
     * 
     * @param idincome The income record ID
     * @param amount The income amount
     * @param source The source of income
     * @param date The date of income
     */
    public Income(int idincome, double amount, String source, LocalDate date) {
        this.idincome = idincome;
        this.amount = amount;
        this.source = source;
        this.date = date;
    }
    
    // Getters
    public int getId() { return idincome; }
    public double getAmount() { return amount; }
    public String getSource() { return source; }
    public LocalDate getDate() { return date; }
    
    // Setters
    public void setAmount(double amount) { this.amount = amount; }
    public void setSource(String source) { this.source = source; }
    public void setDate(LocalDate date) { this.date = date; }
    
    /**
     * Returns a formatted string representation of the income record.
     * 
     * @return Formatted income record string
     */
    @Override
    public String toString() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        return String.format("Income #%d: $%.2f from %s on %s", 
                           idincome, amount, source, date.format(formatter));
    }
    
    /**
     * Checks if this income record equals another object.
     * 
     * @param obj The object to compare with
     * @return true if objects are equal, false otherwise
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Income income = (Income) obj;
        return idincome == income.idincome;
    }
    
    /**
     * Returns the hash code for this income record.
     * 
     * @return Hash code
     */
    @Override
    public int hashCode() {
        return Objects.hash(idincome);
    }
} 