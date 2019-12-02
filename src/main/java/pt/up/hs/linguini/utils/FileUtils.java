package pt.up.hs.linguini.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Utilities to read files.
 *
 * @author José Carlos Paiva <code>josepaiva94@gmail.com</code>
 */
public class FileUtils {

    public static List<String> readAllLines(String path) throws IOException {

        ClassLoader classLoader = FileUtils.class.getClassLoader();

        return readAllLines(classLoader.getResourceAsStream(path));
    }

    public static List<String> readAllLines(InputStream is)
            throws IOException {

        List<String> lines = new ArrayList<>();

        try (InputStreamReader isr = new InputStreamReader(is);
             BufferedReader br = new BufferedReader(isr)) {

            String line;
            while ((line = br.readLine()) != null) {
                lines.add(line);
            }
        }
        return lines;
    }
}
