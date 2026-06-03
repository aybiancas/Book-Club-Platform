package gui;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import model.*;
import services.BookClubService;
import java.time.DateTimeException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class MeetingController {

    @FXML 
    private Menu managerActionsMenu;
    @FXML 
    private TableView<Meeting> meetingTable;
    @FXML 
    private TableColumn<Meeting,String> colDate;
    @FXML 
    private TableColumn<Meeting,String> colTitle;
    @FXML 
    private TableColumn<Meeting,String> colVenue;
    @FXML 
    private TableColumn<Meeting,String> colBook;
    @FXML 
    private TableColumn<Meeting,String> colAttending;
    @FXML 
    private TableColumn<Meeting,String> colStatus;
    @FXML 
    private GridPane detailGrid;
    @FXML 
    private Label notesLabel;

    private BookClubService service;
    private User currentUser;

    private final Label titleLabel = new Label();
    private final Label dateLabel = new Label();
    private final Label venueLabel = new Label();
    private final Label bookLabel = new Label();
    private final Label statusLabel = new Label();
    private final Label organiserLabel = new Label();
    private final Label attendingLabel = new Label();

    private static final DateTimeFormatter formatter =
            DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    private final ObservableList<Meeting> meetings = FXCollections.observableArrayList();

    public void setService(BookClubService service, User currentUser) {
        this.service = service;
        this.currentUser = currentUser;
        if (currentUser instanceof Manager) {
            managerActionsMenu.setVisible(true);
        }
        loadMeetings(service.getUpcomingMeetings());
    }

    @FXML
    public void initialize() {
        colDate.setCellValueFactory(d -> 
                new SimpleStringProperty(d.getValue().getDateTime().format(formatter)));
        colTitle.setCellValueFactory(d -> 
                new SimpleStringProperty(d.getValue().getTitle()));
        colVenue.setCellValueFactory(d ->
                new SimpleStringProperty(d.getValue().getVenue() != null ? d.getValue().getVenue().getName() : "TBD"));
        colBook.setCellValueFactory(d ->
                new SimpleStringProperty(d.getValue().getFeaturedBook() != null ? d.getValue().getFeaturedBook().getTitle() : "—"));
        colAttending.setCellValueFactory(d ->
                new SimpleStringProperty(String.valueOf(d.getValue().getAttendeeIds().size())));
        colStatus.setCellValueFactory(d ->
                new SimpleStringProperty(d.getValue().getStatus().name()));

        meetingTable.setItems(meetings);
        meetingTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        meetingTable.setRowFactory(tv -> new TableRow<>() {
            @Override 
            protected void updateItem(Meeting m, boolean empty) {
                super.updateItem(m, empty);
                if (m == null || empty) { 
                    setStyle(""); 
                    return; 
                }
                switch (m.getStatus()) {
                    case CANCELLED -> setStyle("-fx-text-fill: #c0392b;");
                    case COMPLETED -> setStyle("-fx-text-fill: #7f8c8d;");
                    default -> setStyle("");
                }
            }
        });

        venueLabel.setWrapText(true); 
        bookLabel.setWrapText(true);
        titleLabel.setWrapText(true); 
        notesLabel.setWrapText(true);
        
        addDetailRow(0, "Title:", titleLabel);
        addDetailRow(1, "Date/Time:", dateLabel);
        addDetailRow(2, "Venue:", venueLabel);
        addDetailRow(3, "Book:", bookLabel);
        addDetailRow(4, "Status:", statusLabel);
        addDetailRow(5, "Organiser:", organiserLabel);
        addDetailRow(6, "Attendees:", attendingLabel);

        meetingTable.getSelectionModel().selectedItemProperty().addListener((obs, old, m) -> showDetail(m));
    }

    private void addDetailRow(int row, String labelText, Label value) {
        Label label = new Label(labelText);
        label.setStyle("-fx-text-fill: #94a3b8;");
        label.setMinWidth(80);
        detailGrid.addRow(row, label, value);
    }

    private void showDetail(Meeting m) {
        if (m == null) {
            titleLabel.setText("-"); 
            dateLabel.setText("-"); 
            venueLabel.setText("-");
            bookLabel.setText("-"); 
            statusLabel.setText("-"); 
            organiserLabel.setText("-");
            attendingLabel.setText("-"); 
            notesLabel.setText("-");
            return;
        }
        Venue v = m.getVenue();
        Book b = m.getFeaturedBook();
        String organiser;
        try { 
            organiser = service.getUser(m.getOrganiserId()).getName(); 
        } catch (Exception e) { 
            organiser = String.valueOf(m.getOrganiserId()); 
        }

        titleLabel.setText(m.getTitle());
        dateLabel.setText(m.getDateTime().format(formatter));
        venueLabel.setText(v != null ? v.getName() + "\n" + v.getAddress() : "TBD");
        bookLabel.setText(b != null ? b.getTitle() + "\nby " + b.getAuthor() : "-");
        statusLabel.setText(m.getStatus().name());
        organiserLabel.setText(organiser);
        attendingLabel.setText(m.getAttendeeIds().size() + " registered");
        notesLabel.setText(m.getNotes().isBlank() ? "(none)" : m.getNotes());
    }

    @FXML 
    private void showUpcoming() { 
        loadMeetings(service.getUpcomingMeetings()); 
    }
    
    @FXML 
    private void showAll() { 
        loadMeetings(service.getAllMeetings()); 
    }

    @FXML 
    private void handleAttend() {
        Meeting m = meetingTable.getSelectionModel().getSelectedItem();
        if (m == null) { 
            alert("Select a meeting first."); 
            return; 
        }
        try {
            service.addAttendeeToMeeting(m.getMeetingId(), currentUser.getId());
            alert("You are now attending: " + m.getTitle());
            loadMeetings(service.getUpcomingMeetings());
        } catch (Exception ex) { 
            alert(ex.getMessage()); 
        }
    }

    @FXML private void showScheduleDialog() {
        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle("Schedule New Meeting");
        dialog.setHeaderText("Fill in the meeting details:");

        GridPane form = new GridPane();
        form.setHgap(12);
        form.setVgap(10);
        form.setPadding(new javafx.geometry.Insets(16));

        TextField titleF = new TextField();
        TextField dateF = new TextField();
        dateF.setPromptText("dd/MM/yyyy HH:mm");

        ComboBox<Book> bookBox = new ComboBox<>();
        bookBox.getItems().addAll(service.getAllBooksSorted());
        bookBox.setConverter(new javafx.util.StringConverter<>() {
            public String toString(Book b) {
                return b == null ? "" : b.getTitle();
            }
            public Book fromString(String s) {
                return null;
            }
        });
        bookBox.setPromptText("Select book...");

        ComboBox<Venue> venueBox = new ComboBox<>();
        venueBox.getItems().addAll(service.getAllVenues());
        venueBox.setConverter(new javafx.util.StringConverter<>() {
            public String toString(Venue v) {
                return v == null ? "" : v.getName() + " (" + v.getVenueType() + ")";
            }
            public Venue fromString(String s) {
                return null;
            }
        });
        venueBox.setPromptText("Select venue...");

        form.addRow(0, new Label("Title:"), titleF);
        form.addRow(1, new Label("Date/Time:"), dateF);
        form.addRow(2, new Label("Book:"), bookBox);
        form.addRow(3, new Label("Venue:"), venueBox);
        form.setPrefWidth(400);

        dialog.getDialogPane().setContent(form);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        dialog.setResultConverter(bt -> {
            if (bt != ButtonType.OK) {
                return null;
            }
            if (titleF.getText().isBlank()) {
                alert("Title is required.");
                return null;
            }
            if (bookBox.getValue() == null) {
                alert("Select a book.");
                return null;
            }
            try {
                LocalDateTime dt = LocalDateTime.parse(dateF.getText().trim(), formatter);
                Venue v = venueBox.getValue();
                service.scheduleMeeting(currentUser.getId(), titleF.getText().trim(), v != null ? v.getVenueId() : 0, dt, bookBox.getValue().getIsbn());
                loadMeetings(service.getUpcomingMeetings());
                alert("Meeting scheduled!");
            } catch (DateTimeException ex) {
                alert("Use format: dd/MM/yyyy HH:mm");
            } catch (Exception ex) {
                alert(ex.getMessage());
            }
            return null;
        });
        dialog.showAndWait();
    }

    @FXML
    private void showRescheduleDialog() {
        Meeting m = meetingTable.getSelectionModel().getSelectedItem();
        if (m == null) {
            alert("Select a meeting first.");
            return;
        }
        TextInputDialog dialog = new TextInputDialog(m.getDateTime().format(formatter));
        dialog.setTitle("Reschedule Meeting");
        dialog.setHeaderText("New date and time for:\n" + m.getTitle());
        dialog.setContentText("Date/Time (dd/MM/yyyy HH:mm):");
        dialog.showAndWait().ifPresent(val -> {
            try {
                service.rescheduleMeeting(currentUser.getId(), m.getMeetingId(), LocalDateTime.parse(val.trim(), formatter));
                loadMeetings(service.getAllMeetings());
                alert("Meeting rescheduled.");
            } catch (Exception ex) {
                alert(ex.getMessage());
            }
        });
    }

    @FXML
    private void handleCancel() {
        Meeting m = meetingTable.getSelectionModel().getSelectedItem();
        if (m == null) {
            alert("Select a meeting first.");
            return;
        }
        new Alert(Alert.AlertType.CONFIRMATION, "Cancel \"" + m.getTitle() + "\"?", ButtonType.YES, ButtonType.NO)
                .showAndWait().filter(r -> r == ButtonType.YES).ifPresent(r -> {
                    try {
                        service.cancelMeeting(currentUser.getId(), m.getMeetingId());
                        loadMeetings(service.getAllMeetings());
                    }
                    catch (Exception ex) {
                        alert(ex.getMessage());
                    }
                });
    }

    @FXML private void showNotesDialog() {
        Meeting m = meetingTable.getSelectionModel().getSelectedItem();
        if (m == null) {
            alert("Select a meeting first.");
            return;
        }
        TextInputDialog dialog = new TextInputDialog(m.getNotes());
        dialog.setTitle("Meeting Notes");
        dialog.setHeaderText("Notes for: " + m.getTitle());
        dialog.showAndWait().ifPresent(notes -> {
            m.addNotes(notes);
            try {
                service.rescheduleMeeting(currentUser.getId(), m.getMeetingId(), m.getDateTime());
                notesLabel.setText(notes.isBlank() ? "(none)" : notes);
            } catch (Exception ex) {
                alert(ex.getMessage());
            }
        });
    }

    private void loadMeetings(List<Meeting> list) {
        meetings.setAll(list);
    }

    private void alert(String msg) {
        new Alert(Alert.AlertType.INFORMATION, msg, ButtonType.OK).showAndWait();
    }
}