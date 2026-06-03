package audit;

import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class AuditService {
    private static final String PATH = "audit_logs.csv";
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private static volatile AuditService instance;

    private AuditService() {
        try (FileWriter filewriter = new FileWriter(PATH, true)) {
        } catch (IOException e) {
            System.out.println("Can't open log file" + e.getMessage()); // placeholder
        }
    }

    public static AuditService getInstance() {
        if (instance == null) {
            synchronized (AuditService.class) {
                if (instance == null) {
                    instance = new AuditService();
                }
            }
        }
        return instance;
    }

    public synchronized void log(String action) {
        String timestamp = LocalDateTime.now().format(formatter);
        try (PrintWriter out = new PrintWriter(new FileWriter(PATH, true))) {
            out.println(action + "," + timestamp);
        } catch (IOException e) {
            System.out.println("Can't write in log file" + e.getMessage());
        }
    }

    public static final String REGISTER_USER = "register_user";
    public static final String REMOVE_USER = "remove_user";
    public static final String ADD_BOOK = "add_book";
    public static final String REMOVE_BOOK = "remove_book";
    public static final String SEARCH_BOOKS = "search_books";
    public static final String ADD_TO_READING_LIST = "add_to_reading_list";
    public static final String REMOVE_FROM_READING_LIST = "remove_from_reading_list";
    public static final String ADD_VENUE = "add_venue";
    public static final String UPDATE_VENUE = "update_venue";
    public static final String DELETE_VENUE = "delete_venue";
    public static final String SCHEDULE_MEETING = "schedule_meeting";
    public static final String CANCEL_MEETING = "cancel_meeting";
    public static final String RESCHEDULE_MEETING = "reschedule_meeting";
    public static final String ATTEND_MEETING = "attend_meeting";
    public static final String PUBLISH_POST = "publish_post";
    public static final String UPDATE_POST = "update_post";
    public static final String DELETE_POST = "delete_post";
    public static final String ADD_COMMENT = "add_comment";
    public static final String MARK_NOTIFICATIONS_READ = "mark_notifications_read";
}
