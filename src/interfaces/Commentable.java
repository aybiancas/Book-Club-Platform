package interfaces;

import model.Comment;
import java.util.List;

public interface Commentable {
    void addComment(Comment comment);

    List<Comment> getComments();

    void removeComment(int commentId);

}
