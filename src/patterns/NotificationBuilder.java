package patterns;

import model.NotificationType;
import model.Notification;

public class NotificationBuilder {

    private int recipientId;
    private NotificationType type = NotificationType.GENERAL;
    private String message = "";
    private int relatedEntityId = 0;

    public NotificationBuilder recipient(int recipientId) {
        this.recipientId = recipientId;
        return this;
    }

    public NotificationBuilder type(NotificationType type) {
        this.type = type;
        return this;
    }

    public NotificationBuilder message(String message) {
        this.message = message;
        return this;
    }

    public NotificationBuilder relatedEntity(int relatedEntityId) {
        this.relatedEntityId = relatedEntityId;
        return this;
    }

    public Notification build() {
        if (recipientId == 0) {
            throw new IllegalStateException("Notification must have a valid recipient id.");
        }
        return new Notification(recipientId, type, message, relatedEntityId);
    }
}
