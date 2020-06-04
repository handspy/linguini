package pt.up.hs.linguini.emotaix;

import com.fasterxml.jackson.databind.ObjectMapper;
import pt.up.hs.linguini.dictionaries.exceptions.DictionaryException;
import pt.up.hs.linguini.exceptions.ConfigException;
import pt.up.hs.linguini.models.Emotion;
import pt.up.hs.linguini.resources.ResourceLoader;
import pt.up.hs.linguini.resources.exceptions.ResourceLoadingException;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class Emotaix {
    private static final String FILE_PATH_FORMAT =
            "/%s/emotions/emotaix.json";

    private Map<String, List<Emotion>> dictionary = null;

    private Locale locale;

    public Emotaix(Locale locale) throws DictionaryException {
        this.locale = locale;
        load();
    }

    private void load() throws DictionaryException {
        try {
            dictionary = ResourceLoader
                    .readEmotaixEntries(String.format(FILE_PATH_FORMAT, locale));
        } catch (ResourceLoadingException e) {
            throw new DictionaryException("Failed to load the dictionary", e);
        }
    }

    /**
     * Check if the emotaix contains entries with given word.
     *
     * @param word {@link String} word of the entries.
     * @return {@code boolean} true if it contains some matching entry, false
     *                         otherwise
     */
    public boolean contains(String word) {
        return dictionary.containsKey(word);
    }

    /**
     * Get emotions from emotaix for given word.
     *
     * @param word {@link String} word to retrieve emotions.
     * @return {@code String[]} lemmas to retrieve.
     */
    public List<Emotion> retrieveEmotions(String word) {
        return dictionary.get(word);
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
