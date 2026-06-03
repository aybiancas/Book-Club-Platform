package repositories;

import model.Manager;
import model.Member;
import model.User;
import config.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserRepository {

    private static UserRepository instance;

    private UserRepository() {}

    public static UserRepository getInstance() {
        if (instance == null) {
            instance = new UserRepository();
        }
        return instance;
    }

    private Connection connection() {
        return ConnectionProvider.getInstance().getConnection();
    }

    private User mapRow(ResultSet rs) throws SQLException {
        int id = rs.getInt("id");
        String name = rs.getString("name");
        String username = rs.getString("username");
        String email = rs.getString("email");
        String password = rs.getString("password");
        String role = rs.getString("role");

        if ("Manager".equals(role)) {
            return new Manager(id, name, username, email, password);
        }
        else {
            return new Member(id, name, username, email, password);
        }
    }

    public int addUser(User user) {
        String sql = "insert into users(name, username, email, password, role) values (?,?,?,?,?::user_role_enum)";
        try (PreparedStatement preparedStatement = connection().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            preparedStatement.setString(1, user.getName());
            preparedStatement.setString(2, user.getUsername());
            preparedStatement.setString(3, user.getEmail());
            preparedStatement.setString(4, user.getPassword());
            preparedStatement.setString(5, user.getRole());
            preparedStatement.executeUpdate();

            try (ResultSet generatedKeys = preparedStatement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    return generatedKeys.getInt(1);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    public void updateUser(User user) {
        String sql = "update users set name = ?, username = ?, email = ? where id = ?";
        try (PreparedStatement preparedStatement = connection().prepareStatement(sql)) {

            preparedStatement.setString(1, user.getName());
            preparedStatement.setString(2, user.getUsername());
            preparedStatement.setString(3, user.getEmail());
            preparedStatement.setInt(4, user.getId());
            preparedStatement.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void updatePassword(int userId, String newPassword) {
        String sql = "update users set password = ? where id = ?";
        try (PreparedStatement preparedStatement = connection().prepareStatement(sql)) {

            preparedStatement.setString(1, newPassword);
            preparedStatement.setInt   (2, userId);
            preparedStatement.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void deleteUser(int userId) {
        String sql = "delete from users where id = ?";
        try (PreparedStatement preparedStatement = connection().prepareStatement(sql)) {

            preparedStatement.setInt(1, userId);
            preparedStatement.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public User findById(int userId) {
        String sql = "select * from users where id = ?";
        try (PreparedStatement preparedStatement = connection().prepareStatement(sql)) {

            preparedStatement.setInt(1, userId);
            try (ResultSet rs = preparedStatement.executeQuery()) {
                if (rs.next()) {
                    return mapRow(rs);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public User findByUsername(String username) {
        String sql = "select * from users where username = ?";
        try (PreparedStatement preparedStatement = connection().prepareStatement(sql)) {

            preparedStatement.setString(1, username);
            try (ResultSet rs = preparedStatement.executeQuery()) {
                if (rs.next()) return mapRow(rs);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<User> findAll() {
        List<User> list = new ArrayList<>();
        String sql = "select * from users order by username";
        try (PreparedStatement preparedStatement = connection().prepareStatement(sql);
             ResultSet rs = preparedStatement.executeQuery()) {

            while (rs.next()) {
                list.add(mapRow(rs));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public List<User> findAllMembers() {
        List<User> list = new ArrayList<>();
        String sql = "select * from users where role = 'Member'::user_role_enum order by name";
        try (PreparedStatement preparedStatement = connection().prepareStatement(sql);
             ResultSet rs = preparedStatement.executeQuery()) {

            while (rs.next()) list.add(mapRow(rs));

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public List<User> findAllManagers() {
        List<User> list = new ArrayList<>();
        String sql = "select * from users where role = 'Manager'::user_role_enum order by name";
        try (PreparedStatement preparedStatement = connection().prepareStatement(sql);
             ResultSet rs = preparedStatement.executeQuery()) {

            while (rs.next()) list.add(mapRow(rs));

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }
}
