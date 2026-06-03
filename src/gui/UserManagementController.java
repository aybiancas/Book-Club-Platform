package gui;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import model.*;
import services.BookClubService;

public class UserManagementController {

    @FXML 
    private ComboBox<String> roleFilter;
    @FXML 
    private TableView<User> userTable;
    @FXML 
    private TableColumn<User,String> colId;
    @FXML 
    private TableColumn<User,String> colName;
    @FXML 
    private TableColumn<User,String> colUser;
    @FXML 
    private TableColumn<User,String> colEmail;
    @FXML 
    private TableColumn<User,String> colRole;
    @FXML 
    private TableColumn<User,String> colJoin;
    @FXML 
    private GridPane detailGrid;
    @FXML 
    private Label summaryLabel;

    private BookClubService service;

    private final Label nameLabel = new Label();
    private final Label userLabel = new Label();
    private final Label emailLabel = new Label();
    private final Label roleLabel = new Label();
    private final Label joindateLabel = new Label();

    private final ObservableList<User> users = FXCollections.observableArrayList();

    public void setService(BookClubService service) {
        this.service = service;
        loadAll();
    }

    @FXML
    public void initialize() {
        colId.setCellValueFactory(d ->
                new SimpleStringProperty(String.valueOf(d.getValue().getId())));
        colName.setCellValueFactory(d ->
                new SimpleStringProperty(d.getValue().getName()));
        colUser.setCellValueFactory(d ->
                new SimpleStringProperty(d.getValue().getUsername()));
        colEmail.setCellValueFactory(d ->
                new SimpleStringProperty(d.getValue().getEmail()));
        colRole.setCellValueFactory(d ->
                new SimpleStringProperty(d.getValue().getRole()));
        colJoin.setCellValueFactory(d ->
                new SimpleStringProperty(d.getValue().getJoinDate() != null ? d.getValue().getJoinDate().toString() : "-"));

        userTable.setItems(users);
        userTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        userTable.setRowFactory(tv -> new TableRow<>() {
            @Override protected void updateItem(User u, boolean empty) {
                super.updateItem(u, empty);
                if (u == null || empty) { 
                    setStyle(""); 
                    return; 
                }
                setStyle(u instanceof Manager ? "-fx-font-weight: bold;" : "");
            }
        });

        // Build detail grid rows
        nameLabel.setWrapText(true); emailLabel.setWrapText(true);
        addRow(0, "Name:", nameLabel);
        addRow(1, "Username:", userLabel);
        addRow(2, "Email:", emailLabel);
        addRow(3, "Role:", roleLabel);
        addRow(4, "Joined:", joindateLabel);

        roleFilter.getItems().addAll("All Users", "Members Only", "Managers Only");
        roleFilter.setValue("All Users");

        userTable.getSelectionModel().selectedItemProperty().addListener((obs, old, u) -> showDetail(u));
    }

    private void addRow(int row, String label, Label value) {
        Label lbl = new Label(label);
        lbl.setStyle("-fx-text-fill: #94a3b8;");
        lbl.setMinWidth(75);
        detailGrid.addRow(row, lbl, value);
    }

    private void showDetail(User u) {
        if (u == null) {
            nameLabel.setText("-"); 
            userLabel.setText("-"); 
            emailLabel.setText("-");
            roleLabel.setText("-"); 
            joindateLabel.setText("-"); 
            summaryLabel.setText("");
            return;
        }
        nameLabel.setText(u.getName());
        userLabel.setText(u.getUsername());
        emailLabel.setText(u.getEmail());
        roleLabel.setText(u.getRole());
        joindateLabel.setText(u.getJoinDate() != null ? u.getJoinDate().toString() : "-");
        summaryLabel.setText(u.getProfileSummary());
    }

    @FXML 
    private void handleRefresh() { 
        loadAll(); 
    }

    @FXML 
    private void handleFilterChange() {
        if (service == null) {
            return;
        }
        switch (roleFilter.getValue()) {
            case "Members Only" -> users.setAll(service.getAllMembers());
            case "Managers Only" -> users.setAll(service.getAllUsers().stream()
                    .filter(u -> u instanceof Manager).toList());
            default -> loadAll();
        }
    }

    @FXML 
    private void handleRemove() {
        User u = userTable.getSelectionModel().getSelectedItem();
        if (u == null) { 
            alert("Select a user first."); 
            return; 
        }
        new Alert(Alert.AlertType.CONFIRMATION, "Remove \"" + u.getName() + "\" (@" + u.getUsername() + ")?\n"
                        + "This will also delete their meetings, posts and notifications.",
                ButtonType.YES, ButtonType.NO).showAndWait().filter(r -> r == ButtonType.YES).ifPresent(r -> {
                    try { 
                        service.removeUser(u.getId()); 
                        loadAll(); 
                    }
                    catch (Exception ex) { 
                        alert(ex.getMessage()); 
                    }
                });
    }

    @FXML private void showRegisterDialog() {
        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle("Register New Manager");
        dialog.setHeaderText("Create a Manager account:");

        GridPane form = new GridPane();
        form.setHgap(12);
        form.setVgap(10);
        form.setPadding(new javafx.geometry.Insets(16));

        TextField nameF = new TextField();
        nameF.setPromptText("Full name");
        TextField userF = new TextField();
        userF.setPromptText("Username");
        TextField emailF = new TextField();
        emailF.setPromptText("Email address");
        PasswordField passF = new PasswordField();
        passF.setPromptText("Password");

        form.addRow(0, new Label("Name:"), nameF);
        form.addRow(1, new Label("Username:"), userF);
        form.addRow(2, new Label("Email:"), emailF);
        form.addRow(3, new Label("Password:"), passF);
        form.setPrefWidth(380);

        dialog.getDialogPane().setContent(form);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        dialog.setResultConverter(bt -> {
            if (bt != ButtonType.OK) {
                return null;
            }
            if (nameF.getText().isBlank() || userF.getText().isBlank() || emailF.getText().isBlank() || passF.getText().isBlank()) {
                alert("All fields are required.");
                return null;
            }
            try {
                service.registerUser(new Manager(nameF.getText().trim(), userF.getText().trim(), emailF.getText().trim(), passF.getText()));
                loadAll();
                alert("Manager account created successfully.");
            } catch (Exception ex) {
                alert(ex.getMessage());
            }
            return null;
        });
        dialog.showAndWait();
    }

    private void loadAll() {
        if (service != null) {
            users.setAll(service.getAllUsers());
        }
    }

    private void alert(String msg) {
        new Alert(Alert.AlertType.INFORMATION, msg, ButtonType.OK).showAndWait();
    }
}