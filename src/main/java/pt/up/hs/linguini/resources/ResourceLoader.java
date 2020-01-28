package pt.up.hs.linguini.resources;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import pt.up.hs.linguini.caching.InMemoryCache;
import pt.up.hs.linguini.dictionaries.DictionaryEntry;
import pt.up.hs.linguini.models.Replacement;
import pt.up.hs.linguini.ranking.WordRankingEntry;
import pt.up.hs.linguini.resources.exceptions.ResourceFormatException;
import pt.up.hs.linguini.resources.exceptions.ResourceLoadingException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Utilities to read files.
 *
 * @author José Carlos Paiva <code>josepaiva94@gmail.com</code>
 */
public class ResourceLoader {
    private static final String REPLACEMENT_PREFIX_TAG = "prefix";
    private static final String REPLACEMENT_SUFFIX_TAG = "suffix";
    private static final String REPLACEMENT_REPLACEMENT_TAG = "replacement";
    private static final String REPLACEMENT_EXCEPTIONS_ATTRIBUTE = "exceptions";
    private static final String REPLACEMENT_TARGET_ATTRIBUTE = "target";
    private static final String REPLACEMENT_TAG_ATTRIBUTE = "tag";

    private final static InMemoryCache<String, List<String>> stopwordsCache =
            new InMemoryCache<>(86400, 3600, 20);
    private final static InMemoryCache<String, Replacement[]> replacementsCache =
            new InMemoryCache<>(86400, 3600, 100);
    private final static InMemoryCache<String, Map<String, WordRankingEntry>> wordRankingCache =
            new InMemoryCache<>(86400, 3600, 20);
    private final static InMemoryCache<String, Map<String, HashSet<DictionaryEntry>>> dictionaryCache =
            new InMemoryCache<>(86400, 3600, 20);

    /**
     * Read stopwords from a {@link String} path.
     *
     * @param p {@link String} path to a stopwords text file.
     * @return {@link List} stopwords read
     * @throws ResourceLoadingException if an exception occurs while
     *      reading stopwords
     */
    public static List<String> readStopwords(String p)
            throws ResourceLoadingException {
        List<String> stopwords;
        synchronized (stopwordsCache) {
            if ((stopwords = stopwordsCache.get(p)) == null) {
                stopwords = readStopwords(
                        ResourceLoader.class.getResourceAsStream(p));
                stopwordsCache.put(p, stopwords);
            }
        }
        return new ArrayList<>(stopwords);
    }

    /**
     * Read stopwords from an {@link InputStream} stream.
     *
     * @param is {@link InputStream} input stream from a stopwords text file.
     * @return {@link List} stopwords read
     * @throws ResourceLoadingException if an exception occurs while
     *      reading stopwords
     */
    private static List<String> readStopwords(InputStream is)
            throws ResourceLoadingException {

        try {
            List<String> stopwords = readAllLines(is)
                    .parallelStream()
                    .map(s -> s.trim().toLowerCase())
                    .collect(Collectors.toList());

            is.close();

            return stopwords;
        } catch (IOException e) {
            throw new ResourceLoadingException("Reading stopwords", e);
        }
    }

    /**
     * Read replacements from a {@link String} path.
     *
     * @param p {@link String} path to a replacements file.
     * @return {@link Replacement[]} replacements read
     * @throws ResourceLoadingException if an exception occurs while
     *      reading replacements
     */
    public static Replacement[] readReplacements(String p)
            throws ResourceLoadingException {
        Replacement[] replacements;
        synchronized (replacementsCache) {
            if ((replacements = replacementsCache.get(p)) == null) {
                replacements = readReplacements(
                        ResourceLoader.class.getResourceAsStream(p));
                replacementsCache.put(p, replacements);
            }
        }
        return Arrays.copyOf(replacements, replacements.length);
    }

    /**
     * Read replacements from an {@link InputStream} stream.
     *
     * @param is {@link InputStream} input stream from a replacements file.
     * @return {@link Replacement[]} replacements read
     * @throws ResourceLoadingException if an exception occurs while reading
     *                                  replacements
     */
    private static Replacement[] readReplacements(InputStream is)
            throws ResourceLoadingException {

        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();

        Document doc;
        try {
            DocumentBuilder db = dbf.newDocumentBuilder();
            doc = db.parse(is);
            is.close();
        } catch (ParserConfigurationException | SAXException | IOException e) {
            throw new ResourceLoadingException(e);
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

    /**
     * Read word ranking from a {@link String} path.
     *
     * @param p {@link String} path to a word ranking file.
     * @return {@link Map} word ranking entries indexed by word
     * @throws ResourceLoadingException if an exception occurs while
     *      reading word ranking entries
     */
    public static Map<String, WordRankingEntry> readWordRankingEntries(
            String p) throws ResourceLoadingException {
        Map<String, WordRankingEntry> wordRankingEntries;
        synchronized (wordRankingCache) {
            if ((wordRankingEntries = wordRankingCache.get(p)) == null) {
                wordRankingEntries = readWordRankingEntries(
                        ResourceLoader.class.getResourceAsStream(p));
                wordRankingCache.put(p, wordRankingEntries);
            }
        }
        return new HashMap<>(wordRankingEntries);
    }

    /**
     * Read word ranking from an {@link InputStream} stream.
     *
     * @param is {@link InputStream} input stream of a word ranking file.
     * @return {@link Map} word ranking entries indexed by word
     * @throws ResourceLoadingException if an exception occurs while
     *      reading word ranking entries
     */
    private static Map<String, WordRankingEntry> readWordRankingEntries(
            InputStream is) throws ResourceLoadingException {

        Map<String, WordRankingEntry> entries = new HashMap<>();

        try (
                BufferedReader reader = new BufferedReader(
                        new InputStreamReader(is))
        ) {
            String line;
            int rank = 0;
            int lineNumber = 0;
            while ((line = reader.readLine()) != null) {
                lineNumber++;
                line = line.trim();

                if (line.isEmpty() || line.startsWith("#"))
                    continue;

                if (line.contains("\t") && (line.indexOf("\t") + 1 < line.length())) {
                    String word = line
                            .substring(line.indexOf("\t") + 1)
                            .replace("=", " ");
                    int frequency = Integer.parseInt(
                            line.substring(0, line.indexOf("\t")));
                    entries.put(word,
                            new WordRankingEntry(word, frequency, ++rank));
                } else {
                    throw new ResourceFormatException(lineNumber);
                }
            }

            is.close();
        } catch (IOException e) {
            throw new ResourceLoadingException(e);
        }

        return entries;
    }

    /**
     * Read dictionary from a {@link String} path.
     *
     * @param p {@link String} path to a dictionary file.
     * @return {@link Map} dictionary entries indexed by word's inflected form.
     * @throws ResourceLoadingException if an exception occurs while
     *      reading dictionary entries
     */
    public static Map<String, HashSet<DictionaryEntry>> readDictionaryEntries(
            String p) throws ResourceLoadingException {
        Map<String, HashSet<DictionaryEntry>> dictionaryEntries;
        synchronized (dictionaryCache) {
            if ((dictionaryEntries = dictionaryCache.get(p)) == null) {
                dictionaryEntries = readDictionaryEntries(
                        ResourceLoader.class.getResourceAsStream(p));
                dictionaryCache.put(p, dictionaryEntries);
            }
        }
        return new HashMap<>(dictionaryEntries);
    }

    /**
     * Read dictionary from an {@link InputStream} stream.
     *
     * @param is {@link InputStream} input stream of a dictionary file.
     * @return {@link Map} dictionary entries indexed by word's inflected form.
     * @throws ResourceLoadingException if an exception occurs while
     *      reading dictionary entries
     */
    private static Map<String, HashSet<DictionaryEntry>> readDictionaryEntries(
            InputStream is) throws ResourceLoadingException {

        Map<String, HashSet<DictionaryEntry>> dictionary = new HashMap<>();

        try (
                BufferedReader reader = new BufferedReader(
                        new InputStreamReader(is))
        ) {
            String line;
            int lineNumber = 0;
            while ((line = reader.readLine()) != null) {
                lineNumber++;

                line = line.trim();

                if (line.isEmpty() || line.startsWith("#"))
                    continue;

                if (line.contains(",") && line.contains(".")
                        && (line.indexOf(".") > line.indexOf(",") + 1)
                        && (line.indexOf(".") + 1 < line.length())
                        && (((line.charAt(line.indexOf(".") + 1)) != ':')
                        || (line.charAt(line.indexOf(".") + 1)) != '+')) {

                    String inflectedForm = line.substring(0, line.indexOf(","));
                    String lemma = line.substring(line.indexOf(",") + 1, line.indexOf("."));

                    String partOfSpeech;
                    if (line.contains("+")) {
                        partOfSpeech = line.substring(line.indexOf(".") + 1,
                                line.indexOf("+"));
                    } else if (line.contains(":")) {
                        partOfSpeech = line.substring(line.indexOf(".") + 1,
                                line.indexOf(":"));
                    } else {
                        partOfSpeech = line.substring(line.indexOf(".") + 1);
                    }

                    String subcategory = null;
                    if (line.contains("+")) {
                        if (line.contains(":")) {
                            subcategory = line.substring(line.indexOf("+") + 1,
                                    line.indexOf(":"));
                        } else {
                            subcategory = line.substring(line.indexOf("+"));
                        }
                    }

                    String morphAttributes = null;
                    if (line.contains(":")) {
                        morphAttributes = line.substring(line.indexOf(":") + 1);
                    }

                    HashSet<DictionaryEntry> entrySet =
                            dictionary.get(inflectedForm);
                    if (entrySet == null) {
                        entrySet = new HashSet<>();
                    }
                    entrySet.add(new DictionaryEntry(inflectedForm, lemma,
                            partOfSpeech, subcategory, morphAttributes));
                    dictionary.put(inflectedForm, entrySet);
                } else {
                    throw new ResourceFormatException(lineNumber);
                }
            }

            is.close();
        } catch (IOException e) {
            throw new ResourceLoadingException(e);
        }

        return dictionary;
    }

    private static List<String> readAllLines(InputStream is)
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
