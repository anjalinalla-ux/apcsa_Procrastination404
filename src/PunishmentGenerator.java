import java.util.Random;

/**
 * Generates humorous punishments and escalates to "Chaos Mode" when the user
 * repeatedly ignores tasks.
 */
public class PunishmentGenerator {

    // Threshold: ignored reminders before Chaos Mode kicks in
    private static final int CHAOS_THRESHOLD = 3;

    private static final String[] NORMAL_PUNISHMENTS = {
        "You've been sentenced to 10 jumping jacks. Yes, right now.",
        "Your procrastination has been logged. The Wi-Fi gods are watching you.",
        "Tell someone near you that you've been slacking. Out loud.",
        "Write 'I will not procrastinate' 5 times in your notebook.",
        "Drink a full glass of water and think about your choices.",
        "Set your phone face-down for the next 15 minutes. No peeking.",
        "Stare at a blank wall for 30 seconds and consider your decisions.",
        "Name three things you SHOULD be doing right now. Then do one of them.",
        "Stand up, stretch, and recite your task out loud. Drama welcome.",
        "You must now work in complete silence for 10 minutes. No music."
    };

    private static final String[] CHAOS_PUNISHMENTS = {
        "CHAOS MODE ACTIVATED. You must now complete a mini-game before resuming.",
        "CHAOS MODE: Your streak is GONE. Hope it was worth it.",
        "CHAOS MODE: You are officially placed on the Procrastinator Hall of Shame.",
        "CHAOS MODE: Describe your current excuse in writing. Then delete it.",
        "CHAOS MODE: No snacks until this task is done. The fridge is locked (in spirit)."
    };

    private static final String[] ASCII_REACTIONS = {
        "(╯°□°）╯︵ ┻━┻  TABLE FLIPPED — just like your productivity.",
        "(ง'̀-'́)ง  The task is fighting back. Are you ready?",
        "ʕ •ᴥ•ʔ  Even this bear is disappointed in you.",
        "¯\\_(ツ)_/¯  Tasks don't do themselves. Shocking, we know.",
        "(ಠ_ಠ)  Really? REALLY?"
    };

    private final Random random;

    public PunishmentGenerator() {
        this.random = new Random();
    }

    // Returns a punishment based on how many reminders the user has ignored
    public String getPunishment(int ignoredCount) {
        String ascii = ASCII_REACTIONS[random.nextInt(ASCII_REACTIONS.length)];

        if (ignoredCount >= CHAOS_THRESHOLD) {
            String chaos = CHAOS_PUNISHMENTS[random.nextInt(CHAOS_PUNISHMENTS.length)];
            return "\n" + ascii + "\n" + chaos;
        }

        String normal = NORMAL_PUNISHMENTS[random.nextInt(NORMAL_PUNISHMENTS.length)];
        return "\n" + ascii + "\n" + normal;
    }

    public boolean isChaosMode(int ignoredCount) {
        return ignoredCount >= CHAOS_THRESHOLD;
    }

    // Print a formatted punishment box to the console
    public void displayPunishment(int ignoredCount) {
        String punishment = getPunishment(ignoredCount);
        System.out.println("\n!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
        System.out.println("       PROCRASTINATION DETECTED      ");
        System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
        System.out.println(punishment);
        System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!\n");
    }
}
