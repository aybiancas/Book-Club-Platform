package repositories;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import config.*;
import model.*;

public class MeetingRepository {
    private static MeetingRepository instance;
    private static final String select = """
            select m.meeting_id, m.title, m.date_time, m.status, m.notes, m.organiser_id,
                    v.venue_id, v.name as venue_name, v.address, v.capacity, v.venue_type,
                    b.isbn, b.title as book_title, b.author, b.no_of_pages,
                    c.category_id, c.name as cat_name, c.description as cat_desc, c.color_hex
                    from meetings m
                    join venues v on v.venue_id = m.venue_id
                    join books b on b.isbn = m.book_isbn
                    left join categories c on c.category_id = b.category
            """;

    private MeetingRepository() {}

    public static MeetingRepository getInstance() {
        if (instance == null) {
            instance = new MeetingRepository();
        }
        return instance;
    }

    private Connection connection() {
        return ConnectionProvider.getInstance().getConnection();
    }

    private Meeting mapRow(ResultSet rs) throws SQLException {
        Venue venue = null;
        int venueId = rs.getInt("venue_id");
        if (!rs.wasNull()) {
            venue = new Venue(venueId, rs.getString("venue_name"), rs.getString("address"),
                    rs.getInt("capacity"), VenueType.valueOf(rs.getString("venue_type")));
        }

        Book book = null;
        String isbn = rs.getString("isbn");
        if (isbn != null) {
            Category cat = null;
            String catId = rs.getString("category_id");
            if (catId != null) {
                cat = new Category(catId, rs.getString("cat_name"),
                        rs.getString("cat_desc"), rs.getString("color_hex"));
            }
            book = new Book(isbn, rs.getString("book_title"), rs.getString("author"),
                    cat, rs.getInt("no_of_pages"));
        }

        Meeting meeting = new Meeting(rs.getString("title"), venue, rs.getTimestamp("date_time").toLocalDateTime(),
                rs.getInt("organiser_id"), book);
        meeting.restoreMeetingId(rs.getInt("meeting_id"));

        MeetingStatus status = MeetingStatus.valueOf(rs.getString("status"));
        if (status == MeetingStatus.CANCELLED) {
            meeting.cancel();
        }
        else if (status == MeetingStatus.COMPLETED) {
            meeting.complete();
        }

        String notes = rs.getString("notes");
        if (notes != null && !notes.isEmpty()) {
            meeting.addNotes(notes);
        }

        return meeting;
    }

    public void addMeeting(Meeting meeting) {
        String sql = "insert into meetings (title, venue_id, date_time, organiser_id, book_isbn, status, notes) values (?, ?, ?, ?, ?, ?::meeting_status_enum, ?)";

        try (PreparedStatement preparedStatement = connection().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            preparedStatement.setString(1, meeting.getTitle());
            if (meeting.getVenue() != null)
                preparedStatement.setInt(2, meeting.getVenue().getVenueId());
            else
                preparedStatement.setNull(2, Types.INTEGER);
            preparedStatement.setTimestamp(3, Timestamp.valueOf(meeting.getDateTime()));
            preparedStatement.setInt(4, meeting.getOrganiserId());
            preparedStatement.setString(5, meeting.getFeaturedBook() != null ? meeting.getFeaturedBook().getIsbn() : null);
            preparedStatement.setString(6, meeting.getStatus().name());
            preparedStatement.setString(7, meeting.getNotes());
            preparedStatement.executeUpdate();

            try (ResultSet keys = preparedStatement.getGeneratedKeys()) {
                if (keys.next()) meeting.restoreMeetingId(keys.getInt(1));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void updateMeeting(Meeting meeting) {
        String sql = "update meetings set title = ?, venue_id = ?, date_time = ?, book_isbn = ?, status = ?::meeting_status_enum, notes = ? where meeting_id = ?";
        try (PreparedStatement preparedStatement = connection().prepareStatement(sql)) {

            preparedStatement.setString(1, meeting.getTitle());
            if (meeting.getVenue() != null)
                preparedStatement.setInt(2, meeting.getVenue().getVenueId());
            else
                preparedStatement.setNull(2, Types.INTEGER);            preparedStatement.setTimestamp(3, Timestamp.valueOf(meeting.getDateTime()));
            preparedStatement.setString(4, meeting.getFeaturedBook() != null ? meeting.getFeaturedBook().getIsbn() : null);
            preparedStatement.setString(5, meeting.getStatus().name());
            preparedStatement.setString(6, meeting.getNotes());
            preparedStatement.setInt(7, meeting.getMeetingId());
            preparedStatement.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void deleteMeeting(int meetingId) {
        String sql = "delete from meetings where meeting_id = ?";
        try (PreparedStatement preparedStatement = connection().prepareStatement(sql)) {

            preparedStatement.setInt(1, meetingId);
            preparedStatement.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void addAttendee(int meetingId, int userId) {
        String sql = "insert into meeting_attendees (meeting_id, user_id) values (?, ?) on conflict do nothing";
        try (PreparedStatement preparedStatement = connection().prepareStatement(sql)) {

            preparedStatement.setInt(1, meetingId);
            preparedStatement.setInt(2, userId);
            preparedStatement.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void removeAttendee(int meetingId, int userId) {
        String sql = "delete from meeting_attendees where meeting_id = ? and user_id = ?";
        try (PreparedStatement preparedStatement = connection().prepareStatement(sql)) {

            preparedStatement.setInt(1, meetingId);
            preparedStatement.setInt(2, userId);
            preparedStatement.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<Integer> findAttendeeIds(int meetingId) {
        List<Integer> ids = new ArrayList<>();
        String sql = "select user_id from meeting_attendees where meeting_id = ?";
        try (PreparedStatement preparedStatement = connection().prepareStatement(sql)) {

            preparedStatement.setInt(1, meetingId);
            try (ResultSet rs = preparedStatement.executeQuery()) {
                while (rs.next()) ids.add(rs.getInt("user_id"));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return ids;
    }

    public Meeting findById(int meetingId) {
        String sql = select + "where m.meeting_id = ?";
        try (PreparedStatement preparedStatement = connection().prepareStatement(sql)) {

            preparedStatement.setInt(1, meetingId);
            try (ResultSet rs = preparedStatement.executeQuery()) {
                if (rs.next()) {
                    return mapRow(rs);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<Meeting> findAll() {
        List<Meeting> list = new ArrayList<>();
        String sql = select + "order by m.date_time";
        try (PreparedStatement preparedStatement = connection().prepareStatement(sql);
             ResultSet rs = preparedStatement.executeQuery()) {

            while (rs.next()) {
                list.add(mapRow(rs));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public List<Meeting> findUpcoming() {
        List<Meeting> list = new ArrayList<>();
        String sql = select + "where m.status = 'SCHEDULED'::meeting_status_enum and m.date_time > now() order by m.date_time";
        try (PreparedStatement preparedStatement = connection().prepareStatement(sql);
             ResultSet rs = preparedStatement.executeQuery()) {

            while (rs.next()) {
                list.add(mapRow(rs));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public List<Meeting> findUpcomingAtVenue(int venueId) {
        List<Meeting> list = new ArrayList<>();
        String sql = select + "where m.status = 'SCHEDULED'::meeting_status_enum and m.date_time > now() and m.venue_id = ? order by m.date_time";
        try (PreparedStatement preparedStatement = connection().prepareStatement(sql)) {

            preparedStatement.setInt(1, venueId);
            try (ResultSet rs = preparedStatement.executeQuery()) {
                while (rs.next()) {
                    list.add(mapRow(rs));
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }
}
