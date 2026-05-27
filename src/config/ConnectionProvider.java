package config;

import java.sql.*;

public class ConnectionProvider {
    private static String url = "jdbc:posgresql://localhost:5432/clubproject"; // adauga numele BD
    private static String username = "localadmin";
    private static String password = "localuserpass";
    private static ConnectionProvider instance;

    private ConnectionProvider() {
    }

    public static String getUrl() {
        return url;
    }

    public static String getUsername() {
        return username;
    }

    public static String getPassword() {
        return password;
    }

    public static ConnectionProvider getInstance() {
        if (instance == null) {
            instance = new ConnectionProvider();
//            try {
//                return DriverManager.getConnection(url, username, password);
//            } catch (SQLException e) {
//                throw new RuntimeException(e);
//            }
        }
        return instance;
    }
}
