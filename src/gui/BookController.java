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

public class BookController {

    @FXML
    private Menu managerMenu;
    @FXML
    private TextField searchField;
    @FXML
    private TableView<Book> bookTable;
    @FXML
    private TableColumn<Book,String> colTitle;
    @FXML
    private TableColumn<Book,String> colAuthor;
    @FXML
    private TableColumn<Book,String> colCat;
    @FXML
    private TableColumn<Book,String> colPages;
    @FXML
    private TableColumn<Book,String> colIsbn;
    @FXML
    private Label detailTitle;
    @FXML
    private Label detailBody;

    private BookClubService service;
    private User currentUser;
    private final ObservableList<Book> books = FXCollections.observableArrayList();

    public void setService(BookClubService service, User currentUser) {
        this.service = service;
        this.currentUser = currentUser;
        if (currentUser instanceof Manager) {
            managerMenu.setVisible(true);
        }
        loadBooks(service.getAllBooksSorted());
    }

    @FXML
    public void initialize() {
        colTitle.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getTitle()));
        colAuthor.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getAuthor()));
        colCat.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getCategory() != null ? d.getValue().getCategory().getName() : "-"));
        colPages.setCellValueFactory(d -> new SimpleStringProperty(String.valueOf(d.getValue().getNoOfPages())));
        colIsbn.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getIsbn()));

        bookTable.setItems(books);
        bookTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        bookTable.getSelectionModel().selectedItemProperty().addListener((obs, old, book) -> {
            if (book == null) {
                detailTitle.setText("Select a book to see details");
                detailBody.setText("");
                return;
            }
            detailTitle.setText(book.getTitle());
            detailBody.setText("Author   : " + book.getAuthor() + "\n"
                    + "Category : " + (book.getCategory() != null ? book.getCategory().getName() : "—") + "\n"
                    + "Pages    : " + book.getNoOfPages() + "\n"
                    + "ISBN     : " + book.getIsbn());
        });
    }

    @FXML
    private void handleRefresh() {
        loadBooks(service.getAllBooksSorted());
    }

    @FXML
    private void handleClear() {
        searchField.clear();
        handleRefresh();
    }

    @FXML private void handleSearch() {
        String q = searchField.getText().trim();
        if (!q.isBlank()) loadBooks(service.searchBooks(q));
    }

    @FXML private void handleAddToList() {
        Book b = bookTable.getSelectionModel().getSelectedItem();
        if (b == null) {
            alert("Select a book first.");
            return;
        }
        try {
            service.addBookToReadingList(currentUser.getId(), b.getIsbn());
            alert("\"" + b.getTitle() + "\" added to your reading list.");
        } catch (Exception ex) {
            alert(ex.getMessage());
        }
    }

    @FXML
    private void handleRemoveFromList() {
        Book b = bookTable.getSelectionModel().getSelectedItem();
        if (b == null) {
            alert("Select a book first.");
            return;
        }
        try {
            service.removeBookFromReadingList(currentUser.getId(), b.getIsbn());
            alert("Removed from reading list.");
        } catch (Exception ex) {
            alert(ex.getMessage());
        }
    }

    @FXML private void handleRemoveBook() {
        Book b = bookTable.getSelectionModel().getSelectedItem();
        if (b == null) {
            alert("Select a book first.");
            return;
        }
        new Alert(Alert.AlertType.CONFIRMATION, "Remove \"" + b.getTitle() + "\"?", ButtonType.YES, ButtonType.NO)
                .showAndWait().filter(r -> r == ButtonType.YES).ifPresent(r -> {
                    service.removeBook(b.getIsbn());
                    loadBooks(service.getAllBooksSorted());
                });
    }

    @FXML private void showAddBookDialog() {
        Dialog<Book> dlg = new Dialog<>();
        dlg.setTitle("Add New Book");
        dlg.setHeaderText("Enter book details:");

        GridPane form = new GridPane();
        form.setHgap(10); form.setVgap(10);
        form.setPadding(new javafx.geometry.Insets(14));

        TextField isbnF = new TextField(); isbnF.setPromptText("e.g. 9780140283297");
        TextField titleF = new TextField();
        TextField authF = new TextField();
        TextField pagesF = new TextField(); pagesF.setPromptText("number");

        ComboBox<Category> catBox = new ComboBox<>();
        catBox.getItems().addAll(service.getAllCategories());
        catBox.setConverter(new javafx.util.StringConverter<>() {
            public String toString(Category c) {
                return c == null ? "- none -" : c.getName();
            }
            public Category fromString(String s) {
                return null;
            }
        });
        catBox.setPromptText("Select category (optional)");

        form.addRow(0, new Label("ISBN:"), isbnF);
        form.addRow(1, new Label("Title:"), titleF);
        form.addRow(2, new Label("Author:"), authF);
        form.addRow(3, new Label("Pages:"), pagesF);
        form.addRow(4, new Label("Category:"), catBox);
        form.setPrefWidth(380);

        dlg.getDialogPane().setContent(form);
        dlg.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        dlg.setResultConverter(bt -> {
            if (bt != ButtonType.OK) return null;
            if (isbnF.getText().isBlank() || titleF.getText().isBlank() || authF.getText().isBlank()) {
                alert("ISBN, title and author are required.");
                return null;
            }
            try {
                return new Book(isbnF.getText().trim(), titleF.getText().trim(),
                        authF.getText().trim(), catBox.getValue(),
                        Integer.parseInt(pagesF.getText().trim()));
            } catch (NumberFormatException ex) {
                alert("Pages must be a number.");
                return null;
            }
        });

        dlg.showAndWait().ifPresent(b -> {
            try {
                service.addBook(b);
                loadBooks(service.getAllBooksSorted());
            } catch (Exception ex) {
                alert(ex.getMessage());
            }
        });
    }

    private void loadBooks(List<Book> list) {
        books.setAll(list);
    }

    private void alert(String msg) {
        new Alert(Alert.AlertType.INFORMATION, msg, ButtonType.OK).showAndWait();
    }
}