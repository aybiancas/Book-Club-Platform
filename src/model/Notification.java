package model;

import java.time.LocalDateTime;
import enums.NotificationType;

public class Notification {
    private final String notificationId;
    private final String recipientId;
    private final NotificationType type;
    private final String message;
    private final String relatedEntityId;
    private final LocalDateTime createdAt;
    private boolean read;

    public Notification(String notificationId, String recipientId, NotificationType type, String message, String relatedEntityId, LocalDateTime createdAt) {
        this.notificationId = notificationId;
        this.recipientId = recipientId;
        this.type = type;
        this.message = message;
        this.relatedEntityId = relatedEntityId;
        this.createdAt = LocalDateTime.now();
        this.read = false;
    }

    public void markAsRead() {
        this.read = true;
    }

    public void markAsUnread() {
        this.read = false;
    }
}
