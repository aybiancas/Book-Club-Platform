package repositories;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import config.*;
import model.Comment;
import model.Post;

public class PostRepository {

    private static PostRepository instance;

    private PostRepository() {}

    public static PostRepository getInstance() {
        if (instance == null) {
            instance = new PostRepository();
        }
        return instance;
    }

    private Connection connection() {
        return ConnectionProvider.getInstance().getConnection();
    }

    private Post mapPost(ResultSet rs) throws SQLException {
        Post post = new Post(rs.getString("title"), rs.getString("body"), rs.getInt("author_id"));
        post.restorePostId(rs.getInt("post_id"));
        if (rs.getBoolean("published")) post.publish();
        return post;
    }

    private Comment mapComment(ResultSet rs) throws SQLException {
        Comment comment = new Comment(rs.getInt("post_id"), rs.getInt("author_id"), rs.getString("text"));
        comment.setCommentId(rs.getInt("comment_id"));
        return comment;
    }

    private void loadComments(Post post) {
        String sql = "select * from comments where post_id = ? order by created_at";
        try (PreparedStatement preparedStatement = connection().prepareStatement(sql)) {

            preparedStatement.setInt(1, post.getPostId());
            try (ResultSet rs = preparedStatement.executeQuery()) {
                while (rs.next()) {
                    post.addComment(mapComment(rs));
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void addPost(Post post) {
        String sql = "insert into posts (title, body, author_id, published) values (?, ?, ?, ?)";
        try (PreparedStatement preparedStatement = connection().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            preparedStatement.setString(1, post.getTitle());
            preparedStatement.setString(2, post.getBody());
            preparedStatement.setInt(3, post.getAuthorId());
            preparedStatement.setBoolean(4, post.isPublished());
            preparedStatement.executeUpdate();

            try (ResultSet keys = preparedStatement.getGeneratedKeys()) {
                if (keys.next()) post.restorePostId(keys.getInt(1));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void updatePost(Post post) {
        String sql = "update posts set title = ?, body = ?, published = ?, updated_at = now() where post_id = ?";
        try (PreparedStatement preparedStatement = connection().prepareStatement(sql)) {

            preparedStatement.setString(1, post.getTitle());
            preparedStatement.setString(2, post.getBody());
            preparedStatement.setBoolean(3, post.isPublished());
            preparedStatement.setInt(4, post.getPostId());
            preparedStatement.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void deletePost(int postId) {
        String sql = "delete from posts where post_id = ?";
        try (PreparedStatement preparedStatement = connection().prepareStatement(sql)) {

            preparedStatement.setInt(1, postId);
            preparedStatement.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public Post findById(int postId) {
        String sql = "select * from posts where post_id = ?";
        try (PreparedStatement preparedStatement = connection().prepareStatement(sql)) {

            preparedStatement.setInt(1, postId);
            try (ResultSet rs = preparedStatement.executeQuery()) {
                if (rs.next()) {
                    Post post = mapPost(rs);
                    loadComments(post);
                    return post;
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<Post> findAll() {
        List<Post> list = new ArrayList<>();
        String sql = "select * from posts order by created_at desc";
        try (PreparedStatement preparedStatement = connection().prepareStatement(sql);
             ResultSet rs = preparedStatement.executeQuery()) {

            while (rs.next()) {
                Post post = mapPost(rs);
                loadComments(post);
                list.add(post);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public List<Post> findAllPublished() {
        List<Post> list = new ArrayList<>();
        String sql = "select * from posts where published = true order by created_at desc";
        try (PreparedStatement preparedStatement = connection().prepareStatement(sql);
             ResultSet rs = preparedStatement.executeQuery()) {

            while (rs.next()) {
                Post post = mapPost(rs);
                loadComments(post);
                list.add(post);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public void addComment(Comment comment) {
        String sql = "insert into comments (post_id, author_id, text, created_at, edited) values (?, ?, ?, ?, ?)";
        try (PreparedStatement preparedStatement = connection().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            preparedStatement.setInt(1, comment.getPostId());
            preparedStatement.setInt(2, comment.getAuthorId());
            preparedStatement.setString(3, comment.getText());
            preparedStatement.setTimestamp(4, Timestamp.valueOf(comment.getCreatedAt()));
            preparedStatement.setBoolean(5, comment.isEdited());
            preparedStatement.executeUpdate();

            try (ResultSet keys = preparedStatement.getGeneratedKeys()) {
                if (keys.next()) comment.setCommentId(keys.getInt(1));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void updateComment(Comment comment) {
        String sql = "update comments set text = ?, edited = true where comment_id = ?";
        try (PreparedStatement preparedStatement = connection().prepareStatement(sql)) {

            preparedStatement.setString(1, comment.getText());
            preparedStatement.setInt(2, comment.getCommentId());
            preparedStatement.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void deleteComment(int commentId) {
        String sql = "delete from comments where comment_id = ?";
        try (PreparedStatement preparedStatement = connection().prepareStatement(sql)) {

            preparedStatement.setInt(1, commentId);
            preparedStatement.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
