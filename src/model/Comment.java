package model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

public class Comment {
    private int commentId;
    private final int postId;
    private final int authorId;
    private String text;
    private final LocalDateTime createdAt;
    private boolean edited;

    public Comment(int postId, int authorId, String text) {
        if (text == null || text.isBlank()) {
            throw new IllegalArgumentException("Comment text cannot be empty!");
        }
        this.commentId = 0;
        this.postId = postId;
        this.authorId = authorId;
        this.text = text;
        this.createdAt = LocalDateTime.now();
        this.edited = false;
    }

    public boolean isEdited() {
        return edited;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public String getText() {
        return text;
    }

    public int getAuthorId() {
        return authorId;
    }

    public int getPostId() {
        return postId;
    }

    public int getCommentId() {
        return commentId;
    }

    public void setCommentId(int commentId) {
        this.commentId = commentId;
    }

    public void editText(String t) {
        if (t == null || t.isBlank()) {
            throw new IllegalArgumentException("Comment cannot be empty!");
        }
        this.text = t;
        this.edited = true;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Comment)) {
            return false;
        }
        return commentId == ((Comment) o).commentId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(commentId);
    }

    @Override
    public String toString() {
        return "[" + createdAt.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"))
                + "] " + authorId + ": " + text + (edited ? " (edited)" : "");
    }
}
