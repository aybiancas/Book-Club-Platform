package model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import interfaces.Schedulable;

public class Meeting implements Schedulable {
    private int meetingId;
    private String title;
    private Venue venue;
    private LocalDateTime dateTime;
    private final int organiserId;
    private Book featuredBook;
    private final List<Integer> attendeeIds;
    private MeetingStatus status;
    private String notes;

    public Meeting(String title, Venue venue, LocalDateTime dateTime, int organiserId, Book featuredBook) {
        this.meetingId = 0;
        this.title = title;
        this.venue = venue;
        this.dateTime = dateTime;
        this.organiserId = organiserId;
        this.featuredBook = featuredBook;
        this.attendeeIds = new ArrayList<>();
        this.status = MeetingStatus.SCHEDULED;
        this.notes = "";
    }

    public int getMeetingId() {
        return meetingId;
    }

    public String getTitle() {
        return title;
    }

    public Venue getVenue() {
        return venue;
    }

    public LocalDateTime getDateTime() {
        return dateTime;
    }

    public int getOrganiserId() {
        return organiserId;
    }

    public Book getFeaturedBook() {
        return featuredBook;
    }

    public List<Integer> getAttendeeIds() {
        return attendeeIds;
    }

    public MeetingStatus getStatus() {
        return status;
    }

    public String getNotes() {
        return notes;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setVenue(Venue venue) {
        this.venue = venue;
    }

    public void setFeaturedBook(Book featuredBook) {
        this.featuredBook = featuredBook;
    }

    public void restoreMeetingId(int id) {
        this.meetingId = id;
    }

    @Override
    public void schedule(LocalDateTime dt) {
        this.dateTime = dt;
        this.status = MeetingStatus.SCHEDULED;
    }

    @Override
    public void cancel() {
        this.status = MeetingStatus.CANCELLED;
    }

    @Override
    public void reschedule(LocalDateTime dt) {
        this.dateTime = dt;
        this.status = MeetingStatus.SCHEDULED;
    }

    @Override
    public boolean isScheduled() {
        return status == MeetingStatus.SCHEDULED;
    }

    public void complete() {
        this.status = MeetingStatus.COMPLETED;
    }

    public void addNotes(String n) {
        this.notes = n;
    }

    public void addAttendee(int id) {
        if (!attendeeIds.contains(id)) {
            attendeeIds.add(id);
        }
    }

    public void removeAttendee(int id) {
        attendeeIds.remove(id);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Meeting)) {
            return false;
        }
        return meetingId == ((Meeting) o).meetingId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(meetingId);
    }

    @Override public String toString() {
        return "[" + meetingId + "] " + title + " — "
                + dateTime.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"))
                + " (" + status + ")";
    }
}
