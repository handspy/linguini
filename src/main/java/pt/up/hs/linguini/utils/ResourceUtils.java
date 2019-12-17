package pt.up.hs.linguini.utils;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import pt.up.hs.linguini.exceptions.ReplacementException;
import pt.up.hs.linguini.models.Replacement;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
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
public class ResourceUtils {

    //

    // replacements
    private static final String REPLACEMENT_PREFIX_TAG = "prefix";
    private static final String REPLACEMENT_SUFFIX_TAG = "suffix";
    private static final String REPLACEMENT_REPLACEMENT_TAG = "replacement";
    private static final String REPLACEMENT_EXCEPTIONS_ATTRIBUTE = "exceptions";
    private static final String REPLACEMENT_TARGET_ATTRIBUTE = "target";
    private static final String REPLACEMENT_TAG_ATTRIBUTE = "tag";

    public static List<String> readAllLines(String path) throws IOException {

        ClassLoader classLoader = ResourceUtils.class.getClassLoader();

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

    /**
     * Read replacements from an {@link InputStream} stream.
     *
     * @param is {@link InputStream} input stream from a replacements file.
     * @return {@link Replacement[]} replacements read
     * @throws ReplacementException if an exception occurs while reading
     *                                  replacements
     */
    public static Replacement[] readReplacements(InputStream is)
            throws ReplacementException {

        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();

        Document doc;
        try {
            DocumentBuilder db = dbf.newDocumentBuilder();
            doc = db.parse(is);
            is.close();
        } catch (ParserConfigurationException | SAXException | IOException e) {
            throw new ReplacementException(e);
        }

        doc.getDocumentElement().normalize();

        NodeList entries = doc.getElementsByTagName(REPLACEMENT_PREFIX_TAG);
        String prefix;
        if (entries.getLength() > 0) {
            prefix = entries.item(0).getTextContent();
        } else {
            prefix = "";
        }

        entries = doc.getElementsByTagName(REPLACEMENT_SUFFIX_TAG);
        String suffix;
        if (entries.getLength() > 0) {
            suffix = entries.item(0).getTextContent();
        } else {
            suffix = "";
        }

        entries = doc.getElementsByTagName(REPLACEMENT_REPLACEMENT_TAG);
        Replacement[] replacements = new Replacement[entries.getLength()];
        for (int i = 0; i < entries.getLength(); i++) {
            Node entry = entries.item(i);
            String replacement = entry.getTextContent();
            String exceptions = ((Element) entry)
                    .getAttribute(REPLACEMENT_EXCEPTIONS_ATTRIBUTE);
            String target = ((Element) entry).getAttribute(REPLACEMENT_TARGET_ATTRIBUTE);
            String tag = ((Element) entry).getAttribute(REPLACEMENT_TAG_ATTRIBUTE);
            replacements[i] = new Replacement(prefix, target, suffix, tag,
                    exceptions, replacement);
        }

        return replacements;
    }
}
