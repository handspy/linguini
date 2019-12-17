package pt.up.hs.linguini.dictionaries;

import pt.up.hs.linguini.Config;
import pt.up.hs.linguini.dictionaries.exceptions.DictionaryFormatException;
import pt.up.hs.linguini.dictionaries.exceptions.DictionaryReadException;
import pt.up.hs.linguini.exceptions.ConfigException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;

/**
 * Dictionary of words.
 *
 * @author José Carlos Paiva <code>josepaiva94@gmail.com</code>
 */
public class Dictionary {
    private static final String FILE_PATH_FORMAT =
            "/%s/dictionaries/dictionary.dic";

    private static Map<Locale, Dictionary> dictionaries = new HashMap<>();

    private HashMap<String, HashSet<DictionaryEntry>> dictionary =
            new HashMap<>();

    private Locale locale;

    public Dictionary(Locale locale) {
        this.locale = locale;
    }

    public static Dictionary getInstance() throws DictionaryReadException {
        return getInstance(Locale.ENGLISH);
    }

    public static Dictionary getInstance(Locale locale)
            throws DictionaryReadException {
        if (!dictionaries.containsKey(locale)) {
            String path = String.format(FILE_PATH_FORMAT, locale);
            InputStream is = Dictionary.class.getResourceAsStream(path);
            Dictionary dictionary = new Dictionary(locale);
            dictionary.readDictionary(is);

            try {
                String[] customDicts = Config.getInstance(locale)
                        .getCustomDictionaries();
                for (String customDictPath: customDicts) {
                    InputStream customDictIs = Dictionary.class
                            .getResourceAsStream(customDictPath);
                    dictionary.readDictionary(customDictIs);
                }
            } catch (ConfigException e) {
                throw new DictionaryReadException("Reading custom dictionaries");
            }

            dictionaries.put(locale, dictionary);
        }
        return dictionaries.get(locale);
    }

    /**
     * Read a dictionary from an input stream.
     *
     * @param is {@link InputStream} input stream to read from
     */
    public void readDictionary(InputStream is) throws DictionaryReadException {

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

                    add(new DictionaryEntry(inflectedForm, lemma, partOfSpeech,
                            subcategory, morphAttributes));
                } else {
                    throw new DictionaryFormatException(lineNumber);
                }
            }
        } catch (IOException e) {
            throw new DictionaryReadException(e);
        }
    }

    /**
     * Add entry to the dictionary.
     *
     * @param entry {@link DictionaryEntry} entry in the dictionary.
     */
    public void add(DictionaryEntry entry) {
        HashSet<DictionaryEntry> entrySet =
                dictionary.get(entry.getInflectedForm());
        if (entrySet == null) {
            entrySet = new HashSet<>();
        }
        entrySet.add(entry);
        dictionary.put(entry.getInflectedForm(), entrySet);
    }

    /**
     * Remove entries with given inflected form from the dictionary.
     *
     * @param inflectedForm {@link String} inflected form of the entries to
     *                                     remove.
     * @return {@link DictionaryEntry[]} removed entries.
     */
    public DictionaryEntry[] remove(String inflectedForm) {
        HashSet<DictionaryEntry> removedEntries = dictionary.remove(inflectedForm);
        if (removedEntries != null) {
            return removedEntries.toArray(new DictionaryEntry[0]);
        }
        return null;
    }

    /**
     * Remove entries with given inflected form and PoS tag from the
     * dictionary.
     *
     * @param inflectedForm {@link String} inflected form of the entries to
     *                                     remove.
     * @param posTag {@link String} Part of speech tag.
     * @return {@link DictionaryEntry[]} removed entries.
     */
    public DictionaryEntry[] remove(String inflectedForm, String posTag) {
        HashSet<DictionaryEntry> entrySet = dictionary.remove(inflectedForm);
        HashSet<DictionaryEntry> remainingEntries = new HashSet<>();
        HashSet<DictionaryEntry> removedEntries = new HashSet<>();

        for (DictionaryEntry entry : entrySet) {
            if (entry.getInflectedForm().equals(inflectedForm)
                    && entry.getPartOfSpeech().equals(posTag)) {
                removedEntries.add(entry);
            } else {
                remainingEntries.add(entry);
            }
        }

        if (remainingEntries.size() > 0) {
            dictionary.put(inflectedForm, remainingEntries);
        }

        if (removedEntries.size() > 0) {
            return removedEntries.toArray(new DictionaryEntry[0]);
        }

        return null;
    }

    /**
     * Remove entry from the dictionary.
     *
     * @param entry {@link DictionaryEntry} entry to remove.
     * @return {@link DictionaryEntry} removed entry
     */
    public DictionaryEntry remove(DictionaryEntry entry) {
        HashSet<DictionaryEntry> entrySet =
                dictionary.get(entry.getInflectedForm());
        if (entrySet != null) {
            entrySet.remove(entry);
            if (entrySet.size() > 0) {
                dictionary.put(entry.getInflectedForm(), entrySet);
            } else {
                dictionary.remove(entry.getInflectedForm());
            }
            return entry;
        }
        return null;
    }

    /**
     * Check if the dictionary contains entries with given inflected form.
     *
     * @param inflectedForm {@link String} inflected form of the entries to
     *                                     remove.
     * @return {@code boolean} true if it contains some matching entry, false
     *                         otherwise
     */
    public boolean contains(String inflectedForm) {
        return dictionary.containsKey(inflectedForm);
    }

    /**
     * Check if the dictionary contains entries with given inflected form and
     * PoS tag.
     *
     * @param inflectedForm {@link String} inflected form of the entries to
     *                                     remove.
     * @param posTag {@link String} Part of speech tag.
     * @return {@code boolean} true if it contains some matching entry, false
     *                         otherwise
     */
    public boolean contains(String inflectedForm, String posTag) {
        if (contains(inflectedForm)) {
            HashSet<DictionaryEntry> entrySet = dictionary.get(inflectedForm);
            for (DictionaryEntry entry : entrySet) {
                if (entry.getPartOfSpeech().equals(posTag)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Check if the dictionary contains the specified entry.
     *
     * @param entry {@link DictionaryEntry} entry to remove.
     * @return {@code boolean} true if it contains some matching entry, false
     *                         otherwise
     */
    public boolean contains(DictionaryEntry entry) {
        if (contains(entry.getInflectedForm())) {
            HashSet<DictionaryEntry> entrySet =
                    dictionary.get(entry.getInflectedForm());
            return entrySet.contains(entry);
        }
        return false;
    }

    /**
     * Get lemmas from dictionary matching given inflected form and PoS tag.
     *
     * @param inflectedForm {@link String} inflected form of the lemmas to
     *                                     retrieve.
     * @param posTag {@link String} Part of speech tag of the lemmas to
     *                              retrieve.
     * @return {@code String[]} lemmas to retrieve.
     */
    public String[] retrieveLemmas(String inflectedForm, String posTag) {
        HashSet<DictionaryEntry> entrySet = dictionary.get(inflectedForm);
        HashSet<String> lemmas = new HashSet<>();
        for (DictionaryEntry entry : entrySet) {
            if (entry.getPartOfSpeech().equals(posTag)) {
                lemmas.add(entry.getLemma());
            }
        }
        return lemmas.toArray(new String[0]);
    }

    /**
     * Get entries from dictionary matching given inflected form.
     *
     * @param inflectedForm {@link String} inflected form of the entries to
     *                                     retrieve.
     * @return {@code DictionaryEntry[]} matching entries.
     */
    public DictionaryEntry[] retrieveEntries(String inflectedForm) {
        HashSet<DictionaryEntry> entries = new HashSet<>();
        if (dictionary.containsKey(inflectedForm)) {
            entries = dictionary.get(inflectedForm);
        }
        return entries.toArray(new DictionaryEntry[0]);
    }

    /**
     * Get all entries from dictionary.
     *
     * @return {@code DictionaryEntry[]} dictionary entries.
     */
    public DictionaryEntry[] retrieveAllEntries() {
        HashSet<DictionaryEntry> entries = new HashSet<>();
        Collection<HashSet<DictionaryEntry>> entryCollection = dictionary.values();
        for (HashSet<DictionaryEntry> entrySet : entryCollection) {
            entries.addAll(entrySet);
        }
        return entries.toArray(new DictionaryEntry[0]);
    }

    /**
     * Get lexicon from dictionary.
     *
     * @return {@link Lexicon} lexicon from dictionary
     */
    public Lexicon retrieveLexicon() {
        return Lexicon.fromDictionaryMap(dictionary);
    }

    /**
     * Get size of the lexicon.
     *
     * @return size of the lexicon
     */
    public int size() {
        return dictionary.size();
    }
}
