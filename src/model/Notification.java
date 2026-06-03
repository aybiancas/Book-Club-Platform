package model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

public class Notification {
    private int notificationId;
    private final int recipientId;
    private final NotificationType type;
    private final String message;
    private final int relatedEntityId;
    private final LocalDateTime createdAt;
    private boolean read;

    public Notification(int recipientId, NotificationType type, String message, int relatedEntityId) {
        this.notificationId = 0;
        this.recipientId = recipientId;
        this.type = type;
        this.message = message;
        this.relatedEntityId = relatedEntityId;
        this.createdAt = LocalDateTime.now();
        this.read = false;
    }

    public Notification(int notificationId, int recipientId, NotificationType type, String message, int relatedEntityId, LocalDateTime createdAt) {
        this.notificationId = notificationId;
        this.recipientId = recipientId;
        this.type = type;
        this.message = message;
        this.relatedEntityId = relatedEntityId;
        this.createdAt = createdAt;
        this.read = false;
    }

    public int getNotificationId() {
        return notificationId;
    }

    public int getRecipientId() {
        return recipientId;
    }

    public NotificationType getType() {
        return type;
    }

    public String getMessage() {
        return message;
    }

    public int getRelatedEntityId() {
        return relatedEntityId;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public boolean isRead() {
        return read;
    }

    public void markAsRead() {
        this.read = true;
    }

    public void markAsUnread() {
        this.read = false;
    }

    public void restoreId(int id) {
        this.notificationId = id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Notification)) {
            return false;
        }
        return notificationId == ((Notification) o).notificationId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(notificationId);
    }

    @Override
    public String toString() {
        return "[" + type + "] " + createdAt.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"))
                + " — " + message + (read ? "" : " •");
    }
}
