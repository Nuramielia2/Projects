package projects;

import java.sql.Date;

public class Expense {
    private int id;
    private double amount;
    private String category;
    private Date date;
    private int userId;

    // Constructor for creating new expense
    public Expense(double amount, String category, Date date, int userId) {
        this.amount = amount;
        this.category = category;
        this.date = date;
        this.userId = userId;
    }

    // Constructor for retrieving expense from DB
    public Expense(int id, double amount, String category, Date date, int userId) {
        this.id = id;
        this.amount = amount;
        this.category = category;
        this.date = date;
        this.userId = userId;
    }

    public int getId() { return id; }
    public double getAmount() { return amount; }
    public String getCategory() { return category; }
    public Date getDate() { return date; }
    public int getUserId() { return userId; }

    public void setAmount(double amount) { this.amount = amount; }
    public void setCategory(String category) { this.category = category; }
    public void setDate(Date date) { this.date = date; }
    public void setUserId(int userId) { this.userId = userId; }

    @Override
    public String toString() {
        return String.format("Expense #%d: $%.2f for %s on %s", id, amount, category, date);
    }
}
