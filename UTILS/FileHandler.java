package UTILS;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;

public class FileHandler {

    private static final String FILE_NAME = "accounts.txt";

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
                if (data[0].equals(username) && data[1].equals(password)) return true;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }
}