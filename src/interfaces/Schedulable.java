package interfaces;

import java.time.LocalDateTime;

public interface Schedulable {
    void schedule(LocalDateTime dateTime);

    void cancel();

    void reschedule(LocalDateTime newDateTime);

    boolean isScheduled();

}
