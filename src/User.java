import java.util.ArrayList;

/**
 * Represents the user and tracks their tasks, stats, and procrastination behavior.
 */
public class User {

    private String name;
    private ArrayList<Task> tasks;
    private int streak;            // consecutive days with all tasks completed
    private int totalCompleted;
    private int totalMissed;
    private int totalLate;
    private int totalIgnoredReminders;

    public User(String name) {
        this.name = name;
        this.tasks = new ArrayList<>();
        this.streak = 0;
        this.totalCompleted = 0;
        this.totalMissed = 0;
        this.totalLate = 0;
        this.totalIgnoredReminders = 0;
    }

    // Add a task (max 5 per the project spec)
    public boolean addTask(Task task) {
        if (tasks.size() >= 5) {
            System.out.println("You can only track up to 5 tasks at a time!");
            return false;
        }
        tasks.add(task);
        return true;
    }

    public void removeTask(int index) {
        if (index >= 0 && index < tasks.size()) {
            tasks.remove(index);
        }
    }

    // Called by ReminderEngine when a task is completed or missed
    public void recordCompletion(Task.Status status) {
        switch (status) {
            case COMPLETED -> { totalCompleted++; streak++; }
            case LATE      -> { totalLate++;      streak = 0; }
            case MISSED    -> { totalMissed++;    streak = 0; }
            default        -> {}
        }
    }

    public void recordIgnoredReminder() {
        totalIgnoredReminders++;
    }

    // Returns completion rate as a percentage (0–100)
    public double getCompletionRate() {
        int total = totalCompleted + totalMissed + totalLate;
        if (total == 0) return 0.0;
        return (totalCompleted * 100.0) / total;
    }

    // Prints a formatted stats summary
    public void printStats() {
        System.out.println("\n===== YOUR STATS =====");
        System.out.println("Name:              " + name);
        System.out.println("Current streak:    " + streak + " day(s)");
        System.out.printf ("Completion rate:   %.1f%%%n", getCompletionRate());
        System.out.println("Completed on time: " + totalCompleted);
        System.out.println("Completed late:    " + totalLate);
        System.out.println("Missed:            " + totalMissed);
        System.out.println("Ignored reminders: " + totalIgnoredReminders);
        System.out.println("======================\n");
    }

    // Getters
    public String getName()               { return name; }
    public ArrayList<Task> getTasks()     { return tasks; }
    public int getStreak()                { return streak; }
    public int getTotalMissed()           { return totalMissed; }
    public int getTotalIgnoredReminders() { return totalIgnoredReminders; }
}
