package gui;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import model.*;
import services.BookClubService;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.function.Consumer;

public class LoginController {

    @FXML 
    private TextField usernameField;
    @FXML 
    private PasswordField passwordField;
    @FXML 
    private Button loginBtn;
    @FXML 
    private Label errorLabel;
    @FXML 
    private ListView<String> logListView;

    @FXML 
    private TextField regNameF;
    @FXML 
    private TextField regUsernameF;
    @FXML 
    private TextField regEmailF;
    @FXML 
    private PasswordField regPasswordF;
    @FXML 
    private PasswordField regConfirmF;
    @FXML 
    private Label regStatus;

    private BookClubService service;
    private Consumer<User> onSuccess;
    private final ObservableList<String> loginLog = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        logListView.setItems(loginLog);
    }

    public void setService(BookClubService service, Consumer<User> onSuccess) {
        this.service = service;
        this.onSuccess = onSuccess;
    }

    @FXML
    private void handleLogin() {
        String username = usernameField.getText().trim();
        String password = passwordField.getText();

        if (username.isBlank() || password.isBlank()) {
            new Alert(Alert.AlertType.WARNING, "Username and password cannot be empty.", ButtonType.OK).showAndWait();
            return;
        }

        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss"));
        try {
            User user = service.getUserByUsername(username);
            if (user.authenticate(password)) {
                loginLog.add(0, timestamp + "  ✓  " + username + " signed in as " + user.getRole());
                errorLabel.setText(" ");
                usernameField.clear();
                passwordField.clear();
                onSuccess.accept(user);
            }
            else {
                loginLog.add(0, timestamp + "  ✗  " + username + " — wrong password");
                errorLabel.setText("Incorrect password.");
            }
        } catch (Exception ex) {
            loginLog.add(0, timestamp + "  ✗  " + username + " — user not found");
            errorLabel.setText("User not found.");
        }
    }

    @FXML
    private void handleRegister() {
        String name = regNameF.getText().trim();
        String username = regUsernameF.getText().trim();
        String email = regEmailF.getText().trim();
        String pass = regPasswordF.getText();
        String confirm = regConfirmF.getText();

        if (name.isBlank() || username.isBlank() || email.isBlank() || pass.isBlank()) {
            new Alert(Alert.AlertType.WARNING, "All fields are required.", ButtonType.OK).showAndWait();
            return;
        }
        if (!pass.equals(confirm)) {
            regStatus.setStyle("-fx-text-fill: #ef4444; -fx-font-size: 12;");
            regStatus.setText("Passwords do not match.");
            return;
        }
        try {
            service.registerUser(new Member(name, username, email, pass));
            regStatus.setStyle("-fx-text-fill: #22c55e; -fx-font-size: 12;");
            regStatus.setText("Account created! You can now sign in.");
            regNameF.clear();
            regUsernameF.clear();
            regEmailF.clear();
            regPasswordF.clear();
            regConfirmF.clear();
        } catch (Exception ex) {
            regStatus.setStyle("-fx-text-fill: #ef4444; -fx-font-size: 12;");
            regStatus.setText("Registration failed: " + ex.getMessage());
        }
    }
}