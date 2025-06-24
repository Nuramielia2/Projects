package projects;

/**
 * Simple launcher for the Personal Budget Manager Web Application
 * Run this class to start the web app and open it in your browser
 * 
 * @author Amielia
 */
public class Launcher {
    public static void main(String[] args) {
        System.out.println("🚀 Starting Personal Budget Manager Web App...");
        System.out.println("📱 This will open in your default web browser");
        System.out.println("⏳ Please wait a moment...");
        
        try {
            // Start the web application
            WebBudgetManager.main(args);
        } catch (Exception e) {
            System.out.println("❌ Error starting the web application: " + e.getMessage());
            System.out.println("💡 Make sure no other application is using port 8080");
        }
    }
} 