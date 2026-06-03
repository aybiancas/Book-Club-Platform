package repositories;

import config.*;
import model.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class BookRepository {
    private static BookRepository instance;
    private static final String select = """
            select b.isbn, b.title, b.author, b.no_of_pages, b.category,
            c.name as category_name, c.description as category_description, c.color_hex as category_color
            from books b left join categories c on c.category_id = b.category
            """;

    private BookRepository() {}

    public static BookRepository getInstance() {
        if (instance == null) instance = new BookRepository();
        return instance;
    }

    private Connection connection() {
        return ConnectionProvider.getInstance().getConnection();
    }

    private Book mapRow (ResultSet rs) throws SQLException {
        Category category = null;
        String categoryId = rs.getString("category");

        if (categoryId != null) {
            category = new Category(categoryId, rs.getString("category_name"),
                    rs.getString("category_description"), rs.getString("category_color"));
        }

        return new Book(rs.getString("isbn"), rs.getString("title"),
                rs.getString("author"), category, rs.getInt("no_of_pages"));
    }

    public void addBook (Book book) {
        String sql = "insert into books(isbn, title, author, no_of_pages, category) values (?, ?, ?, ?, ?)";

        try (PreparedStatement preparedStatement = connection().prepareStatement(sql)) {

            preparedStatement.setString(1, book.getIsbn());
            preparedStatement.setString(2, book.getTitle());
            preparedStatement.setString(3, book.getAuthor());
            preparedStatement.setInt(4, book.getNoOfPages());
            preparedStatement.setString(5, book.getCategory() != null ? book.getCategory().getCategoryId() : null);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void updateBook (Book book) {
        String sql = "update books set title=?, author=?, no_of_pages=?, category=? where isbn=?";
        try (PreparedStatement preparedStatement = connection().prepareStatement(sql)) {
            preparedStatement.setString(1, book.getTitle());
            preparedStatement.setString(2, book.getAuthor());
            preparedStatement.setInt(3, book.getNoOfPages());
            preparedStatement.setString(4, book.getCategory() != null ? book.getCategory().getCategoryId() : null);
            preparedStatement.setString(5, book.getIsbn());
            preparedStatement.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void deleteBook (String isbn) {
        String sql = "delete from books where isbn=?";
        try (PreparedStatement preparedStatement = connection().prepareStatement(sql)) {
            preparedStatement.setString(1, isbn);
            preparedStatement.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public Book findByIsbn (String isbn) {
        String sql = select + "where b.isbn = ?";
        try (PreparedStatement preparedStatement = connection().prepareStatement(sql)) {
            preparedStatement.setString(1, isbn);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    return mapRow(resultSet);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    public List<Book> findAll() {
        List<Book> list = new ArrayList<>();
        String sql = select + "order by b.title";

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

    public List<Book> findByTitle(String title) {
        List<Book> list = new ArrayList<>();
        String sql = select + "where lower(b.title) like lower(?) order by b.title";

        try (PreparedStatement preparedStatement = connection().prepareStatement(sql)) {
            preparedStatement.setString(1, "%" + title + "%");
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    list.add(mapRow(resultSet));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return list;
    }

    public List<Book> findByAuthor(String author) {
        List<Book> list = new ArrayList<>();
        String sql = select + "where lower(b.author) like lower(?) order by b.author";

        try (PreparedStatement preparedStatement = connection().prepareStatement(sql)) {
            preparedStatement.setString(1, "%" + author + "%");

            try (ResultSet rs = preparedStatement.executeQuery()) {
                while (rs.next()) {
                    list.add(mapRow(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return list;
    }

    public List<Book> findByCategory(String categoryId) {
        List<Book> list = new ArrayList<>();
        String sql = select + "where b.category = ? order by b.title";

        try (PreparedStatement preparedStatement = connection().prepareStatement(sql)) {
            preparedStatement.setString(1, categoryId);

            try (ResultSet rs = preparedStatement.executeQuery()) {
                while (rs.next()) {
                    list.add(mapRow(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return list;
    }

    public void addToReadingList(int memberId, String isbn) {
        String sql = "insert into reading_list (user_id, isbn) values (?, ?) on conflict do nothing";
        try (PreparedStatement preparedStatement = connection().prepareStatement(sql)) {

            preparedStatement.setInt   (1, memberId);
            preparedStatement.setString(2, isbn);
            preparedStatement.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void removeFromReadingList(int memberId, String isbn) {
        String sql = "delete from reading_list where user_id = ? and isbn = ?";
        try (PreparedStatement preparedStatement = connection().prepareStatement(sql)) {

            preparedStatement.setInt   (1, memberId);
            preparedStatement.setString(2, isbn);
            preparedStatement.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<Book> findReadingListForMember(int memberId) {
        List<Book> list = new ArrayList<>();
        String sql = select + "join reading_list rl on rl.isbn = b.isbn where rl.user_id = ? order by b.title";
        try (PreparedStatement preparedStatement = connection().prepareStatement(sql)) {

            preparedStatement.setInt(1, memberId);
            try (ResultSet rs = preparedStatement.executeQuery()) {
                while (rs.next()) list.add(mapRow(rs));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }
}
