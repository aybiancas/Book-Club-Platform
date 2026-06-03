package services;

import audit.AuditService;
import interfaces.MeetingObserver;
import model.*;
import exceptions.*;
import patterns.*;
import repositories.*;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

public class BookClubService implements MeetingObserver {

    private final UserRepository userRepo = UserRepository.getInstance();
    private final BookRepository bookRepo = BookRepository.getInstance();
    private final CategoryRepository catRepo = CategoryRepository.getInstance();
    private final VenueRepository venueRepo = VenueRepository.getInstance();
    private final MeetingRepository meetRepo = MeetingRepository.getInstance();
    private final PostRepository postRepo = PostRepository.getInstance();
    private final NotificationRepository notifRepo = NotificationRepository.getInstance();
    private final AuditService audit = AuditService.getInstance();
    private final MeetingPublisher meetingPublisher = new MeetingPublisher();

    public BookClubService() {
        meetingPublisher.subscribe(this);   // self-subscribe for notification fan-out - observer pattern
    }

    // USER OPERATIONS

    public void registerUser(User user) {
        userRepo.addUser(user);
        audit.log(AuditService.REGISTER_USER);
    }

    public void removeUser(int userId) {
        if (userRepo.findById(userId) == null) {
            throw new UserNotFound("User not found: " + userId);
        }
        userRepo.deleteUser(userId);
        audit.log(AuditService.REMOVE_USER);
    }

    public User getUser(int userId) {
        User u = userRepo.findById(userId);
        if (u == null) throw new UserNotFound("User not found: " + userId);
        return u;
    }

    public User getUserByUsername(String username) {
        User u = userRepo.findByUsername(username);
        if (u == null) throw new UserNotFound("User not found: " + username);
        return u;
    }

    public List<Member> getAllMembers() {
        return userRepo.findAllMembers().stream()
                .map(u -> (Member) u).collect(Collectors.toList());
    }

    public List<User> getAllUsers() {
        return userRepo.findAll();
    }

    // BOOK OPERATIONS

    public void addBook(Book book) {
        bookRepo.addBook(book);
        audit.log(AuditService.ADD_BOOK);
    }

    public void removeBook(String isbn) {
        bookRepo.deleteBook(isbn);
        audit.log(AuditService.REMOVE_BOOK);
    }

    public Book getBook(String isbn) {
        Book b = bookRepo.findByIsbn(isbn);
        if (b == null) throw new BookNotFound("Book not found: " + isbn);
        return b;
    }

    public List<Book> getAllBooksSorted() {
        return bookRepo.findAll();   // DB returns sorted by title
    }

    public List<Book> searchBooks(String title) {
        audit.log(AuditService.SEARCH_BOOKS);
        return bookRepo.findByTitle(title);
    }

    public void addBookToReadingList(int memberId, String isbn) {
        getUser(memberId);
        getBook(isbn);
        bookRepo.addToReadingList(memberId, isbn);
        audit.log(AuditService.ADD_TO_READING_LIST);
    }

    public void removeBookFromReadingList(int memberId, String isbn) {
        bookRepo.removeFromReadingList(memberId, isbn);
        audit.log(AuditService.REMOVE_FROM_READING_LIST);
    }

    public SortedSet<Book> getReadingList(int memberId) {
        TreeSet<Book> sorted = new TreeSet<>(bookRepo.findReadingListForMember(memberId));
        return Collections.unmodifiableSortedSet(sorted);
    }

    // CATEGORY OPERATIONS

    public void addCategory(Category c) {
        catRepo.addCategory(c);
    }

    public List<Category> getAllCategories() {
        return catRepo.findAll();
    }

    // VENUE OPERATIONS

    public void addVenue(Venue v) {
        venueRepo.addVenue(v);
        audit.log(AuditService.ADD_VENUE);
    }

    public List<Venue> getAllVenues() {
        return venueRepo.findAll();
    }

    public Venue getVenue(int id) {
        Venue v = venueRepo.findById(id);
        if (v == null) throw new IllegalArgumentException("Venue not found: " + id);
        return v;
    }

    public void updateVenue(Venue v) {
        venueRepo.updateVenue(v);
        audit.log(AuditService.UPDATE_VENUE);
    }

    public void deleteVenue(int venueId) {
        venueRepo.deleteVenue(venueId);
        audit.log(AuditService.DELETE_VENUE);
    }

    // MEETING OPERATIONS

    public Meeting scheduleMeeting(int managerId, String title, int venueId,
                                   LocalDateTime dateTime, String isbn) {
        User u = getUser(managerId);
        if (!(u instanceof Manager)) {
            throw new UnauthorizedActionException("scheduleMeeting");
        }

        Venue venue = venueId > 0 ? venueRepo.findById(venueId) : null;

        if (venue != null && venue.getCapacity() > 0) {
            for (Meeting m : meetRepo.findUpcomingAtVenue(venueId)) {
                long diff = Math.abs(
                        java.time.Duration.between(m.getDateTime(), dateTime).toMinutes());
                if (diff < 60)
                    throw new MeetingConflictException("A meeting at venue '" + venue.getName() + "' exists within 1 hour of " + dateTime);
            }
        }

        Book book = getBook(isbn);

        Meeting meeting = new Meeting(title, venue, dateTime, managerId, book);
        meetRepo.addMeeting(meeting);
        ((Manager) u).trackMeeting(meeting.getMeetingId());

        audit.log(AuditService.SCHEDULE_MEETING);
        meetingPublisher.publishScheduled(meeting);
        return meeting;
    }

    public void cancelMeeting(int managerId, int meetingId) {
        if (!(getUser(managerId) instanceof Manager)) {
            throw new UnauthorizedActionException("cancelMeeting");
        }
        Meeting m = meetRepo.findById(meetingId);
        if (m == null) {
            throw new IllegalArgumentException("Meeting not found: " + meetingId);
        }
        m.cancel();
        meetRepo.updateMeeting(m);
        audit.log(AuditService.CANCEL_MEETING);
        meetingPublisher.publishCancelled(m);
    }

    public void rescheduleMeeting(int managerId, int meetingId, LocalDateTime newDateTime) {
        if (!(getUser(managerId) instanceof Manager)) {
            throw new UnauthorizedActionException("rescheduleMeeting");
        }
        Meeting m = meetRepo.findById(meetingId);
        if (m == null) {
            throw new IllegalArgumentException("Meeting not found: " + meetingId);
        }
        m.reschedule(newDateTime);
        meetRepo.updateMeeting(m);
        audit.log(AuditService.RESCHEDULE_MEETING);
        meetingPublisher.publishRescheduled(m);
    }

    public void addAttendeeToMeeting(int meetingId, int userId) {
        getUser(userId);
        Meeting m = meetRepo.findById(meetingId);
        if (m == null) throw new IllegalArgumentException("Meeting not found: " + meetingId);
        if (!m.isScheduled()) throw new IllegalStateException("Meeting is not active.");
        m.addAttendee(userId);
        meetRepo.addAttendee(meetingId, userId);
        audit.log(AuditService.ATTEND_MEETING);
    }

    public List<Meeting> getUpcomingMeetings() {
        return meetRepo.findUpcoming();
    }

    public List<Meeting> getAllMeetings() {
        return meetRepo.findAll();
    }

    // POSTS AND COMMENTS OPERATIONS

    public Post publishPost(int managerId, String title, String body) {
        User u = getUser(managerId);
        if (!(u instanceof Manager)) {
            throw new UnauthorizedActionException("publishPost");
        }
        Post post = new Post(title, body, managerId);
        post.publish();
        postRepo.addPost(post);
        ((Manager) u).trackPost(post.getPostId());
        audit.log(AuditService.PUBLISH_POST);
        return post;
    }

    public void updatePost(int managerId, int postId, String newTitle, String newBody) {
        if (!(getUser(managerId) instanceof Manager))
            throw new UnauthorizedActionException("updatePost");
        Post p = postRepo.findById(postId);
        if (p == null) throw new IllegalArgumentException("Post not found: " + postId);
        p.editTitle(newTitle);
        p.editBody(newBody);
        postRepo.updatePost(p);
        audit.log(AuditService.UPDATE_POST);
    }

    public void deletePost(int managerId, int postId) {
        if (!(getUser(managerId) instanceof Manager))
            throw new UnauthorizedActionException("deletePost");
        postRepo.deletePost(postId);
        audit.log(AuditService.DELETE_POST);
    }

    public void addCommentToPost(int postId, int authorId, String text) {
        getUser(authorId);
        Post p = postRepo.findById(postId);
        if (p == null) throw new IllegalArgumentException("Post not found: " + postId);
        Comment comment = new Comment(postId, authorId, text);
        p.addComment(comment);
        postRepo.addComment(comment);
        audit.log(AuditService.ADD_COMMENT);
    }

    public List<Post> getAllPublishedPosts() {
        return postRepo.findAllPublished();
    }

    public List<Post> getAllPosts() {
        return postRepo.findAll();
    }

    // NOTIFICATIONS OPERATIONS

    public List<Notification> getNotificationsForUser(int userId) {
        return notifRepo.findByRecipient(userId);
    }

    public void markAllNotificationsRead(int userId) {
        notifRepo.markAllReadForUser(userId);
        audit.log(AuditService.MARK_NOTIFICATIONS_READ);
    }

    // Meeting observer specific operations

    @Override
    public void onMeetingScheduled(Meeting meeting) {
        fanOutNotification(NotificationType.NEW_MEETING, "New meeting scheduled: " + meeting.getTitle(), meeting.getMeetingId());
    }

    @Override
    public void onMeetingCancelled(Meeting meeting) {
        fanOutNotification(NotificationType.MEETING_CANCELLED, "Meeting cancelled: " + meeting.getTitle(), meeting.getMeetingId());
    }

    @Override
    public void onMeetingRescheduled(Meeting meeting) {
        fanOutNotification(NotificationType.MEETING_REMINDER, "Meeting rescheduled: " + meeting.getTitle(), meeting.getMeetingId());
    }

    private void fanOutNotification(NotificationType type, String message, int entityId) {
        for (User u : userRepo.findAllMembers()) {
            Notification n = new patterns.NotificationBuilder()
                    .recipient(u.getId())
                    .type(type)
                    .message(message)
                    .relatedEntity(entityId)
                    .build();
            notifRepo.addNotification(n);
        }
    }
}
