import java.time.LocalDateTime;
import java.time.Duration;
import java.util.ArrayList;

/**
 * Checks task deadlines, fires reminders, and triggers punishments
 * when the user ignores tasks or misses deadlines.
 */
public class ReminderEngine {

    // How many minutes before a deadline to send the first reminder
    private static final int REMINDER_LEAD_MINUTES = 30;

    private final User user;
    private final PunishmentGenerator punishmentGenerator;

    public ReminderEngine(User user, PunishmentGenerator punishmentGenerator) {
        this.user = user;
        this.punishmentGenerator = punishmentGenerator;
    }

    /**
     * Scans all pending tasks and:
     *   - Sends a reminder if the deadline is within REMINDER_LEAD_MINUTES
     *   - Marks tasks as missed if the deadline has passed
     *   - Triggers punishments for ignored or missed tasks
     *
     * Call this at the start of each menu loop iteration.
     */
    public void checkTasks() {
        ArrayList<Task> tasks = user.getTasks();
        LocalDateTime now = LocalDateTime.now();

        for (Task task : tasks) {
            if (task.getStatus() != Task.Status.PENDING) continue;

            long minutesUntilDue = Duration.between(now, task.getDeadline()).toMinutes();

            if (minutesUntilDue < 0) {
                // Deadline passed — mark missed
                task.markMissed();
                user.recordCompletion(Task.Status.MISSED);
                System.out.println("\n[REMINDER ENGINE] Task MISSED: \"" + task.getName() + "\"");
                punishmentGenerator.displayPunishment(task.getIgnoredReminderCount());

            } else if (minutesUntilDue <= REMINDER_LEAD_MINUTES) {
                // Coming up soon — fire a reminder
                sendReminder(task, minutesUntilDue);
            }
        }
    }

    private void sendReminder(Task task, long minutesLeft) {
        System.out.println("\n[REMINDER] \"" + task.getName() + "\" is due in "
            + minutesLeft + " minute(s)!");
        System.out.println("           Mark it complete from the main menu when done.");
    }

    /**
     * Called when the user explicitly dismisses/skips a reminder without
     * completing the task. Increments ignored count and may trigger punishment.
     */
    public void handleIgnoredReminder(Task task) {
        task.incrementIgnoredReminders();
        user.recordIgnoredReminder();

        System.out.println("You ignored the reminder for: \"" + task.getName() + "\"");
        punishmentGenerator.displayPunishment(task.getIgnoredReminderCount());
    }

    /**
     * Prints a quick status overview: which tasks are urgent or overdue.
     */
    public void printStatus() {
        ArrayList<Task> tasks = user.getTasks();
        if (tasks.isEmpty()) {
            System.out.println("No tasks to check.");
            return;
        }

        LocalDateTime now = LocalDateTime.now();
        System.out.println("\n--- Reminder Check ---");
        for (Task task : tasks) {
            if (task.getStatus() != Task.Status.PENDING) continue;
            long mins = Duration.between(now, task.getDeadline()).toMinutes();
            if (mins < 0) {
                System.out.println("  OVERDUE: " + task.getName());
            } else if (mins <= REMINDER_LEAD_MINUTES) {
                System.out.println("  DUE SOON (" + mins + " min): " + task.getName());
            }
        }
        System.out.println("----------------------\n");
    }
}
