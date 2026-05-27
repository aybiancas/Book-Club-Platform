package model;

import java.util.*;

public class Member extends User {

    private final TreeSet<Book> readingList;
    private final List<String> attendedMeetings; // the ids of the meetings
    private final List<Notification> notifications;

    public Member (int id, String name, String username, String email, String password) {
        super (id, name, username, email, password);
        this.readingList = new TreeSet<>();
        this.attendedMeetings = new ArrayList<>();
        this.notifications = new ArrayList<>();
    }

    public Member (String name, String username, String email, String password) {
        super (name, username, email, password);
        this.readingList = new TreeSet<>();
        this.attendedMeetings = new ArrayList<>();
        this.notifications = new ArrayList<>();
    }

    @Override
    public String getRole() {
        return "Member";
    }

//    @Override
//    public String getProfileSummary() {
//        long unread = notifications.stream().filter(n -> !n.isRead()).count();
//        return String.format("Member since %d | %d books in list | %d unread notifications",
//                joinYear, readingList.size(), unread);
//    }

    public boolean addToReadingList(Book book) {
        return readingList.add(book);
    }

    public boolean removeFromReadingList(Book book) {
        return readingList.remove(book);
    }

    public boolean isInReadingList(Book book) {
        return readingList.contains(book);
    }

    public SortedSet<Book> getReadingList() {
        return Collections.unmodifiableSortedSet(readingList);
    }

    public int readingListSize() {
        return readingList.size();
    }

    public void attendMeeting(String meetingId) {
        if (!attendedMeetings.contains(meetingId)) {
            attendedMeetings.add(meetingId);
        }
    }
    public void leaveMeeting(String meetingId) {
        attendedMeetings.remove(meetingId);
    }

    public List<String> getAttendedMeetingIds() {
        return new ArrayList<>(attendedMeetings);
    }

    public void addNotification(Notification n) {
        notifications.add(n);
    }

    public void markAllNotificationsRead() {
        notifications.forEach(Notification::markAsRead);
    }

    public List<Notification> getNotifications() {
        return new ArrayList<>(notifications);
    }

    public List<Notification> getUnreadNotifications() {
        List<Notification> unread = new ArrayList<>();
        for (Notification n : notifications) if (!n.isRead()) unread.add(n);
        return unread;
    }
}
