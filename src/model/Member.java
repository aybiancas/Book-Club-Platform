package model;

import java.util.TreeSet;

public class Member extends User {

    private final TreeSet<Book> readingList;

    public Member (int id, String name, String username, String email, String password) {
        super (id, name, username, email, password);
    }

    public Member (String name, String username, String email, String password) {
        super (name, username, email, password);
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
}
