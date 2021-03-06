package pt.up.hs.linguini.resources;

import com.fasterxml.jackson.databind.ObjectMapper;
import pt.up.hs.linguini.caching.InMemoryCache;
import pt.up.hs.linguini.data.DELAFEntry;
import pt.up.hs.linguini.models.Emotion;
import pt.up.hs.linguini.models.Replacement;
import pt.up.hs.linguini.models.Replacements;
import pt.up.hs.linguini.ranking.WordRankingEntry;
import pt.up.hs.linguini.resources.exceptions.ResourceFormatException;
import pt.up.hs.linguini.resources.exceptions.ResourceLoadingException;

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
    private final static InMemoryCache<String, List<String>> stopwordsCache =
            new InMemoryCache<>(0, 3600, 20);
    /*private final static InMemoryCache<String, Replacement[]> replacementsCache =
            new InMemoryCache<>(0, 3600, 100);*/
    /*private final static InMemoryCache<String, Map<String, WordRankingEntry>> wordRankingCache =
            new InMemoryCache<>(0, 3600, 20);*/
    private final static InMemoryCache<String, Map<String, HashSet<DELAFEntry>>> dictionaryCache =
            new InMemoryCache<>(0, 3600, 20);
    private static final InMemoryCache<String, Map<String, List<Emotion>>> emotaixCache =
            new InMemoryCache<>(0, 3600, 20);

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
        return stopwords;
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
        /*Replacement[] replacements;
        synchronized (replacementsCache) {
            if ((replacements = replacementsCache.get(p)) == null) {
                replacements = readReplacements(
                        ResourceLoader.class.getResourceAsStream(p));
                // replacementsCache.put(p, replacements);
            }
        }*/
        return readReplacements(
                ResourceLoader.class.getResourceAsStream(p));
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

        Replacements replacements;
        try {
            replacements = new ObjectMapper().readValue(is, Replacements.class);
            is.close();
        } catch (IOException e) {
            throw new ResourceLoadingException("Reading replacements", e);
        }

        return replacements.getReplacements();
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

        /*Map<String, WordRankingEntry> wordRankingEntries;
        synchronized (wordRankingCache) {
            if ((wordRankingEntries = wordRankingCache.get(p)) == null) {
                wordRankingEntries = readWordRankingEntries(
                        ResourceLoader.class.getResourceAsStream(p));
                wordRankingCache.put(p, wordRankingEntries);
            }
        }*/

        return new HashMap<>();
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

        /*try (
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
        }*/

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
    public static Map<String, HashSet<DELAFEntry>> readDictionaryEntries(
            String p) throws ResourceLoadingException {
        Map<String, HashSet<DELAFEntry>> dictionaryEntries;
        synchronized (dictionaryCache) {
            if ((dictionaryEntries = dictionaryCache.get(p)) == null) {
                dictionaryEntries = readDictionaryEntries(
                        ResourceLoader.class.getResourceAsStream(p));
                dictionaryCache.put(p, dictionaryEntries);
            }
        }
        return dictionaryEntries;
    }

    /**
     * Read dictionary from an {@link InputStream} stream.
     *
     * @param is {@link InputStream} input stream of a dictionary file.
     * @return {@link Map} dictionary entries indexed by word's inflected form.
     * @throws ResourceLoadingException if an exception occurs while
     *      reading dictionary entries
     */
    private static Map<String, HashSet<DELAFEntry>> readDictionaryEntries(
            InputStream is) throws ResourceLoadingException {

        Map<String, HashSet<DELAFEntry>> dictionary = new HashMap<>();

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

                    String word = line.substring(0, line.indexOf(","));
                    String lemma = line.substring(line.indexOf(",") + 1, line.indexOf("."));

                    String pos;
                    if (line.contains("+")) {
                        pos = line.substring(line.indexOf(".") + 1,
                                line.indexOf("+"));
                    } else if (line.contains(":")) {
                        pos = line.substring(line.indexOf(".") + 1,
                                line.indexOf(":"));
                    } else {
                        pos = line.substring(line.indexOf(".") + 1);
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

                    HashSet<DELAFEntry> entrySet =
                            dictionary.get(word);
                    if (entrySet == null) {
                        entrySet = new HashSet<>();
                    }
                    entrySet.add(new DELAFEntry(word, lemma, pos, subcategory, morphAttributes));
                    dictionary.put(word, entrySet);
                } else {
                    throw new ResourceFormatException(lineNumber);
                }
            }
        } catch (IOException e) {
            throw new ResourceLoadingException(e);
        }

        return dictionary;
    }

    /**
     * Read emotaix entries from a {@link String} path.
     *
     * @param p {@link String} path to a emotaix entries JSON file.
     * @return {@link List} stopwords read
     * @throws ResourceLoadingException if an exception occurs while
     *      reading stopwords
     */
    public static Map<String, List<Emotion>> readEmotaixEntries(String p)
            throws ResourceLoadingException {
        Map<String, List<Emotion>> entries;
        synchronized (emotaixCache) {
            if ((entries = emotaixCache.get(p)) == null) {
                entries = readEmotaixEntries(
                        ResourceLoader.class.getResourceAsStream(p));
                emotaixCache.put(p, entries);
            }
        }
        return entries;
    }

    /**
     * Read emotaix entries from an {@link InputStream} stream.
     *
     * @param is {@link InputStream} input stream from a emotaix JSON file.
     * @return {@link Map} entries read
     * @throws ResourceLoadingException if an exception occurs while
     *      reading entries.
     */
    private static Map<String, List<Emotion>> readEmotaixEntries(InputStream is)
            throws ResourceLoadingException {

        try {
            Map<String, List<LinkedHashMap<String, String>>> entries =
                    new ObjectMapper().readValue(is, Map.class);
            Map<String, List<Emotion>> emotaixEntries = new HashMap<>();
            for (String key: entries.keySet()) {
                List<LinkedHashMap<String, String>> value = entries.get(key);
                List<Emotion> emotions = value.parallelStream().map(hm -> {
                    Emotion emotion = new Emotion();
                    if (hm.containsKey("polarity")) {
                        emotion.setPolarity(Emotion.Polarity.valueOf(hm.get("polarity")));
                    }
                    if (hm.containsKey("primary") && !hm.get("primary").isEmpty()) {
                        emotion.setGlobal(Emotion.Global.valueOf(hm.get("primary")));
                    }
                    if (hm.containsKey("secondary") && !hm.get("secondary").isEmpty()) {
                        emotion.setIntermediate(Emotion.Intermediate.valueOf(hm.get("secondary")));
                    }
                    if (hm.containsKey("tertiary") && !hm.get("tertiary").isEmpty()) {
                        emotion.setSpecific(Emotion.Specific.valueOf(hm.get("tertiary")));
                    }
                    return emotion;
                }).collect(Collectors.toList());
                emotaixEntries.put(key, emotions);
            }

            is.close();

            return emotaixEntries;
        } catch (IOException e) {
            throw new ResourceLoadingException("Reading emotaix entries", e);
        }
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
