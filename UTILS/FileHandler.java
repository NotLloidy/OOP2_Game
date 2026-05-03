package UTILS;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

public class FileHandler {

    private static final String FILE_NAME  = "accounts.txt";
    private static final String TIMES_FILE = "arcade_times.txt"; // best arcade clear times per account

    // ── Logged-in user ────────────────────────────────────────────────────
    // Set by GameGUI after a successful login so arcade screens can save
    // times without needing the username passed through every call chain.
    private static String currentUsername = null;

    public static void   setCurrentUser(String username) { currentUsername = username; }
    public static String getCurrentUser()                { return currentUsername; }

    // =========================================================================
    // ACCOUNT METHODS (unchanged)
    // =========================================================================

    public static boolean register(String username, String password) {
        try {
            if (userExists(username)) return false;

            FileWriter writer = new FileWriter(FILE_NAME, true);
            writer.write(username + "," + password + "\n");
            writer.close();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean userExists(String username) {
        try {
            File file = new File(FILE_NAME);
            if (!file.exists()) return false;

            String content = Files.readString(Paths.get(FILE_NAME));
            String[] lines = content.split("\n");

            for (String line : lines) {
                if (line.isEmpty()) continue;
                String[] data = line.split(",");
                if (data[0].equals(username)) return true;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static boolean login(String username, String password) {
        try {
            File file = new File(FILE_NAME);
            if (!file.exists()) return false;

            String content = Files.readString(Paths.get(FILE_NAME));
            String[] lines = content.split("\n");

            for (String line : lines) {
                if (line.isEmpty()) continue;
                String[] data = line.split(",");
                if (data[0].equals(username) && data[1].equals(password)) {
                    setCurrentUser(username); // track who is logged in
                    return true;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    // =========================================================================
    // ARCADE BEST TIME METHODS
    //
    // File format  (arcade_times.txt):
    //   username,bestTimeInSeconds
    //   e.g.  alice,142
    //
    // Only the best (lowest) time per account is kept.
    // =========================================================================

    /**
     * Saves an arcade clear time for the currently logged-in account.
     * Only updates the file if this time is better (lower) than the
     * previously recorded best for that account.
     *
     * @param seconds total clear time in whole seconds
     */
    public static void saveArcadeTime(int seconds) {
        if (currentUsername == null) return; // no one logged in, skip

        Map<String, Integer> times = loadAllTimes();

        if (!times.containsKey(currentUsername) || seconds < times.get(currentUsername)) {
            times.put(currentUsername, seconds);
            writeAllTimes(times);
        }
    }

    /**
     * Returns the top 10 accounts sorted by best clear time (ascending — fastest first).
     * Each element is a String[2]: { username, formattedTime }.
     */
    public static List<String[]> getTopTenTimes() {
        Map<String, Integer> times = loadAllTimes();

        List<Map.Entry<String, Integer>> sorted = new ArrayList<>(times.entrySet());
        sorted.sort(Map.Entry.comparingByValue()); // ascending = fastest first

        List<String[]> result = new ArrayList<>();
        int limit = Math.min(10, sorted.size());
        for (int i = 0; i < limit; i++) {
            Map.Entry<String, Integer> e = sorted.get(i);
            result.add(new String[]{ e.getKey(), formatTime(e.getValue()) });
        }
        return result;
    }

    /**
     * Converts a raw second count into a M:SS display string.
     * e.g. 142 → "2:22"
     */
    public static String formatTime(int totalSeconds) {
        int minutes = totalSeconds / 60;
        int secs    = totalSeconds % 60;
        return minutes + ":" + String.format("%02d", secs);
    }

    // ── Private helpers ───────────────────────────────────────────────────

    private static Map<String, Integer> loadAllTimes() {
        Map<String, Integer> times = new LinkedHashMap<>();
        File file = new File(TIMES_FILE);
        if (!file.exists()) return times;

        try {
            String content = Files.readString(Paths.get(TIMES_FILE));
            for (String line : content.split("\n")) {
                if (line.isEmpty()) continue;
                String[] parts = line.split(",");
                if (parts.length < 2) continue;
                try {
                    times.put(parts[0].trim(), Integer.parseInt(parts[1].trim()));
                } catch (NumberFormatException ignored) {}
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return times;
    }

    private static void writeAllTimes(Map<String, Integer> times) {
        try (FileWriter writer = new FileWriter(TIMES_FILE, false)) {
            for (Map.Entry<String, Integer> e : times.entrySet()) {
                writer.write(e.getKey() + "," + e.getValue() + "\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}