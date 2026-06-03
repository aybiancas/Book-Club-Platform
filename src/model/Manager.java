package model;

import java.util.List;
import java.util.ArrayList;

public class Manager extends User {
    private final List<Integer> managedMeetingIds = new ArrayList<>();
    private final List<Integer> publishedPostIds = new ArrayList<>();

    public Manager(int id, String name, String username, String email, String password) {
        super (id, name, username, email, password);
    }

    public Manager(String name, String username, String email, String password) {
        super (name, username, email, password);
    }

    @Override
    public String getRole() {
        return "Manager";
    }

    @Override
    public String getProfileSummary() {
        return "Manager | " + managedMeetingIds.size() + " meetings | " + publishedPostIds.size() + " posts";
    }

    public void trackMeeting(int id) {
        if (!managedMeetingIds.contains(id)) {
            managedMeetingIds.add(id);
        }
    }

    public void untrackMeeting(int id) {
        managedMeetingIds.remove(id);
    }

    public void trackPost(int id) {
        if (!publishedPostIds.contains(id)) {
            publishedPostIds.add(id);
        }
    }

    public void untrackPost(int id) {
        publishedPostIds.remove(id);
    }

    public List<Integer> getManagedMeetingIds() {
        return new ArrayList<>(managedMeetingIds);
    }

    public List<Integer> getPublishedPostIds() {
        return new ArrayList<>(publishedPostIds);
    }

}
