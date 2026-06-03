package config;

import java.sql.*;

public class ConnectionProvider {
    private static final String url = "jdbc:postgresql://localhost:5432/clubproject"; // adauga numele BD
    private static final String username = "localadmin";
    private static final String password = "localuserpass";
    private static volatile ConnectionProvider instance;
    private Connection connection;

    private ConnectionProvider() {
        try {
            this.connection = DriverManager.getConnection(url, username, password);
        }
        catch(SQLException e) {
            throw new RuntimeException("Cannot connect to database: " + e.getMessage(), e);

        }
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
            synchronized (ConnectionProvider.class) {
                if (instance == null) {
                    instance = new ConnectionProvider();
                }
            }
        }
        return instance;
    }

    public Connection getConnection() {
        try {
            if (connection == null || connection.isClosed()) {
                connection = DriverManager.getConnection(url, username, password);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Cannot reopen to database: " + e.getMessage(), e);
        }
        return connection;
    }

    public void close() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        } catch(SQLException e) {
            System.out.println("Error closing connection: " + e.getMessage());
        }
    }
}
