package patterns;

import interfaces.MeetingObserver;
import model.Meeting;
import java.util.ArrayList;
import java.util.List;

public class MeetingPublisher {

    private final List<MeetingObserver> observers = new ArrayList<>();

    public void subscribe(MeetingObserver observer) {
        if (!observers.contains(observer)) {
            observers.add(observer);
        }
    }

    public void unsubscribe(MeetingObserver observer) {
        observers.remove(observer);
    }

    public void publishScheduled(Meeting meeting) {
        for (MeetingObserver observer : observers) {
            observer.onMeetingScheduled(meeting);
        }
    }

    public void publishCancelled(Meeting meeting) {
        for (MeetingObserver observer : observers) {
            observer.onMeetingCancelled(meeting);
        }
    }

    public void publishRescheduled(Meeting meeting) {
        for (MeetingObserver observer : observers) {
            observer.onMeetingRescheduled(meeting);
        }
    }
}
