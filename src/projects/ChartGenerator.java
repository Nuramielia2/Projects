/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package projects;

/**
 *
 * @author Nikeisha
 */
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.data.category.DefaultCategoryDataset;

public class ChartGenerator {
    public static JFreeChart createIncomeExpenseChart(double income, double expenses) {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        dataset.setValue(income, "Amount", "Income");
        dataset.setValue(expenses, "Amount", "Expenses");

        return ChartFactory.createBarChart(
                "Income vs Expenses",
                "Category",
                "Amount",
                dataset
        );
    }
}