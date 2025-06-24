package projects;

import java.util.ArrayList;
import java.util.List;

public class UserManager {
    private List<User> users = new ArrayList<>();
    private User currentUser = null;

    public boolean register(String username, String password, String email) {
        if (findUser(username) != null) return false;
        users.add(new User(username, password, email));
        return true;
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