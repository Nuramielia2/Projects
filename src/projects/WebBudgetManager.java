package projects;

import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpExchange;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.List;
import java.time.LocalDate;
import java.sql.Date;

/**
 * Simple web-based Personal Budget Manager using Java's built-in HTTP server
 * 
 * @author Amielia
 */
public class WebBudgetManager {
    private static UserManager userManager = new UserManager();
    private static IncomeManager incomeManager = new IncomeManager();
    private static ExpenseManager expenseManager = new ExpenseManager();
    private static GoalManager goalManager = new GoalManager();
    private static Map<String, User> sessions = new ConcurrentHashMap<>();
    
    public static void main(String[] args) throws IOException {
        HttpServer server = HttpServer.create(new InetSocketAddress(8080), 0);
        
        server.createContext("/", new MainHandler());
        server.createContext("/login", new LoginHandler());
        server.createContext("/register", new RegisterHandler());
        server.createContext("/logout", new LogoutHandler());
        server.createContext("/income", new IncomeHandler());
        server.createContext("/expense", new ExpenseHandler());
        server.createContext("/view-income", new ViewIncomeHandler());
        server.createContext("/view-expense", new ViewExpenseHandler());
        server.createContext("/goal", new GoalHandler());
        server.createContext("/view-goal", new ViewGoalHandler());
        server.createContext("/report", new FinancialReportHandler());
        
        server.setExecutor(null);
        server.start();
        
        System.out.println("Budget Manager Web App running on http://localhost:8080");
        System.out.println("Opening browser...");
        
        // Automatically open the default web browser
        openBrowser("http://localhost:8080");
    }
    
    /**
     * Opens the default web browser with the specified URL
     */
    private static void openBrowser(String url) {
        try {
            String os = System.getProperty("os.name").toLowerCase();
            Runtime runtime = Runtime.getRuntime();
            
            System.out.println("🌐 Detected OS: " + os);
            System.out.println("🔗 Opening URL: " + url);
            
            if (os.contains("win")) {
                // Windows - try multiple approaches
                try {
                    runtime.exec("cmd /c start " + url);
                    System.out.println("✅ Browser opened successfully (Windows)");
                } catch (Exception e) {
                    // Try alternative for Windows
                    runtime.exec("rundll32 url.dll,FileProtocolHandler " + url);
                    System.out.println("✅ Browser opened successfully (Windows alternative)");
                }
            } else if (os.contains("mac")) {
                // macOS
                runtime.exec("open " + url);
                System.out.println("✅ Browser opened successfully (macOS)");
            } else {
                // Linux
                runtime.exec("xdg-open " + url);
                System.out.println("✅ Browser opened successfully (Linux)");
            }
            
            // Wait a moment for browser to open
            Thread.sleep(2000);
            
        } catch (Exception e) {
            System.out.println("❌ Could not open browser automatically: " + e.getMessage());
            System.out.println("🔗 Please manually open this URL in your browser: " + url);
            System.out.println("💡 Copy and paste this link: " + url);
        }
    }
    
    static class MainHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            try {
                System.out.println("🌐 Request received: " + exchange.getRequestMethod() + " " + exchange.getRequestURI());
                
                String sessionId = getSessionId(exchange);
                User currentUser = null;
                if (sessionId != null) {
                    currentUser = sessions.get(sessionId);
                }
                
                String html = generateMainPage(currentUser);
                System.out.println("📄 Generated HTML length: " + html.length());
                
                sendResponse(exchange, html);
                
                System.out.println("✅ Response sent successfully");
            } catch (Exception e) {
                System.out.println("❌ Error in MainHandler: " + e.getMessage());
                e.printStackTrace();
                
                // Send error response
                String errorHtml = "<html><body><h1>Error</h1><p>" + e.getMessage() + "</p></body></html>";
                exchange.getResponseHeaders().add("Content-Type", "text/html");
                exchange.sendResponseHeaders(500, errorHtml.length());
                try (OutputStream os = exchange.getResponseBody()) {
                    os.write(errorHtml.getBytes());
                }
            }
        }
    }
    
    static class LoginHandler implements HttpHandler {
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        if ("POST".equals(exchange.getRequestMethod())) {
            String body = new String(exchange.getRequestBody().readAllBytes());
            Map<String, String> params = parseParams(body);

            String username = params.get("username");
            String password = params.get("password");

            UserDAO userDAO = new UserDAO();
            User user = userDAO.findUser(username, password);

            if (user != null) {
                String sessionId = java.util.UUID.randomUUID().toString();
                sessions.put(sessionId, user); // Store full user with userId

                exchange.getResponseHeaders().add("Set-Cookie", "session=" + sessionId);
                exchange.getResponseHeaders().add("Location", "/");
                exchange.sendResponseHeaders(302, -1);
            } else {
                String html = generateMainPage(null) + "<script>alert('Login failed!');</script>";
                sendResponse(exchange, html);
            }
        } else {
            exchange.getResponseHeaders().add("Location", "/");
            exchange.sendResponseHeaders(302, -1);
        }
    }
}

    
    static class RegisterHandler implements HttpHandler {
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        if ("POST".equals(exchange.getRequestMethod())) {
            String body = new String(exchange.getRequestBody().readAllBytes());
            Map<String, String> params = parseParams(body);

            String username = params.get("username");
            String email = params.get("email");
            String password = params.get("password");

            UserDAO userDAO = new UserDAO();
            boolean success = userDAO.addUser(username, password, email);

            String html;
            if (success) {
                html = generateMainPage(null) + "<script>alert('Registration successful! Please login.');</script>";
            } else {
                html = generateMainPage(null) + "<script>alert('Registration failed! Username may already exist.');</script>";
            }

            sendResponse(exchange, html);
        } else {
            exchange.getResponseHeaders().add("Location", "/");
            exchange.sendResponseHeaders(302, -1);
        }
    }
}

    
    static class LogoutHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String sessionId = getSessionId(exchange);
            sessions.remove(sessionId);
            
            exchange.getResponseHeaders().add("Set-Cookie", "session=; Max-Age=0");
            exchange.getResponseHeaders().add("Location", "/");
            exchange.sendResponseHeaders(302, -1);
        }
    }
    
    static class IncomeHandler implements HttpHandler {
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String sessionId = getSessionId(exchange);
        User currentUser = sessions.get(sessionId);

        if (currentUser == null) {
            exchange.getResponseHeaders().add("Location", "/");
            exchange.sendResponseHeaders(302, -1);
            return;
        }

        if ("POST".equalsIgnoreCase(exchange.getRequestMethod())) {
            String body = new String(exchange.getRequestBody().readAllBytes());
            Map<String, String> params = parseParams(body);

            try {
                double amount = Double.parseDouble(params.get("amount"));
                String source = params.get("source");
                Date date = Date.valueOf(params.get("date"));

                Income income = new Income(amount, source, date, currentUser.getUserId());
                IncomeDAO dao = new IncomeDAO();

                if (dao.addIncome(income)) {
                    System.out.println("💾 Income successfully saved to database.");
                } else {
                    System.out.println("⚠️ Failed to save income to database.");
                }

            } catch (Exception e) {
                System.out.println("❌ Error recording income: " + e.getMessage());
                e.printStackTrace();
            }

            exchange.getResponseHeaders().add("Location", "/view-income");
            exchange.sendResponseHeaders(302, -1);

        } else {
            String html = generateIncomeForm(); // Call the styled form
            sendResponse(exchange, html);
        }
    }
}

    static class ExpenseHandler implements HttpHandler {
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String sessionId = getSessionId(exchange);
        User currentUser = sessions.get(sessionId);

        if (currentUser == null) {
            exchange.getResponseHeaders().add("Location", "/");
            exchange.sendResponseHeaders(302, -1);
            return;
        }

        if ("POST".equalsIgnoreCase(exchange.getRequestMethod())) {
            String body = new String(exchange.getRequestBody().readAllBytes());
            Map<String, String> params = parseParams(body);

            try {
                double amount = Double.parseDouble(params.get("amount"));
                String category = params.get("category");
                Date date = Date.valueOf(params.get("date"));

                // FIXED: Added currentUser.getUserId()
                boolean success = expenseManager.recordExpense(amount, category, date, currentUser.getUserId());

                if (success) {
                    System.out.println("✅ Expense recorded: $" + amount + " for " + category);
                } else {
                    System.out.println("❌ Failed to record expense in database.");
                }
            } catch (Exception e) {
                System.out.println("❌ Error parsing or recording expense: " + e.getMessage());
            }

            exchange.getResponseHeaders().add("Location", "/");
            exchange.sendResponseHeaders(302, -1);
        } else {
            String html = generateExpenseForm(); // your existing HTML form
            sendResponse(exchange, html);
        }
    }
}

    
    static class ViewIncomeHandler implements HttpHandler {
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String sessionId = getSessionId(exchange);
        User currentUser = sessions.get(sessionId);

        if (currentUser == null) {
            exchange.getResponseHeaders().add("Location", "/");
            exchange.sendResponseHeaders(302, -1);
            return;
        }

        String html = generateViewIncomePage(currentUser);
        sendResponse(exchange, html);
    }
}


    
    static class ViewExpenseHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String sessionId = getSessionId(exchange);
            User currentUser = sessions.get(sessionId);
            
            if (currentUser == null) {
                exchange.getResponseHeaders().add("Location", "/");
                exchange.sendResponseHeaders(302, -1);
                return;
            }
            
            String html = generateViewExpensePage(currentUser); //
            sendResponse(exchange, html);
        }
    }
    
    static class GoalHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String sessionId = getSessionId(exchange);
            User currentUser = sessions.get(sessionId);
            
            if (currentUser == null) {
                exchange.getResponseHeaders().add("Location", "/");
                exchange.sendResponseHeaders(302, -1);
                return;
            }
            
            if ("POST".equals(exchange.getRequestMethod())) {
                String body = new String(exchange.getRequestBody().readAllBytes());
                Map<String, String> params = parseParams(body);
                
                try {
                    String description = params.get("description");
                    double targetAmount = Double.parseDouble(params.get("targetAmount"));
                    LocalDate deadline = LocalDate.parse(params.get("deadline"));
                    
                    goalManager.createGoal(description, targetAmount, deadline);
                    
                    exchange.getResponseHeaders().add("Location", "/");
                    exchange.sendResponseHeaders(302, -1);
                } catch (Exception e) {
                    System.out.println("❌ Error creating goal: " + e.getMessage());
                    exchange.getResponseHeaders().add("Location", "/");
                    exchange.sendResponseHeaders(302, -1);
                }
            } else {
                String html = generateGoalForm();
                sendResponse(exchange, html);
            }
        }
    }
    
    
    static class ViewGoalHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String sessionId = getSessionId(exchange);
            User currentUser = sessions.get(sessionId);
            
            if (currentUser == null) {
                exchange.getResponseHeaders().add("Location", "/");
                exchange.sendResponseHeaders(302, -1);
                return;
            }
            
            String html = generateViewGoalPage();
            sendResponse(exchange, html);
        }
    }
    
    static class FinancialReportHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String sessionId = getSessionId(exchange);
            User currentUser = sessions.get(sessionId);
            
            if (currentUser == null) {
                exchange.getResponseHeaders().add("Location", "/");
                exchange.sendResponseHeaders(302, -1);
                return;
            }
            
            String html = generateFinancialReportPage(currentUser);
            sendResponse(exchange, html);
        }
    }
    
    private static String generateMainPage(User currentUser) {
        StringBuilder html = new StringBuilder();
        html.append("<!DOCTYPE html>");
        html.append("<html><head><title>Personal Budget Manager</title>");
        html.append("<style>");
        html.append("body { font-family: Arial, sans-serif; margin: 20px; background-color: #f4f4f4; }");
        html.append(".container { max-width: 800px; margin: 0 auto; background: white; padding: 20px; border-radius: 8px; }");
        html.append(".header { text-align: center; color: #333; margin-bottom: 30px; }");
        html.append(".form-group { margin-bottom: 15px; }");
        html.append("label { display: block; margin-bottom: 5px; font-weight: bold; }");
        html.append("input[type=text], input[type=password], input[type=email], input[type=number] { width: 100%; padding: 8px; border: 1px solid #ddd; border-radius: 4px; box-sizing: border-box; }");
        html.append("button { background-color: #4CAF50; color: white; padding: 10px 20px; border: none; border-radius: 4px; cursor: pointer; margin-right: 10px; }");
        html.append("button:hover { background-color: #45a049; }");
        html.append(".menu { margin: 20px 0; }");
        html.append(".menu a { display: inline-block; background-color: #2196F3; color: white; padding: 10px 15px; text-decoration: none; border-radius: 4px; margin: 5px; }");
        html.append(".menu a:hover { background-color: #1976D2; }");
        html.append(".logout { background-color: #f44336 !important; }");
        html.append(".logout:hover { background-color: #d32f2f !important; }");
        html.append("</style></head><body>");
        html.append("<div class='container'>");
        html.append("<div class='header'><h1>💰 Personal Budget Manager</h1></div>");
        
        if (currentUser == null) {
            html.append("<h2>Welcome! Please login or create an account</h2>");
            html.append("<div style='display: flex; gap: 20px;'>");
            
            // Login Form
            html.append("<div style='flex: 1;'><h3>Login</h3>");
            html.append("<form action='/login' method='post'>");
            html.append("<div class='form-group'><label>Username:</label><input type='text' name='username' required></div>");
            html.append("<div class='form-group'><label>Password:</label><input type='password' name='password' required></div>");
            html.append("<button type='submit'>Login</button></form></div>");
            
            // Register Form
            html.append("<div style='flex: 1;'><h3>Create Account</h3>");
            html.append("<form action='/register' method='post'>");
            html.append("<div class='form-group'><label>Username:</label><input type='text' name='username' required></div>");
            html.append("<div class='form-group'><label>Email:</label><input type='email' name='email' required></div>");
            html.append("<div class='form-group'><label>Password:</label><input type='password' name='password' required></div>");
            html.append("<button type='submit'>Register</button></form></div>");
            
            html.append("</div>");
        } else {
            html.append("<h2>Welcome, ").append(currentUser.getUsername()).append("!</h2>");
            html.append("<div class='menu'>");
            html.append("<a href='/income'>💰 Record Income</a>");
            html.append("<a href='/expense'>💸 Record Expense</a>");
            html.append("<a href='/budget'>📋 Create Budget</a>");
            html.append("<a href='/goal'>🎯 Set Goal</a>");
            html.append("<a href='/view-income'>📊 View Income</a>");
            html.append("<a href='/view-expense'>📊 View Expenses</a>");
            html.append("<a href='/view-budget'>📋 View Budgets</a>");
            html.append("<a href='/view-goal'>🎯 View Goals</a>");
            html.append("<a href='/report'>📈 Financial Report</a>");
            html.append("<a href='/edit-income'>✏️ Edit Income</a>");
            html.append("<a href='/edit-expense'>✏️ Edit Expenses</a>");
            html.append("<a href='/logout' class='logout'>🚪 Logout</a>");
            html.append("</div>");
        }
        
        html.append("</div></body></html>");
        return html.toString();
    }
    
    private static String generateIncomeForm() {
        return "<!DOCTYPE html><html><head><title>Record Income</title>" +
               "<style>body { font-family: Arial, sans-serif; margin: 20px; }" +
               ".container { max-width: 600px; margin: 0 auto; background: white; padding: 20px; border-radius: 8px; }" +
               ".form-group { margin-bottom: 15px; }" +
               "label { display: block; margin-bottom: 5px; }" +
               "input { width: 100%; padding: 8px; border: 1px solid #ddd; border-radius: 4px; }" +
               "button { background-color: #4CAF50; color: white; padding: 10px 20px; border: none; border-radius: 4px; cursor: pointer; }" +
               "</style></head><body>" +
               "<div class='container'>" +
               "<h1>💰 Record Income</h1>" +
               "<form action='/income' method='post'>" +
               "<div class='form-group'><label>Amount ($):</label><input type='number' step='0.01' name='amount' required></div>" +
               "<div class='form-group'><label>Source:</label><input type='text' name='source' required></div>" +
               "<div class='form-group'><label>Date:</label><input type='date' name='date' required></div>" +
               "<button type='submit'>Record Income</button>" +
               "</form>" +
               "<p><a href='/'>Back to Home</a></p>" +
               "</div></body></html>";
    }
    
    private static String generateExpenseForm() {
        return "<!DOCTYPE html><html><head><title>Record Expense</title>" +
               "<style>body { font-family: Arial, sans-serif; margin: 20px; }" +
               ".container { max-width: 600px; margin: 0 auto; background: white; padding: 20px; border-radius: 8px; }" +
               ".form-group { margin-bottom: 15px; }" +
               "label { display: block; margin-bottom: 5px; }" +
               "input { width: 100%; padding: 8px; border: 1px solid #ddd; border-radius: 4px; }" +
               "button { background-color: #f44336; color: white; padding: 10px 20px; border: none; border-radius: 4px; cursor: pointer; }" +
               "</style></head><body>" +
               "<div class='container'>" +
               "<h1>💸 Record Expense</h1>" +
               "<form action='/expense' method='post'>" +
               "<div class='form-group'><label>Amount ($):</label><input type='number' step='0.01' name='amount' required></div>" +
               "<div class='form-group'><label>Category:</label><input type='text' name='category' required></div>" +
               "<div class='form-group'><label>Date:</label><input type='date' name='date' required></div>" +
               "<button type='submit'>Record Expense</button>" +
               "</form>" +
               "<p><a href='/'>Back to Home</a></p>" +
               "</div></body></html>";
    }
    
    private static String generateViewExpensePage(User currentUser) {
    StringBuilder html = new StringBuilder();
    html.append("<!DOCTYPE html><html><head><title>View Expenses</title>");
    html.append("<style>body { font-family: Arial, sans-serif; margin: 20px; }");
    html.append(".container { max-width: 800px; margin: 0 auto; background: white; padding: 20px; border-radius: 8px; }");
    html.append(".record { background: #f9f9f9; padding: 10px; margin: 10px 0; border-radius: 4px; border-left: 4px solid #f44336; }");
    html.append(".total { font-weight: bold; font-size: 18px; color: #f44336; margin-top: 20px; }");
    html.append("</style></head><body>");
    html.append("<div class='container'>");
    html.append("<h1>📊 Expense Records</h1>");

    List<Expense> expenses = expenseManager.getExpensesForUser(currentUser.getUserId());
    if (expenses.isEmpty()) {
        html.append("<p>No expense records found.</p>");
    } else {
        double total = 0;
        for (Expense expense : expenses) {
            html.append("<div class='record'>");
            html.append("<strong>ID:</strong> ").append(expense.getId()).append("<br>");
            html.append("<strong>Amount:</strong> $").append(String.format("%.2f", expense.getAmount())).append("<br>");
            html.append("<strong>Category:</strong> ").append(expense.getCategory()).append("<br>");
            html.append("<strong>Date:</strong> ").append(expense.getDate());
            html.append("</div>");
            total += expense.getAmount();
        }
        html.append("<div class='total'>Total Expenses: $").append(String.format("%.2f", total)).append("</div>");
    }

    html.append("<p><a href='/'>Back to Home</a></p>");
    html.append("</div></body></html>");
    return html.toString();
}
    
    private static String generateViewIncomePage(User currentUser) {
    StringBuilder html = new StringBuilder();
    html.append("<!DOCTYPE html><html><head><title>View Income</title>");
    html.append("<style>body { font-family: Arial, sans-serif; margin: 20px; }");
    html.append(".container { max-width: 800px; margin: 0 auto; background: white; padding: 20px; border-radius: 8px; }");
    html.append(".record { background: #f9f9f9; padding: 10px; margin: 10px 0; border-radius: 4px; border-left: 4px solid #4CAF50; }");
    html.append(".total { font-weight: bold; font-size: 18px; color: #4CAF50; margin-top: 20px; }");
    html.append("</style></head><body>");
    html.append("<div class='container'>");
    html.append("<h1>💵 Income Records</h1>");

    IncomeDAO dao = new IncomeDAO();
    List<Income> incomes = dao.getAllIncomeForUser(currentUser.getUserId());

    if (incomes.isEmpty()) {
        html.append("<p>No income records found.</p>");
    } else {
        double total = 0;
        for (Income income : incomes) {
            html.append("<div class='record'>");
            html.append("<strong>ID:</strong> ").append(income.getId()).append("<br>");
            html.append("<strong>Amount:</strong> $").append(String.format("%.2f", income.getAmount())).append("<br>");
            html.append("<strong>Source:</strong> ").append(income.getSource()).append("<br>");
            html.append("<strong>Date:</strong> ").append(income.getDate());
            html.append("</div>");
            total += income.getAmount();
        }
        html.append("<div class='total'>Total Income: $").append(String.format("%.2f", total)).append("</div>");
    }

    html.append("<p><a href='/'>Back to Home</a></p>");
    html.append("</div></body></html>");
    return html.toString();
}


    
    private static void sendResponse(HttpExchange exchange, String response) throws IOException {
        byte[] responseBytes = response.getBytes("UTF-8");
        exchange.getResponseHeaders().add("Content-Type", "text/html; charset=UTF-8");
        exchange.sendResponseHeaders(200, responseBytes.length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(responseBytes);
        }
    }
    
    private static String getSessionId(HttpExchange exchange) {
        String cookie = exchange.getRequestHeaders().getFirst("Cookie");
        if (cookie != null) {
            for (String pair : cookie.split(";")) {
                String[] keyValue = pair.trim().split("=");
                if (keyValue.length == 2 && "session".equals(keyValue[0])) {
                    return keyValue[1];
                }
            }
        }
        return null;
    }
    
    private static Map<String, String> parseParams(String body) {
        Map<String, String> params = new HashMap<>();
        for (String pair : body.split("&")) {
            String[] keyValue = pair.split("=");
            if (keyValue.length == 2) {
                params.put(keyValue[0], java.net.URLDecoder.decode(keyValue[1], java.nio.charset.StandardCharsets.UTF_8));
            }
        }
        return params;
    }
    
    private static String generateGoalForm() {
        return "<!DOCTYPE html><html><head><title>Set Goal</title>" +
               "<style>body { font-family: Arial, sans-serif; margin: 20px; }" +
               ".container { max-width: 600px; margin: 0 auto; background: white; padding: 20px; border-radius: 8px; }" +
               ".form-group { margin-bottom: 15px; }" +
               "label { display: block; margin-bottom: 5px; }" +
               "input, textarea { width: 100%; padding: 8px; border: 1px solid #ddd; border-radius: 4px; }" +
               "button { background-color: #4CAF50; color: white; padding: 10px 20px; border: none; border-radius: 4px; cursor: pointer; }" +
               "</style></head><body>" +
               "<div class='container'>" +
               "<h1>🎯 Set Financial Goal</h1>" +
               "<form action='/goal' method='post'>" +
               "<div class='form-group'><label>Description:</label><input type='text' name='description' required></div>" +
               "<div class='form-group'><label>Target Amount ($):</label><input type='number' step='0.01' name='targetAmount' required></div>" +
               "<div class='form-group'><label>Deadline:</label><input type='date' name='deadline' required></div>" +
               "<button type='submit'>Set Goal</button>" +
               "</form>" +
               "<p><a href='/'>Back to Home</a></p>" +
               "</div></body></html>";
    }
    
    private static String generateViewGoalPage() {
        StringBuilder html = new StringBuilder();
        html.append("<!DOCTYPE html><html><head><title>View Goals</title>");
        html.append("<style>body { font-family: Arial, sans-serif; margin: 20px; }");
        html.append(".container { max-width: 800px; margin: 0 auto; background: white; padding: 20px; border-radius: 8px; }");
        html.append(".record { background: #f9f9f9; padding: 10px; margin: 10px 0; border-radius: 4px; border-left: 4px solid #4CAF50; }");
        html.append(".progress { background: #ddd; border-radius: 4px; height: 20px; margin: 5px 0; }");
        html.append(".progress-bar { background: #4CAF50; height: 100%; border-radius: 4px; transition: width 0.3s; }");
        html.append("</style></head><body>");
        html.append("<div class='container'>");
        html.append("<h1>🎯 Financial Goals</h1>");
        
        List<Goal> goals = goalManager.getAllGoals();
        if (goals.isEmpty()) {
            html.append("<p>No goals found.</p>");
        } else {
            for (Goal goal : goals) {
                html.append("<div class='record'>");
                html.append("<strong>ID:</strong> ").append(goal.getId()).append("<br>");
                html.append("<strong>Description:</strong> ").append(goal.getDescription()).append("<br>");
                html.append("<strong>Target:</strong> $").append(String.format("%.2f", goal.getTargetAmount())).append("<br>");
                html.append("<strong>Current Savings:</strong> $").append(String.format("%.2f", goal.getCurrentSavings())).append("<br>");
                html.append("<strong>Progress:</strong> ").append(String.format("%.1f", goal.getProgress())).append("%<br>");
                html.append("<div class='progress'><div class='progress-bar' style='width: ").append(goal.getProgress()).append("%'></div></div>");
                html.append("<strong>Deadline:</strong> ").append(goal.getDeadline());
                if (goal.isCompleted()) {
                    html.append(" <span style='color: green; font-weight: bold;'>✓ COMPLETED!</span>");
                } else if (goal.isOverdue()) {
                    html.append(" <span style='color: red; font-weight: bold;'>⚠ OVERDUE!</span>");
                }
                html.append("</div>");
            }
        }
        
        html.append("<p><a href='/'>Back to Home</a></p>");
        html.append("</div></body></html>");
        return html.toString();
    }
    
    private static String generateFinancialReportPage(User currentUser) {
    StringBuilder html = new StringBuilder();
    html.append("<!DOCTYPE html><html><head><title>Financial Report</title>");
    html.append("<style>body { font-family: Arial, sans-serif; margin: 20px; }");
    html.append(".container { max-width: 800px; margin: 0 auto; background: white; padding: 20px; border-radius: 8px; }");
    html.append(".summary { background: #f9f9f9; padding: 15px; margin: 15px 0; border-radius: 8px; }");
    html.append(".income { border-left: 4px solid #4CAF50; }");
    html.append(".expense { border-left: 4px solid #f44336; }");
    html.append(".balance { border-left: 4px solid #2196F3; font-weight: bold; }");
    html.append("</style></head><body>");
    html.append("<div class='container'>");
    html.append("<h1>📈 Financial Report</h1>");

    IncomeDAO incomeDAO = new IncomeDAO();
    ExpenseDAO expenseDAO = new ExpenseDAO();

    double totalIncome = incomeDAO.getTotalIncomeByUser(currentUser.getUserId());
    double totalExpenses = expenseDAO.getExpensesByUserId(currentUser.getUserId())
                                     .stream().mapToDouble(Expense::getAmount).sum();
    double balance = totalIncome - totalExpenses;

    html.append("<div class='summary income'>");
    html.append("<h3>💰 Total Income: $").append(String.format("%.2f", totalIncome)).append("</h3></div>");

    html.append("<div class='summary expense'>");
    html.append("<h3>💸 Total Expenses: $").append(String.format("%.2f", totalExpenses)).append("</h3></div>");

    html.append("<div class='summary balance'>");
    html.append("<h3>💳 Net Balance: $").append(String.format("%.2f", balance)).append("</h3>");
    if (balance > 0) {
        html.append("<p style='color: green;'>✅ You have a positive balance!</p>");
    } else if (balance < 0) {
        html.append("<p style='color: red;'>⚠️ You have a negative balance.</p>");
    } else {
        html.append("<p style='color: blue;'>⚖️ Your income and expenses are balanced.</p>");
    }
    html.append("</div>");

    html.append("<p><a href='/'>Back to Home</a></p>");
    html.append("</div></body></html>");
    return html.toString();
}

    
} 