package repositories;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import config.*;
import model.NotificationType;
import model.Notification;

public class NotificationRepository {

    private static NotificationRepository instance;

    private NotificationRepository() {}

    public static NotificationRepository getInstance() {
        if (instance == null) {
            instance = new NotificationRepository();
        }
        return instance;
    }

    private Connection connection() {
        return ConnectionProvider.getInstance().getConnection();
    }

    private Notification mapRow(ResultSet rs) throws SQLException {
        Notification n = new Notification(rs.getInt("notification_id"), rs.getInt("recipient_id"),
                NotificationType.valueOf(rs.getString("type")), rs.getString("message"),
                rs.getInt("related_entity_id"), rs.getTimestamp("created_at").toLocalDateTime());
        if (rs.getBoolean("is_read")) {
            n.markAsRead();
        }
        return n;
    }

    public void addNotification(Notification notification) {
        String sql = "insert into notifications (recipient_id, type, message, related_entity_id, created_at, is_read) values (?, ?::notification_type_enum, ?, ?, ?, ?)";
        try (PreparedStatement preparedStatement = connection().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            preparedStatement.setInt(1, notification.getRecipientId());
            preparedStatement.setString(2, notification.getType().name());
            preparedStatement.setString(3, notification.getMessage());
            int rel = notification.getRelatedEntityId();
            if (rel > 0) {
                preparedStatement.setInt(4, rel);
            } else {
                preparedStatement.setNull(4, Types.INTEGER);
            }
            preparedStatement.setTimestamp(5, Timestamp.valueOf(notification.getCreatedAt()));
            preparedStatement.setBoolean(6, notification.isRead());
            preparedStatement.executeUpdate();

            try (ResultSet keys = preparedStatement.getGeneratedKeys()) {
                if (keys.next()) notification.restoreId(keys.getInt(1));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void deleteNotification(int notificationId) {
        String sql = "delete from notifications where notification_id = ?";
        try (PreparedStatement preparedStatement = connection().prepareStatement(sql)) {

            preparedStatement.setInt(1, notificationId);
            preparedStatement.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<Notification> findByRecipient(int userId) {
        List<Notification> list = new ArrayList<>();
        String sql = "select * from notifications where recipient_id = ? order by created_at desc";
        try (PreparedStatement preparedStatement = connection().prepareStatement(sql)) {

            preparedStatement.setInt(1, userId);
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

    public void markAllReadForUser(int userId) {
        String sql = "update notifications set is_read = true where recipient_id = ?";
        try (PreparedStatement preparedStatement = connection().prepareStatement(sql)) {

            preparedStatement.setInt(1, userId);
            preparedStatement.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<Notification> findAll() {
        List<Notification> list = new ArrayList<>();
        String sql = "select * from notifications order by created_at desc";
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
}
