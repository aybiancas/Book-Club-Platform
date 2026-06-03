package interfaces;

public interface Publishable {
    void publish();

    void unpublish();

    void editBody(String newBody);

    boolean isPublished();

}