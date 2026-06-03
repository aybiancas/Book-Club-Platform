import config.*;
import model.*;
import services.*;
import java.time.LocalDateTime;
import java.util.SortedSet;

public class Main {
    public static void main(String[] args) {

        /// ///////// DEMO ///////////////////
//        // placeholder //
//        BookClubService service = new BookClubService();
//        System.out.println("========== BOOK CLUB PLATFORM — DEMO ==========\n");
//
//        // 1. Register users
//        System.out.println("--- Registering users ---");
//        Manager manager = new Manager("Alice Popescu",     "alice",  "alice@bookclub.ro",  "pass123");
//        Member  member1 = new Member ("Bogdan Ionescu",    "bogdan", "bogdan@bookclub.ro", "pass456");
//        Member  member2 = new Member ("Carmen Dumitrescu", "carmen", "carmen@bookclub.ro", "pass789");
//
//        service.registerUser(manager);
//        service.registerUser(member1);
//        service.registerUser(member2);
//
//        manager = (Manager) service.getUserByUsername("alice");
//        member1 = (Member)  service.getUserByUsername("bogdan");
//        member2 = (Member)  service.getUserByUsername("carmen");
//
//        System.out.println("Registered: " + manager);
//        System.out.println("Registered: " + member1);
//        System.out.println("Registered: " + member2);
//
//        // 2. Categories and books
//        System.out.println("\n--- Adding categories and books ---");
//        Category fiction = new Category("cat-fi", "Fiction", "Literary fiction", "#3B82F6");
//        service.addCategory(fiction);
//
//        Book book1 = new Book("9780140283297", "Crime and Punishment", "Fyodor Dostoevsky", fiction, 576);
//        Book book2 = new Book("9780061120084", "To Kill a Mockingbird", "Harper Lee",        fiction, 281);
//        Book book3 = new Book("9780743273565", "The Road",             "Cormac McCarthy",   fiction, 287);
//
//        service.addBook(book1); service.addBook(book2); service.addBook(book3);
//        System.out.println("Added: " + book1 + ", " + book2 + ", " + book3);
//
//        // 3. Search
//        System.out.println("\n--- Search ---");
//        System.out.println("Search 'crime': " + service.searchBooks("crime"));
//
//        // 4. Reading list
//        System.out.println("\n--- Reading list ---");
//        service.addBookToReadingList(member1.getId(), book1.getIsbn());
//        service.addBookToReadingList(member1.getId(), book2.getIsbn());
//        SortedSet<Book> rl = service.getReadingList(member1.getId());
//        System.out.println(member1.getName() + "'s list: " + rl);
//
//        // 5. Venues  — no user-defined string id any more, just pass name/address/etc.
//        System.out.println("\n--- Venues ---");
//        Venue library = new Venue("Central Library", "Str. Unirii 10, Cluj",   50, VenueType.Physical);
//        Venue online  = new Venue("Zoom Room",       "https://zoom.us/j/12345", 0, VenueType.Online);
//
//        service.addVenue(library);   // library.getVenueId() is now set by the DB
//        service.addVenue(online);
//        service.getAllVenues().forEach(v -> System.out.println("Venue [" + v.getVenueId() + "]: " + v));
//
//        // 6. Meetings  — venueId and meetingId are now int
//        System.out.println("\n--- Meetings ---");
//        LocalDateTime nextWeek  = LocalDateTime.now().plusDays(7) .withHour(18).withMinute(0);
//        LocalDateTime nextMonth = LocalDateTime.now().plusDays(30).withHour(19).withMinute(0);
//
//        Meeting m1 = service.scheduleMeeting(manager.getId(), "Discussion: Crime and Punishment",
//                library.getVenueId(), nextWeek,  book1.getIsbn());
//        System.out.println("Scheduled: " + m1);
//
//        Meeting m2 = service.scheduleMeeting(manager.getId(), "Online: To Kill a Mockingbird",
//                online.getVenueId(), nextWeek, book2.getIsbn());
//        System.out.println("Scheduled (online, same time — no conflict): " + m2);
//
//        service.addAttendeeToMeeting(m1.getMeetingId(), member1.getId());
//        service.addAttendeeToMeeting(m1.getMeetingId(), member2.getId());
//        System.out.println("Members registered for meeting 1.");
//
//        service.rescheduleMeeting(manager.getId(), m2.getMeetingId(), nextMonth);
//        System.out.println("Meeting 2 rescheduled.");
//
//        Meeting m3 = service.scheduleMeeting(manager.getId(), "The Road — Book Club",
//                library.getVenueId(), nextMonth, book3.getIsbn());
//        service.cancelMeeting(manager.getId(), m3.getMeetingId());
//        System.out.println("Meeting 3 cancelled.");
//
//        // 7. Posts and comments  — postId and authorId are now int
//        System.out.println("\n--- Posts and comments ---");
//        Post post = service.publishPost(manager.getId(), "Welcome!", "Kick-off with Dostoevsky.");
//        System.out.println("Published: " + post);
//
//        service.addCommentToPost(post.getPostId(), member1.getId(), "Looking forward!");
//        service.addCommentToPost(post.getPostId(), member2.getId(), "Great choice.");
//
//        Post loaded = service.getAllPublishedPosts().stream()
//                .filter(p -> p.getPostId() == post.getPostId())
//                .findFirst().orElse(null);
//        if (loaded != null) {
//            System.out.println("Post has " + loaded.getComments().size() + " comment(s).");
//        }
//
//        service.updatePost(manager.getId(), post.getPostId(),
//                "Welcome to 2026!", "Crime and Punishment awaits!");
//        System.out.println("Post updated.");
//
//        // 8. Notifications
//        System.out.println("\n--- Notifications for " + member1.getName() + " ---");
//        service.getNotificationsForUser(member1.getId()).forEach(n -> System.out.println("  " + n));
//        service.markAllNotificationsRead(member1.getId());
//
//        // 9. Upcoming meetings
//        System.out.println("\n--- Upcoming meetings ---");
//        service.getUpcomingMeetings().forEach(m -> System.out.println("  " + m));
//
//        // 10. All users
//        System.out.println("\n--- All users ---");
//        service.getAllUsers().forEach(u -> System.out.println("  " + u + " | " + u.getProfileSummary()));
//
//        System.out.println("\n========== DEMO COMPLETE ==========");
//        ConnectionProvider.getInstance().close();
//    }
    }
}
