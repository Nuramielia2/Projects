package projects;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.Objects;

public class Income {
    private int id;
    private double amount;
    private String source;
    private Date date;
    private int userId;  // NEW

    // Constructor for inserting new income
    public Income(double amount, String source, Date date, int userId) {
        this.amount = amount;
        this.source = source;
        this.date = date;
        this.userId = userId;
    }

    // Constructor for retrieving income from DB
    public Income(int id, double amount, String source, Date date, int userId) {
        this.id = id;
        this.amount = amount;
        this.source = source;
        this.date = date;
        this.userId = userId;
    }

    public int getId() { return id; }
    public double getAmount() { return amount; }
    public String getSource() { return source; }
    public Date getDate() { return date; }
    public int getUserId() { return userId; }

    public void setAmount(double amount) { this.amount = amount; }
    public void setSource(String source) { this.source = source; }
    public void setDate(Date date) { this.date = date; }
    public void setUserId(int userId) { this.userId = userId; }

    @Override
    public String toString() {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        return String.format("Income: $%.2f from %s on %s", amount, source, formatter.format(date));
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Income income = (Income) obj;
        return id == income.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
