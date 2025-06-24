package projects;

import java.time.LocalDate;

public class Expense {
    private int id;
    private double amount;
    private String category;
    private LocalDate date;
    private static int nextId = 1;

    public Expense(double amount, String category, LocalDate date) {
        this.id = nextId++;
        this.amount = amount;
        this.category = category;
        this.date = date;
    }

    public int getId() { return id; }
    public double getAmount() { return amount; }
    public String getCategory() { return category; }
    public LocalDate getDate() { return date; }

    public void setAmount(double amount) { this.amount = amount; }
    public void setCategory(String category) { this.category = category; }
    public void setDate(LocalDate date) { this.date = date; }

    @Override
    public String toString() {
        return String.format("Expense #%d: $%.2f for %s on %s", id, amount, category, date);
    }
} 