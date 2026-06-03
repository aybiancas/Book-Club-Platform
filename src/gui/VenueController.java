package gui;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import model.*;
import services.BookClubService;

public class VenueController {

    @FXML private Menu managerMenu;
    @FXML private TableView<Venue> venueTable;
    @FXML private TableColumn<Venue,String> colName;
    @FXML private TableColumn<Venue,String> colType;
    @FXML private TableColumn<Venue,String> colAddress;
    @FXML private TableColumn<Venue,String> colCapacity;
    @FXML private GridPane detailGrid;

    private BookClubService service;
    private User currentUser;

    private final Label nameLabel = new Label();
    private final Label typeLabel = new Label();
    private final Label addressLabel = new Label();
    private final Label capacityLabel  = new Label();

    private final ObservableList<Venue> venues = FXCollections.observableArrayList();

    public void setService(BookClubService service, User currentUser) {
        this.service = service;
        this.currentUser = currentUser;
        if (currentUser instanceof Manager) {
            managerMenu.setVisible(true);
        }
        loadVenues();
    }

    @FXML
    public void initialize() {
        colName.setCellValueFactory(d -> 
                new SimpleStringProperty(d.getValue().getName()));
        colType.setCellValueFactory(d -> 
                new SimpleStringProperty(d.getValue().getVenueType().name()));
        colAddress.setCellValueFactory(d -> 
                new SimpleStringProperty(d.getValue().getAddress() != null ? d.getValue().getAddress() : "-"));
        colCapacity.setCellValueFactory(d -> 
                new SimpleStringProperty(d.getValue().getCapacity() == 0 ? "Unlimited" : String.valueOf(d.getValue().getCapacity())));

        venueTable.setItems(venues);
        venueTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        nameLabel.setWrapText(true); addressLabel.setWrapText(true);
        addRow(0, "Name:", nameLabel);
        addRow(1, "Type:", typeLabel);
        addRow(2, "Address:", addressLabel);
        addRow(3, "Capacity:", capacityLabel);

        venueTable.getSelectionModel().selectedItemProperty().addListener((obs, old, v) -> showDetail(v));
    }

    private void addRow(int row, String label, Label value) {
        Label lbl = new Label(label);
        lbl.setStyle("-fx-text-fill: #94a3b8;");
        lbl.setMinWidth(70);
        detailGrid.addRow(row, lbl, value);
    }

    private void showDetail(Venue v) {
        if (v == null) {
            nameLabel.setText("-"); 
            typeLabel.setText("-");
            addressLabel.setText("-"); 
            capacityLabel.setText("-");
            return;
        }
        nameLabel.setText(v.getName());
        typeLabel.setText(v.getVenueType().name());
        addressLabel.setText(v.getAddress() != null ? v.getAddress() : "-");
        capacityLabel.setText(v.getCapacity() == 0 ? "Unlimited" : v.getCapacity() + " people");
    }

    @FXML 
    private void handleRefresh() { 
        loadVenues(); 
    }

    @FXML 
    private void showAddDialog() { 
        openDialog(null); 
    }
    
    @FXML 
    private void showEditDialog() {
        Venue v = venueTable.getSelectionModel().getSelectedItem();
        if (v == null) { 
            alert("Select a venue first."); 
            return; 
        }
        openDialog(v);
    }

    private void openDialog(Venue existing) {
        boolean editing = existing != null;
        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle(editing ? "Edit Venue" : "Add New Venue");
        dialog.setHeaderText(editing ? "Update venue details:" : "Enter venue details:");

        GridPane form = new GridPane();
        form.setHgap(12);
        form.setVgap(10);
        form.setPadding(new javafx.geometry.Insets(16));

        TextField nameF = new TextField(editing ? existing.getName() : "");
        nameF.setPromptText("e.g. City Library Hall");
        TextField addrF = new TextField(editing && existing.getAddress() != null ? existing.getAddress() : "");
        addrF.setPromptText("e.g. Str. Unirii 10");
        TextField capF  = new TextField(editing ? String.valueOf(existing.getCapacity()) : "0");
        capF.setPromptText("0 = unlimited");

        ComboBox<VenueType> typeBox = new ComboBox<>();
        typeBox.getItems().addAll(VenueType.values());
        typeBox.setValue(editing ? existing.getVenueType() : VenueType.Physical);

        form.addRow(0, new Label("Name:"), nameF);
        form.addRow(1, new Label("Address:"), addrF);
        form.addRow(2, new Label("Capacity:"), capF);
        form.addRow(3, new Label("Type:"), typeBox);
        form.setPrefWidth(380);

        dialog.getDialogPane().setContent(form);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        dialog.setResultConverter(bt -> {
            if (bt != ButtonType.OK) {
                return null;
            }
            if (nameF.getText().isBlank()) {
                alert("Venue name is required.");
                return null;
            }
            int cap;
            try {
                cap = Integer.parseInt(capF.getText().trim());
            }
            catch (NumberFormatException ex) {
                alert("Capacity must be a number.");
                return null;
            }
            try {
                if (editing) {
                    existing.setName(nameF.getText().trim());
                    existing.setAddress(addrF.getText().trim());
                    existing.setCapacity(cap);
                    existing.setVenueType(typeBox.getValue());
                    service.updateVenue(existing);
                }
                else {
                    service.addVenue(new Venue(nameF.getText().trim(), addrF.getText().trim(), cap, typeBox.getValue()));
                }
                loadVenues();
            } catch (Exception ex) {
                alert(ex.getMessage());
            }
            return null;
        });
        dialog.showAndWait();
    }

    @FXML
    private void handleDelete() {
        Venue v = venueTable.getSelectionModel().getSelectedItem();
        if (v == null) {
            alert("Select a venue first.");
            return;
        }
        new Alert(Alert.AlertType.CONFIRMATION, "Delete venue \"" + v.getName() + "\"?\nThis may affect existing meetings.",
                ButtonType.YES, ButtonType.NO).showAndWait().filter(r -> r == ButtonType.YES).ifPresent(r -> {
                    try {
                        service.deleteVenue(v.getVenueId());
                        loadVenues();
                    }
                    catch (Exception ex) {
                        alert(ex.getMessage());
                    }
                });
    }

    private void loadVenues() {
        venues.setAll(service.getAllVenues());
    }

    private void alert(String msg) {
        new Alert(Alert.AlertType.INFORMATION, msg, ButtonType.OK).showAndWait();
    }
}