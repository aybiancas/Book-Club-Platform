package model;

public class Manager extends User {
    public Manager (int id, String name, String username, String email, String password) {
        super (id, name, username, email, password);
    }

    public Manager (String name, String username, String email, String password) {
        super (name, username, email, password);
    }

    @Override
    public String getRole() {
        return "Manager";
    }
}
