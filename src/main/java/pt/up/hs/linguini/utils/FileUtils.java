package pt.up.hs.linguini.utils;

import java.io.*;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**
 * Utilities to read files.
 *
 * @author José Carlos Paiva <code>josepaiva94@gmail.com</code>
 */
public class FileUtils {

    /**
     * Get file from classpath resources folder.
     *
     * @param path {@link String} path within resources folder.
     * @return {@link File} file from classpath resources folder
     * @throws IOException if the resource is not found
     */
    public static File getFileFromResources(String path) throws IOException {

        ClassLoader classLoader = FileUtils.class.getClassLoader();

        URL resource = classLoader.getResource(path);
        if (resource == null) {
            throw new IOException(path + " not found!");
        } else {
            return new File(resource.getFile());
        }
    }

    /**
     * Get absolute path to file in resources folder.
     *
     * @param path {@link String} path within resources folder.
     * @return {@link String} absolute path to file in resources folder
     * @throws IOException if the resource is not found
     * @throws URISyntaxException if the resource URI is not correctly
     * formatted
     */
    public static String getAbsPathToFileInResources(String path)
            throws IOException, URISyntaxException {

        ClassLoader classLoader = FileUtils.class.getClassLoader();

        URL resource = classLoader.getResource(path);
        if (resource == null) {
            throw new IOException(path + " not found!");
        } else {
            return Paths.get(resource.toURI()).toFile().getAbsolutePath();
        }
    }

    public static List<String> readAllLines(String path) throws IOException {
        return readAllLines(getFileFromResources(path));
    }

    public static List<String> readAllLines(File file) throws IOException {

        List<String> lines = new ArrayList<>();

        if (file == null)
            return lines;

        try (FileReader reader = new FileReader(file);
             BufferedReader br = new BufferedReader(reader)) {

            String line;
            while ((line = br.readLine()) != null) {
                lines.add(line);
            }
        }
        return lines;
    }
}
