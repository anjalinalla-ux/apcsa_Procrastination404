import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * Procrastination404 — main entry point.
 * Console-driven menu loop that ties together all components.
 */
public class Main {

    private static final DateTimeFormatter INPUT_FORMATTER =
        DateTimeFormatter.ofPattern("MM/dd/yyyy HH:mm");

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.println("================================");
        System.out.println("   PROCRASTINATION404           ");
        System.out.println("   Your tasks won't do          ");
        System.out.println("   themselves. (We checked.)    ");
        System.out.println("================================\n");

        System.out.print("Enter your name: ");
        String name = scanner.nextLine().trim();

        User user = new User(name.isEmpty() ? "Student" : name);
        PunishmentGenerator punisher = new PunishmentGenerator();
        MiniGame miniGame = new MiniGame(scanner);
        ReminderEngine reminderEngine = new ReminderEngine(user, punisher);

        System.out.println("\nWelcome, " + user.getName() + "! Let's get things done.\n");

        boolean running = true;
        while (running) {
            // Auto-check reminders and missed deadlines on every loop
            reminderEngine.checkTasks();

            printMenu();
            System.out.print("Choice: ");
            String input = scanner.nextLine().trim();

            switch (input) {
                case "1" -> addTask(user, scanner);
                case "2" -> viewTasks(user);
                case "3" -> completeTask(user, scanner);
                case "4" -> ignoreReminder(user, reminderEngine, scanner);
                case "5" -> triggerMiniGame(miniGame, punisher, user);
                case "6" -> user.printStats();
                case "7" -> reminderEngine.printStatus();
                case "8" -> {
                    System.out.println("\nGoodbye, " + user.getName()
                        + "! Go finish your tasks. (We mean it.)");
                    running = false;
                }
                default -> System.out.println("Invalid choice. Try again.\n");
            }
        }

        scanner.close();
    }

    // ---------------------------------------------------------------
    // Menu
    // ---------------------------------------------------------------

    private static void printMenu() {
        System.out.println("---------- MENU ----------");
        System.out.println("1. Add a task");
        System.out.println("2. View all tasks");
        System.out.println("3. Mark task as complete");
        System.out.println("4. Ignore a reminder (warning: punishment follows)");
        System.out.println("5. Trigger mini-game");
        System.out.println("6. View my stats");
        System.out.println("7. Check reminders now");
        System.out.println("8. Exit");
        System.out.println("--------------------------");
    }

    // ---------------------------------------------------------------
    // Option handlers
    // ---------------------------------------------------------------

    private static void addTask(User user, Scanner scanner) {
        if (user.getTasks().size() >= 5) {
            System.out.println("You already have 5 tasks. Complete or remove one first.\n");
            return;
        }

        System.out.print("Task name: ");
        String taskName = scanner.nextLine().trim();
        if (taskName.isEmpty()) {
            System.out.println("Task name cannot be empty.\n");
            return;
        }

        System.out.print("Deadline (MM/DD/YYYY HH:MM, 24-hour): ");
        String deadlineInput = scanner.nextLine().trim();
        try {
            LocalDateTime deadline = LocalDateTime.parse(deadlineInput, INPUT_FORMATTER);
            if (deadline.isBefore(LocalDateTime.now())) {
                System.out.println("That deadline is already in the past!\n");
                return;
            }
            Task task = new Task(taskName, deadline);
            user.addTask(task);
            System.out.println("Task added: \"" + taskName + "\"\n");
        } catch (DateTimeParseException e) {
            System.out.println("Invalid date format. Use MM/DD/YYYY HH:MM (e.g. 04/20/2026 14:30)\n");
        }
    }

    private static void viewTasks(User user) {
        ArrayList<Task> tasks = user.getTasks();
        if (tasks.isEmpty()) {
            System.out.println("No tasks yet. Add one!\n");
            return;
        }
        System.out.println("\n--- Your Tasks ---");
        for (int i = 0; i < tasks.size(); i++) {
            System.out.println((i + 1) + ". " + tasks.get(i));
        }
        System.out.println();
    }

    private static void completeTask(User user, Scanner scanner) {
        ArrayList<Task> tasks = user.getTasks();
        if (tasks.isEmpty()) {
            System.out.println("No tasks to complete.\n");
            return;
        }

        viewTasks(user);
        System.out.print("Enter task number to mark complete: ");
        try {
            int index = Integer.parseInt(scanner.nextLine().trim()) - 1;
            if (index < 0 || index >= tasks.size()) {
                System.out.println("Invalid number.\n");
                return;
            }
            Task task = tasks.get(index);
            if (task.getStatus() != Task.Status.PENDING) {
                System.out.println("That task is already " + task.getStatus() + ".\n");
                return;
            }
            task.markCompleted();
            user.recordCompletion(task.getStatus());

            if (task.getStatus() == Task.Status.LATE) {
                System.out.println("Marked late but done — better late than never, barely.");
            } else {
                System.out.println("Great job! \"" + task.getName() + "\" is complete.");
                System.out.println("Streak: " + user.getStreak() + " day(s)!");
            }
            System.out.println();
        } catch (NumberFormatException e) {
            System.out.println("Enter a valid number.\n");
        }
    }

    private static void ignoreReminder(User user, ReminderEngine reminderEngine, Scanner scanner) {
        ArrayList<Task> tasks = user.getTasks();
        // Only show pending tasks
        ArrayList<Task> pending = new ArrayList<>();
        for (Task t : tasks) {
            if (t.getStatus() == Task.Status.PENDING) pending.add(t);
        }

        if (pending.isEmpty()) {
            System.out.println("No pending tasks to ignore.\n");
            return;
        }

        System.out.println("\nPending tasks:");
        for (int i = 0; i < pending.size(); i++) {
            System.out.println((i + 1) + ". " + pending.get(i).getName());
        }
        System.out.print("Which reminder are you ignoring? ");
        try {
            int index = Integer.parseInt(scanner.nextLine().trim()) - 1;
            if (index < 0 || index >= pending.size()) {
                System.out.println("Invalid number.\n");
                return;
            }
            reminderEngine.handleIgnoredReminder(pending.get(index));
        } catch (NumberFormatException e) {
            System.out.println("Enter a valid number.\n");
        }
    }

    private static void triggerMiniGame(MiniGame miniGame, PunishmentGenerator punisher, User user) {
        System.out.println("\nTriggering mini-game...");
        boolean won = miniGame.play();
        if (!won) {
            System.out.println("You lost the mini-game. Your tasks are not impressed.\n");
        } else {
            System.out.println("Mini-game cleared! Now go do your actual work.\n");
        }
    }
}
