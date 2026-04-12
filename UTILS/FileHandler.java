package UTILS;

import java.io.*;

public class FileHandler {

    private static final String FILE_NAME = "accounts.txt";

    public static boolean register(String username, String password) {
        try {
            if (userExists(username)) {
                return false;
            }

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

            if (!file.exists()) {
                return false;
            }

            FileReader reader = new FileReader(file);
            String content = readAll(reader);

            String[] lines = content.split("\n");

            for (String line : lines) {
                if (line.isEmpty()) continue;

                String[] data = line.split(",");
                if (data[0].equals(username)) {
                    return true;
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        return false;
    }

    public static boolean login(String username, String password) {
        try {
            File file = new File(FILE_NAME);

            if (!file.exists()) {
                return false;
            }

            FileReader reader = new FileReader(file);
            String content = readAll(reader);

            String[] lines = content.split("\n");

            for (String line : lines) {
                if (line.isEmpty()) continue;

                String[] data = line.split(",");

                if (data[0].equals(username) && data[1].equals(password)) {
                    return true;
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        return false;
    }

    private static String readAll(FileReader reader) throws IOException {
        String content = "";
        int ch;

        while ((ch = reader.read()) != -1) {
            content += (char) ch;
        }

        reader.close();
        return content;
    }
}