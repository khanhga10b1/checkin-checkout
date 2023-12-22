package bv.utils;

import java.io.*;

public class FileUtils {

    public static final String rootPath = System.getProperty("user.home") + File.separator + ".mybv" + File.separator;

    private FileUtils() {
    }

    public static void saveToFile(String data, String fileName) {
        ObjectUtils.callFunction(() -> {
            BufferedWriter writer = new BufferedWriter(new FileWriter(rootPath + fileName));
            writer.write(data);
            writer.close();
        }, (e) -> System.err.println("saveToFile: error"));
    }

    public static String loadFromFile(String fileName) {
        return ObjectUtils.getIgnoreException(() -> {
            StringBuilder data = new StringBuilder();
            BufferedReader reader = new BufferedReader(new FileReader(rootPath + fileName));
            String line;
            while ((line = reader.readLine()) != null) {
                data.append(line);
            }
            return data.toString();
        }, "", "loadFromFile: error");
    }
}
