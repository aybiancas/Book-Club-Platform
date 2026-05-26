package model;

import java.time.LocalDate;
import java.util.Objects;

public abstract class User {
    private int id;
    private String name, username, email, password;
    private LocalDate joinDate;

    public User (int id, String name, String username, String email, String password) {
        this.id = id;
        this.name = name;
        this.username = username;
        this.email = email;
    }

    public User (String name, String username, String email, String password) {
        this.name = name;
        this.username = username;
        this.email = email;
        this.password = password;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public LocalDate getJoinDate() {
        return joinDate;
    }

    public void setUsername(String username) { // set/change username
        this.username = username;
    }

    public void setName(String name) { // set/change name
        this.name = name;
    }

    public void setEmail(String email) { // set/change email
        this.email = email;
    }

    public void setPassword(String password) { // set/change password
        this.password = password;
    }

    public abstract String getRole();

//    public abstract String getProfileSummary();

    public boolean authenticate(String pw) {
        return this.password.equals(pw);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof User)) return false;
        return Objects.equals(id, ((User) o).id);
    }

    @Override
    public int hashCode() { return Objects.hash(id); }

    @Override
    public String toString() {
        return String.format("%s [%s] (%s)", name, getRole(), email);
    }
}
