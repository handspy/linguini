package pt.up.hs.linguini.jspell;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import pt.up.hs.linguini.utils.FileUtils;

import java.io.*;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.nio.file.Paths;
import java.util.Locale;

/**
 * Wrapper for JSpell process
 *
 * @author José Carlos Paiva <code>josepaiva94@gmail.com</code>
 */
public class JSpellWrapper {
    private static final Logger LOGGER =
            LogManager.getLogger(JSpellWrapper.class.getName());
    private static final Charset DEFAULT_CHARSET = Charset.forName("UTF-8");

    private static final String UJSPELL_ENV_VALUE = System.getProperty("ujspellPath");
    private static final String JSPELL_CMD = UJSPELL_ENV_VALUE != null && !UJSPELL_ENV_VALUE.isEmpty() ?
            UJSPELL_ENV_VALUE : "/usr/local/bin/ujspell";

    private static final String DICTIONARY_PATH_FORMAT =
            "dictionaries/%s";
    private static final String DICTIONARY_NAME =
            "emospell";

    private ProcessBuilder processBuilder;

    private Process process;

    private BufferedWriter writer;
    private BufferedReader reader;

    private boolean stopped = false;

    public JSpellWrapper(Locale locale) throws IOException, URISyntaxException {
        LOGGER.debug("Using ujspell from ... " + JSPELL_CMD);
        processBuilder = new ProcessBuilder(
                JSPELL_CMD,
                "-d",
                Paths.get(
                        FileUtils.getAbsPathToFileInResources(
                                String.format(
                                        DICTIONARY_PATH_FORMAT,
                                        locale.toString()
                                )
                        ),
                        DICTIONARY_NAME
                ).toString(),
                "-a", "--flush");
        processBuilder.environment().put("LANG", locale.toString() + ".UTF-8");
    }

    public void start() throws IOException {

        process = processBuilder.start();

        writer = new BufferedWriter(new OutputStreamWriter(process.getOutputStream(), DEFAULT_CHARSET));
        reader = new BufferedReader(new InputStreamReader(process.getInputStream(), DEFAULT_CHARSET));

        reader.readLine(); // ignore the banner
    }

    public String process(String text) throws IOException {

        // utf8 treatment
        text = text
                .replaceAll("[\\p{C}]", "*") // all control chars
                .replaceAll("[\\xa0\\x91\\x92\\x93\\x94\\x97\\xab\\xa9\\xae\\x{2018}\\x{2019}\\x{201C}\\x{201D}\\x{2022}\\x{2013}\\x{2014}\\x{2122}]", "*")
                .replaceAll("[^\\u0000-\\uFFFF]", "*");

        // write text to process
        writer.write(text);
        writer.newLine();
        writer.flush();

        // read result from processing
        StringBuilder outBuilder = new StringBuilder();

        String line;
        while ((line = reader.readLine()) != null && !"".equals(line)) {
            outBuilder.append(line);
            outBuilder.append('\n');
        }

        return outBuilder.toString();
    }

    public boolean isStopped() {
        return stopped || !process.isAlive();
    }

    public void stop() throws IOException {

        writer.close();
        reader.close();

        process.destroy();

        stopped = true;
    }

    public void close() throws IOException {

        if (!stopped) {
            writer.close();
            reader.close();
        }

        process.destroyForcibly();

        stopped = true;
    }
}
