package repositories;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import config.*;
import model.Category;

public class CategoryRepository {

    private static CategoryRepository instance;

    private CategoryRepository() {}

    public static CategoryRepository getInstance() {
        if (instance == null) {
            instance = new CategoryRepository();
        }
        return instance;
    }

    private Connection connection() {
        return ConnectionProvider.getInstance().getConnection();
    }

    private Category mapRow(ResultSet rs) throws SQLException {
        return new Category(rs.getString("category_id"), rs.getString("name"),
                rs.getString("description"), rs.getString("color_hex"));
    }

    public void addCategory(Category category) {
        String sql = "insert into categories (category_id, name, description, color_hex) values (?, ?, ?, ?) on conflict (category_id) do nothing";
        try (PreparedStatement preparedStatement = connection().prepareStatement(sql)) {

            preparedStatement.setString(1, category.getCategoryId());
            preparedStatement.setString(2, category.getName());
            preparedStatement.setString(3, category.getDescription());
            preparedStatement.setString(4, category.getColorHex());
            preparedStatement.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void updateCategory(Category category) {
        String sql = "update categories set name = ?, description = ?, color_hex = ? where category_id = ?";
        try (PreparedStatement preparedStatement = connection().prepareStatement(sql)) {

            preparedStatement.setString(1, category.getName());
            preparedStatement.setString(2, category.getDescription());
            preparedStatement.setString(3, category.getColorHex());
            preparedStatement.setString(4, category.getCategoryId());
            preparedStatement.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void deleteCategory(String categoryId) {
        String sql = "delete from categories where category_id = ?";
        try (PreparedStatement preparedStatement = connection().prepareStatement(sql)) {

            preparedStatement.setString(1, categoryId);
            preparedStatement.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public Category findById(String categoryId) {
        String sql = "select * from categories where category_id = ?";
        try (PreparedStatement preparedStatement = connection().prepareStatement(sql)) {

            preparedStatement.setString(1, categoryId);
            try (ResultSet rs = preparedStatement.executeQuery()) {
                if (rs.next()) return mapRow(rs);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<Category> findAll() {
        List<Category> list = new ArrayList<>();
        String sql = "select * from categories order by name";
        try (PreparedStatement preparedStatement = connection().prepareStatement(sql);
             ResultSet rs = preparedStatement.executeQuery()) {

            while (rs.next()) list.add(mapRow(rs));

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }
}
