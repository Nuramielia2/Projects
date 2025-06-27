package projects;

public class UserManager {
    private User currentUser = null;

    public boolean register(String username, String password, String email) {
        UserDAO userDAO = new UserDAO();
        return userDAO.addUser(username, password, email);
    }

    public boolean login(String username, String password) {
        UserDAO userDAO = new UserDAO();
        User user = userDAO.findUser(username, password);
        if (user != null) {
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
}