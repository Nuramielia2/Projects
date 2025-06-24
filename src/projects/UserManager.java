package projects;

import java.util.ArrayList;
import java.util.List;
import projects.UserDAO;

public class UserManager {
    private List<User> users = new ArrayList<>();
    private User currentUser = null;

    public boolean register(String username, String password, String email) {
        if (findUser(username) != null) return false;
        users.add(new User(username, password, email));
        // Save to database
        UserDAO userDAO = new UserDAO();
        return userDAO.addUser(username, password, email);
    }

    public boolean login(String username, String password) {
        User user = findUser(username);
        if (user != null && user.getPassword().equals(password)) {
            currentUser = user;
            return true;
        }
        return false;
    }

    public void logout() {
        currentUser = null;
    }

    public User getCurrentUser() {
        return currentUser;
    }

    private User findUser(String username) {
        for (User user : users) {
            if (user.getUsername().equals(username)) return user;
        }
        return null;
    }
} 