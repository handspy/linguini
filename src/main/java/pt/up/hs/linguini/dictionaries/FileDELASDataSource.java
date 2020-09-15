package pt.up.hs.linguini.dictionaries;

import pt.up.hs.linguini.data.DELASEntry;
import pt.up.hs.linguini.data.IDELASDataSource;

import java.util.*;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class FileDELASDataSource implements IDELASDataSource {
    private static final Logger LOG = Logger.getLogger(FileDELASDataSource.class.getName());

    private final Locale locale;

    private Map<String, HashSet<DELASEntry>> dictionary;

    public FileDELASDataSource(Locale locale) {
        this.locale = locale;
    }

    @Override
    public void add(String lemma, String posTag) {
        HashSet<DELASEntry> delasEntries;
        if (dictionary.containsKey(lemma)) {
            delasEntries = dictionary.get(lemma);
        } else {
            delasEntries = new HashSet<>();
            dictionary.put(lemma, delasEntries);
        }
        delasEntries.add(new DELASEntry(lemma, posTag));
    }

    @Override
    public HashSet<DELASEntry> remove(String lemma) {
        return dictionary.remove(lemma);
    }

    @Override
    public List<DELASEntry> remove(String lemma, String posTag) {
        HashSet<DELASEntry> entries = dictionary.get(lemma);
        if (entries != null) {
            List<DELASEntry> removedEntries = entries.parallelStream()
                    .filter(delasEntry -> delasEntry.getPos().equals(posTag))
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
    public Collection<DELASEntry> getAllEntries() {
        return dictionary.values().parallelStream()
                .flatMap(Collection::parallelStream)
                .collect(Collectors.toList());
    }

    @Override
    public Collection<DELASEntry> getEntries(String lemma) {
        if (dictionary.containsKey(lemma)) {
            return dictionary.get(lemma);
        }
        return new HashSet<>();
    }

    @Override
    public boolean contains(String lemma) {
        return dictionary.containsKey(lemma);
    }

    @Override
    public boolean contains(String lemma, String posTag) {
        if (contains(lemma)) {
            HashSet<DELASEntry> entrySet = dictionary.get(lemma);
            for (DELASEntry entry : entrySet) {
                if (entry.getPos().equals(posTag)) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public int size() {
        return dictionary.keySet().size();
    }
}
