package model;

import interfaces.Commentable;
import interfaces.Publishable;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Post implements Publishable, Commentable {
    private int postId;
    private String title;
    private String body;
    private final int authorId;
    private final LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private boolean published;
    private final List<Comment> comments;

    public Post(String title, String body, int authorId) {
        this.postId = 0;
        this.title = title;
        this.body = body;
        this.authorId = authorId;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = this.createdAt;
        this.published = false;
        this.comments = new ArrayList<>();
    }

    public int getPostId() {
        return postId;
    }

    public String getTitle() {
        return title;
    }

    public String getBody() {
        return body;
    }

    public int getAuthorId() {
        return authorId;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void restorePostId(int id)  {
        this.postId = id;
    }

    @Override
    public void addComment(Comment c) {
        comments.add(c);
    }

    @Override
    public List<Comment> getComments() {
        return new ArrayList<>(comments);
    }

    @Override
    public void removeComment(int commentId) {
        comments.removeIf(c -> c.getCommentId() == commentId);
    }

    @Override
    public void publish() {
        this.published = true;
        this.updatedAt = LocalDateTime.now();
    }

    @Override
    public void unpublish() {
        this.published = false;
        this.updatedAt = LocalDateTime.now();
    }

    @Override
    public void editBody(String newBody) {
        this.body = newBody;
        this.updatedAt = LocalDateTime.now();
    }

    @Override
    public boolean isPublished() {
        return published;
    }

    public void editTitle(String t) {
        this.title = t;
        this.updatedAt = LocalDateTime.now();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Post)) {
            return false;
        }
        return postId == ((Post) o).postId;
    }
    @Override
    public int hashCode() {
        return Objects.hash(postId);
    }

    @Override
    public String toString() {
        return "[" + postId + "] \"" + title + "\" | published=" + published;
    }

}
