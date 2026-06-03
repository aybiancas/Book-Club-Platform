package gui;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import model.*;
import services.BookClubService;
import java.util.List;

public class PostController {

    @FXML 
    private Menu managerMenu;
    @FXML 
    private TableView<Post> postTable;
    @FXML 
    private TableColumn<Post,String> colTitle;
    @FXML 
    private TableColumn<Post,String> colAuthor;
    @FXML 
    private TableColumn<Post,String> colPublished;
    @FXML 
    private Label postTitleLabel;
    @FXML 
    private Label postAuthorLabel;
    @FXML 
    private TextArea bodyArea;
    @FXML 
    private ListView<String> commentList;
    @FXML 
    private TextField commentField;

    private BookClubService service;
    private User currentUser;
    private final ObservableList<Post> posts = FXCollections.observableArrayList();
    private final ObservableList<String> comments = FXCollections.observableArrayList();

    public void setService(BookClubService service, User currentUser) {
        this.service = service;
        this.currentUser = currentUser;
        if (currentUser instanceof Manager) {
            managerMenu.setVisible(true);
        }
        loadPosts();
    }

    @FXML
    public void initialize() {
        colTitle.setCellValueFactory(d ->
                new SimpleStringProperty(d.getValue().getTitle()));
        colAuthor.setCellValueFactory(d -> {
            try {
                return new SimpleStringProperty(service.getUser(d.getValue().getAuthorId()).getName());
            } catch (Exception ex) {
                return new SimpleStringProperty(String.valueOf(d.getValue().getAuthorId()));
            }
        });
        colPublished.setCellValueFactory(d ->
                new SimpleStringProperty(d.getValue().isPublished() ? "✓ Published" : "✒ Draft"));

        postTable.setItems(posts);
        postTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        commentList.setItems(comments);

        postTable.getSelectionModel().selectedItemProperty().addListener((obs, old, post) -> {
            comments.clear();
            if (post == null) {
                postTitleLabel.setText(""); postAuthorLabel.setText(""); bodyArea.clear();
                return;
            }
            postTitleLabel.setText(post.getTitle());
            try {
                postAuthorLabel.setText("by " + service.getUser(post.getAuthorId()).getName());
            } catch (Exception ex) {
                postAuthorLabel.setText("by user #" + post.getAuthorId());
            }
            bodyArea.setText(post.getBody());
            bodyArea.setScrollTop(0);
            post.getComments().forEach(c -> comments.add(c.toString()));
        });
    }

    @FXML private void handleRefresh() { loadPosts(); }

    @FXML private void handlePostComment() {
        Post p = postTable.getSelectionModel().getSelectedItem();
        if (p == null) { 
            alert("Select a post first."); 
            return; 
        }
        String txt = commentField.getText().trim();
        if (txt.isBlank()) {
            return;
        }
        try {
            service.addCommentToPost(p.getPostId(), currentUser.getId(), txt);
            commentField.clear();
            comments.clear();
            service.getAllPosts().stream().filter(x -> x.getPostId() == p.getPostId())
                    .findFirst().ifPresent(refreshed ->
                            refreshed.getComments().forEach(c -> comments.add(c.toString())));
        } catch (Exception ex) { 
            alert(ex.getMessage()); 
        }
    }

    @FXML 
    private void showNewPostDialog() {
        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle("New Post");
        dialog.setHeaderText("Write a new post:");

        GridPane form = new GridPane();
        form.setHgap(10);
        form.setVgap(10);
        form.setPadding(new javafx.geometry.Insets(14));

        TextField titleF = new TextField(); titleF.setPromptText("Post title");
        TextArea bodyF = new TextArea();
        bodyF.setWrapText(true);
        bodyF.setPrefRowCount(7);
        bodyF.setPrefColumnCount(40);
        bodyF.setPromptText("Write your post here...");

        form.addRow(0, new Label("Title:"), titleF);
        form.addRow(1, new Label("Body:"),  bodyF);
        javafx.scene.layout.ColumnConstraints cc = new javafx.scene.layout.ColumnConstraints(60);
        javafx.scene.layout.ColumnConstraints cg = new javafx.scene.layout.ColumnConstraints();
        cg.setHgrow(javafx.scene.layout.Priority.ALWAYS); cg.setFillWidth(true);
        form.getColumnConstraints().addAll(cc, cg);
        dialog.getDialogPane().setContent(form);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        dialog.getDialogPane().setPrefWidth(520);

        dialog.setResultConverter(bt -> {
            if (bt != ButtonType.OK) {
                return null;
            }
            if (titleF.getText().isBlank() || bodyF.getText().isBlank()) {
                alert("Title and body are required.");
                return null;
            }
            try {
                service.publishPost(currentUser.getId(), titleF.getText().trim(), bodyF.getText().trim()); loadPosts();
            }
            catch (Exception ex) {
                alert(ex.getMessage());
            }
            return null;
        });
        dialog.showAndWait();
    }

    @FXML
    private void showEditPostDialog() {
        Post p = postTable.getSelectionModel().getSelectedItem();
        if (p == null) {
            alert("Select a post first.");
            return;
        }

        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle("Edit Post");

        GridPane form = new GridPane();
        form.setHgap(10);
        form.setVgap(10);
        form.setPadding(new javafx.geometry.Insets(14));

        TextField titleF = new TextField(p.getTitle());
        TextArea bodyF = new TextArea(p.getBody());
        bodyF.setWrapText(true);
        bodyF.setPrefRowCount(7);
        bodyF.setPrefColumnCount(40);

        form.addRow(0, new Label("Title:"), titleF);
        form.addRow(1, new Label("Body:"), bodyF);
        javafx.scene.layout.ColumnConstraints cc = new javafx.scene.layout.ColumnConstraints(60);
        javafx.scene.layout.ColumnConstraints cg = new javafx.scene.layout.ColumnConstraints();
        cg.setHgrow(javafx.scene.layout.Priority.ALWAYS); cg.setFillWidth(true);
        form.getColumnConstraints().addAll(cc, cg);
        dialog.getDialogPane().setContent(form);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        dialog.getDialogPane().setPrefWidth(520);

        dialog.setResultConverter(bt -> {
            if (bt != ButtonType.OK) {
                return null;
            }
            try {
                service.updatePost(currentUser.getId(), p.getPostId(), titleF.getText().trim(), bodyF.getText().trim()); loadPosts();
            }
            catch (Exception ex) {
                alert(ex.getMessage());
            }
            return null;
        });
        dialog.showAndWait();
    }

    @FXML
    private void handleDelete() {
        Post p = postTable.getSelectionModel().getSelectedItem();
        if (p == null) {
            alert("Select a post first.");
            return;
        }
        new Alert(Alert.AlertType.CONFIRMATION, "Delete \"" + p.getTitle() + "\"?", ButtonType.YES, ButtonType.NO)
                .showAndWait().filter(r -> r == ButtonType.YES).ifPresent(r -> {
                    try {
                        service.deletePost(currentUser.getId(), p.getPostId()); loadPosts();
                    }
                    catch (Exception ex) {
                        alert(ex.getMessage());
                    }
                });
    }

    private void loadPosts() {
        List<Post> list = (currentUser instanceof Manager) ? service.getAllPosts() : service.getAllPublishedPosts();
        posts.setAll(list);
    }

    private void alert(String msg) {
        new Alert(Alert.AlertType.INFORMATION, msg, ButtonType.OK).showAndWait();
    }
}