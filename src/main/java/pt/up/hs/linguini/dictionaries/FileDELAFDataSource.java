package pt.up.hs.linguini.dictionaries;

import pt.up.hs.linguini.Config;
import pt.up.hs.linguini.data.DELAFEntry;
import pt.up.hs.linguini.data.IDELAFDataSource;
import pt.up.hs.linguini.exceptions.ConfigException;
import pt.up.hs.linguini.resources.ResourceLoader;
import pt.up.hs.linguini.resources.exceptions.ResourceLoadingException;

import java.util.*;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class FileDELAFDataSource implements IDELAFDataSource {
    private static final String FILE_PATH_FORMAT =
            "/%s/dictionaries/dictionary.dic";

    private static final Logger LOG = Logger.getLogger(FileDELAFDataSource.class.getName());

    protected final Locale locale;
    protected final String path;

    private Map<String, HashSet<DELAFEntry>> dictionary;

    public FileDELAFDataSource(Locale locale, String path) {
        this.locale = locale;
        this.path = path;
        load();
    }

    private void load() {
        try {
            dictionary = ResourceLoader.readDictionaryEntries(path);

            String[] customDicts = Config.getInstance(locale)
                    .getCustomDictionaries();
            for (String customDictPath : customDicts) {
                Map<String, HashSet<DELAFEntry>> customEntries = ResourceLoader
                        .readDictionaryEntries(customDictPath);
                for (String key : customEntries.keySet()) {
                    if (dictionary.containsKey(key)) {
                        dictionary.get(key).addAll(customEntries.get(key));
                    } else {
                        dictionary.put(key, customEntries.get(key));
                    }
                }
            }
        } catch (ConfigException | ResourceLoadingException e) {
            LOG.severe("Failed to load the dictionary");
            e.printStackTrace();
        }
    }

    @Override
    public void add(String word, String lemma, String pos, String subcategory, String morphAttributes) {
        HashSet<DELAFEntry> delafEntries;
        if (dictionary.containsKey(word)) {
            delafEntries = dictionary.get(word);
        } else {
            delafEntries = new HashSet<>();
            dictionary.put(word, delafEntries);
        }
        delafEntries.add(new DELAFEntry(word, lemma, pos, subcategory, morphAttributes));
    }

    @Override
    public Collection<DELAFEntry> remove(String word) {
        return dictionary.remove(word);
    }

    @Override
    public Collection<DELAFEntry> remove(String word, String pos) {
        HashSet<DELAFEntry> entries = dictionary.get(word);
        if (entries != null) {
            List<DELAFEntry> removedEntries = entries.parallelStream()
                    .filter(delafEntry -> delafEntry.getPos().equals(pos))
                    .collect(Collectors.toList());
            if (removedEntries.isEmpty()) {
                return null;
            }
            entries.removeAll(removedEntries);
            return removedEntries;
        }
        return null;
    }

    @Override
    public Collection<DELAFEntry> getAllEntries() {
        return dictionary.values().parallelStream()
                .flatMap(Collection::parallelStream)
                .collect(Collectors.toList());
    }

    @Override
    public Collection<DELAFEntry> getEntries(String word) {
        if (dictionary.containsKey(word)) {
            return dictionary.get(word);
        }
        return new HashSet<>();
    }

    @Override
    public boolean contains(String word) {
        return dictionary.containsKey(word);
    }

    @Override
    public boolean contains(String word, String posTag) {
        if (contains(word)) {
            HashSet<DELAFEntry> entrySet = dictionary.get(word);
            for (DELAFEntry entry : entrySet) {
                if (entry.getPos().equals(posTag)) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public Collection<String> getLemmas(String word, String posTag) {
        if (contains(word)) {
            HashSet<DELAFEntry> entrySet = dictionary.get(word);
            return entrySet.parallelStream()
                    .filter(delafEntry -> delafEntry.getPos().equals(posTag))
                    .map(DELAFEntry::getLemma)
                    .collect(Collectors.toSet());
        }
        return new HashSet<>();
    }

    @Override
    public int size() {
        return dictionary.keySet().size();
    }
}
