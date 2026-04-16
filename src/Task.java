import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Represents a single user task with a deadline and completion tracking.
 */
public class Task {

    public enum Status {
        PENDING, COMPLETED, LATE, MISSED
    }

    private static final DateTimeFormatter FORMATTER =
        DateTimeFormatter.ofPattern("MMM d, yyyy h:mm a");

    private String name;
    private LocalDateTime deadline;
    private Status status;
    private LocalDateTime completionTime;
    private int ignoredReminderCount;

    public Task(String name, LocalDateTime deadline) {
        this.name = name;
        this.deadline = deadline;
        this.status = Status.PENDING;
        this.completionTime = null;
        this.ignoredReminderCount = 0;
    }

    // Mark the task as completed right now
    public void markCompleted() {
        this.completionTime = LocalDateTime.now();
        if (completionTime.isAfter(deadline)) {
            this.status = Status.LATE;
        } else {
            this.status = Status.COMPLETED;
        }
    }

    // Mark the task as missed (deadline passed, never done)
    public void markMissed() {
        this.status = Status.MISSED;
    }

    // Returns true if the deadline has passed and task is still pending
    public boolean isOverdue() {
        return status == Status.PENDING && LocalDateTime.now().isAfter(deadline);
    }

    // Returns minutes between deadline and completion (negative = finished early)
    public long getDelayMinutes() {
        if (completionTime == null) return 0;
        return java.time.Duration.between(deadline, completionTime).toMinutes();
    }

    public void incrementIgnoredReminders() {
        ignoredReminderCount++;
    }

    // Getters
    public String getName()               { return name; }
    public LocalDateTime getDeadline()    { return deadline; }
    public Status getStatus()             { return status; }
    public LocalDateTime getCompletionTime() { return completionTime; }
    public int getIgnoredReminderCount()  { return ignoredReminderCount; }

    @Override
    public String toString() {
        String deadlineStr = deadline.format(FORMATTER);
        String statusLabel = switch (status) {
            case PENDING   -> isOverdue() ? "[OVERDUE]" : "[PENDING]";
            case COMPLETED -> "[DONE]   ";
            case LATE      -> "[LATE]   ";
            case MISSED    -> "[MISSED] ";
        };
        return statusLabel + " " + name + "  |  Due: " + deadlineStr
            + (ignoredReminderCount > 0 ? "  |  Ignored reminders: " + ignoredReminderCount : "");
    }
}
