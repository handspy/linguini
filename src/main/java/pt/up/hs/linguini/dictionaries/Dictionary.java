package pt.up.hs.linguini.dictionaries;

import pt.up.hs.linguini.Config;
import pt.up.hs.linguini.dictionaries.exceptions.DictionaryException;
import pt.up.hs.linguini.exceptions.ConfigException;
import pt.up.hs.linguini.resources.ResourceLoader;
import pt.up.hs.linguini.resources.exceptions.ResourceLoadingException;

import java.util.*;

/**
 * Dictionary of words.
 *
 * @author José Carlos Paiva <code>josepaiva94@gmail.com</code>
 */
public class Dictionary {
    private static final String FILE_PATH_FORMAT =
            "/%s/dictionaries/dictionary.dic";

    private Map<String, HashSet<DictionaryEntry>> dictionary;

    private Locale locale;

    public Dictionary(Locale locale) throws DictionaryException {
        this.locale = locale;
        load();
    }

    private void load() throws DictionaryException {
        try {
            dictionary = ResourceLoader
                    .readDictionaryEntries(String.format(FILE_PATH_FORMAT, locale));

            String[] customDicts = Config.getInstance(locale)
                    .getCustomDictionaries();
            for (String customDictPath: customDicts) {
                Map<String, HashSet<DictionaryEntry>> customEntries = ResourceLoader
                        .readDictionaryEntries(customDictPath);
                for (String key: customEntries.keySet()) {
                    if (dictionary.containsKey(key)) {
                        dictionary.get(key).addAll(customEntries.get(key));
                    } else {
                        dictionary.put(key, customEntries.get(key));
                    }
                }
            }
        } catch (ConfigException | ResourceLoadingException e) {
            throw new DictionaryException("Failed to load the dictionary", e);
        }
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
