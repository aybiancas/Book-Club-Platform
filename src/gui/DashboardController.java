package gui;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import model.*;
import services.BookClubService;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class DashboardController {

    @FXML 
    private Label avatarLabel;
    @FXML 
    private Label nameLabel;
    @FXML 
    private Label roleLabel;
    @FXML 
    private Label summaryLabel;
    @FXML 
    private HBox statsRow;

    @FXML 
    private ListView<String> meetingsList;
    @FXML 
    private ListView<String> postsList;

    @FXML 
    private HBox memberBottomRow;
    @FXML 
    private ListView<String> readingList;
    @FXML 
    private ListView<String> notifList;
    @FXML 
    private Label unreadBadge;
    @FXML 
    private Button markAllBtn;

    private BookClubService service;
    private User currentUser;
    private Runnable onLogout;

    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd MMM yyyy  HH:mm");

    public void setService(BookClubService service, User currentUser, Runnable onLogout) {
        this.service = service;
        this.currentUser = currentUser;
        this.onLogout = onLogout;
        populate();
    }

    @FXML
    public void initialize() {
    }

    private void populate() {
//        avatarLabel.setText(currentUser instanceof Manager ? "👔" : "📖");
        avatarLabel.setText(currentUser instanceof Manager ? "✩" : "●");

        nameLabel.setText("Welcome back, " + currentUser.getName() + "!");
        roleLabel.setText(currentUser.getRole() + "  •  " + currentUser.getEmail());
        summaryLabel.setText(currentUser.getProfileSummary());

        List<Meeting> upcoming = service.getUpcomingMeetings();
        List<Post> posts = service.getAllPublishedPosts();
        int rlSize = service.getReadingList(currentUser.getId()).size();
        List<Notification> notifs = service.getNotificationsForUser(currentUser.getId());
        long unreadCount = notifs.stream().filter(n -> !n.isRead()).count();

        statsRow.getChildren().addAll(
                statCard("⏲", String.valueOf(upcoming.size()), "Upcoming Meetings"),
                statCard("✉", String.valueOf(posts.size()), "Published Posts"),
                statCard("✓", String.valueOf(rlSize), "My Book List"),
                statCard("✦", String.valueOf(unreadCount), "Unread Notifications")
        );
        for (javafx.scene.Node n : statsRow.getChildren()) {
            HBox.setHgrow(n, Priority.ALWAYS);
        }

        // Meetings list
        ObservableList<String> meetItems = FXCollections.observableArrayList();
        if (upcoming.isEmpty()) {
            meetItems.add("No upcoming meetings.");
        }
        else {
            upcoming.forEach(m -> meetItems.add(m.getDateTime().format(formatter) + "  -  " + m.getTitle()));
        }
        meetingsList.setItems(meetItems);

        // Posts list
        ObservableList<String> postItems = FXCollections.observableArrayList();
        if (posts.isEmpty()) {
            postItems.add("No posts yet.");
        }
        else {
            posts.stream().limit(10).forEach(p -> postItems.add("• " + p.getTitle()));
        }
        postsList.setItems(postItems);

        // Member only section
        if (currentUser instanceof Member) {
            memberBottomRow.setVisible(true);
            memberBottomRow.setManaged(true);

            ObservableList<String> rlItems = FXCollections.observableArrayList();
            var rl = service.getReadingList(currentUser.getId());
            if (rl.isEmpty()) {
                rlItems.add("Your reading list is empty.");
            }
            else {
                rl.forEach(b -> rlItems.add(b.getTitle() + "  -  " + b.getAuthor()));
            }
            readingList.setItems(rlItems);

            loadNotifications(notifs, unreadCount);
        }
    }

    private void loadNotifications(List<Notification> notifs, long unreadCount) {
        ObservableList<String> notifItems = FXCollections.observableArrayList();
        if (notifs.isEmpty()) {
            notifItems.add("No notifications.");
        }
        else notifs.stream().limit(15).forEach(n -> notifItems.add((n.isRead() ? "   " : "● ") + n));
        notifList.setItems(notifItems);

        if (unreadCount > 0) {
            unreadBadge.setText(unreadCount + " unread");
            unreadBadge.setStyle("-fx-background-color: #ef4444; -fx-text-fill: white; "
                    + "-fx-background-radius: 12; -fx-padding: 3 10 3 10; "
                    + "-fx-font-size: 11; -fx-font-weight: bold;");
        }
        else {
            unreadBadge.setText("All read");
            unreadBadge.setStyle("-fx-background-color: #22c55e; -fx-text-fill: white; "
                    + "-fx-background-radius: 12; -fx-padding: 3 10 3 10; "
                    + "-fx-font-size: 11; -fx-font-weight: bold;");
        }
    }

    @FXML
    private void handleLogout() {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION, "Are you sure you want to log out?", ButtonType.YES, ButtonType.NO);
        confirm.setTitle("Log Out");
        confirm.showAndWait()
                .filter(r -> r == ButtonType.YES)
                .ifPresent(r -> onLogout.run());
    }

    @FXML
    private void handleMarkAllRead() {
        service.markAllNotificationsRead(currentUser.getId());
        unreadBadge.setText("All read");
        unreadBadge.setStyle("-fx-background-color: #22c55e; -fx-text-fill: white; "
                + "-fx-background-radius: 12; -fx-padding: 3 10 3 10; "
                + "-fx-font-size: 11; -fx-font-weight: bold;");
        ObservableList<String> items = FXCollections.observableArrayList();
        service.getNotificationsForUser(currentUser.getId()).stream().limit(15).forEach(n -> items.add("   " + n));
        notifList.setItems(items);
    }

    private VBox statCard(String icon, String value, String label) {
        VBox card = new VBox(4);
        card.setStyle("-fx-background-color: white; -fx-background-radius: 8; "
                + "-fx-border-color: #e2e8f0; -fx-border-radius: 8; "
                + "-fx-border-width: 1; -fx-padding: 14;");
        card.setAlignment(Pos.CENTER_LEFT);

        HBox row = new HBox(8);
        row.setAlignment(Pos.CENTER_LEFT);
        Label iconLbl = new Label(icon);
        iconLbl.setStyle("-fx-font-size: 20;");
        Label valueLbl = new Label(value);
        valueLbl.setFont(Font.font("SansSerif", FontWeight.BOLD, 24));

        row.getChildren().addAll(iconLbl, valueLbl);

        Label desc = new Label(label);
        desc.setStyle("-fx-text-fill: #94a3b8; -fx-font-size: 12;");
        card.getChildren().addAll(row, desc);
        return card;
    }
}