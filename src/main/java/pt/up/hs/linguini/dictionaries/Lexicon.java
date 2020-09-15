package pt.up.hs.linguini.dictionaries;

import pt.up.hs.linguini.data.DELAFEntry;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Lexicon.
 *
 * @author José Carlos Paiva <code>josepaiva94@gmail.com</code>
 */
public class Lexicon {

    private final HashMap<String, HashSet<String>> lexicon =
            new HashMap<>();

    private Lexicon() {
    }

    public static Lexicon fromDictionaryMap(Map<String, HashSet<DELAFEntry>> dict) {
        Lexicon lexicon = new Lexicon();
        for (String form : dict.keySet()) {
            for (DELAFEntry entry : dict.get(form)) {
                lexicon.add(entry.getLemma(), entry.getPos());
            }
        }
        return lexicon;
    }

    /**
     * Add entry to the lexicon.
     *
     * @param lemma {@link String} lexeme in the lexicon.
     * @param posTag {@link String} Part-Of-Speech tag of the lexicon.
     */
    public void add(String lemma, String posTag) {
        HashSet<String> entrySet = lexicon.get(lemma);
        if (entrySet == null) {
            entrySet = new HashSet<>();
        }
        entrySet.add(posTag);
        lexicon.put(lemma, entrySet);
    }

    /**
     * Remove entries with given lemma from the lexicon.
     *
     * @param lemma {@link String} lemma of the entries to remove.
     * @return {@link String[]} removed entries.
     */
    public String[] remove(String lemma) {
        HashSet<String> removedEntries = lexicon.remove(lemma);
        if (removedEntries != null) {
            return removedEntries.toArray(new String[0]);
        }
        return null;
    }

    /**
     * Remove entries with given lemma and PoS tag from the lexicon.
     *
     * @param lemma {@link String} lemma of the entries to remove.
     * @param posTag {@link String} Part of speech tag of the entries to
     *                              remove.
     * @return {@link String} removed entry.
     */
    public String remove(String lemma, String posTag) {
        HashSet<String> posTagSet = lexicon.remove(lemma);
        HashSet<String> remainingPoSTags = new HashSet<>();
        if (posTagSet != null) {
            for (String pos : posTagSet) {
                if (!pos.equals(posTag)) {
                    remainingPoSTags.add(pos);
                }
            }
            if (remainingPoSTags.size() > 0) {
                lexicon.put(lemma, remainingPoSTags);
            }
            return posTag;
        }
        return null;
    }

    /**
     * Check if the lexicon contains entries with given lemma.
     *
     * @param lemma {@link String} lemma to check.
     * @return {@code boolean} true if it contains some matching entry, false
     *                         otherwise
     */
    public boolean contains(String lemma) {
        return lexicon.containsKey(lemma);
    }

    /**
     * Check if the lexicon contains entries with given lemma and PoS tag.
     *
     * @param lemma {@link String} inflected form of the entries to
     *                                     remove.
     * @param posTag {@link String} Part of speech tag.
     * @return {@code boolean} true if it contains some matching entry, false
     *                         otherwise
     */
    public boolean contains(String lemma, String posTag) {
        if (contains(lemma)) {
            HashSet<String> entrySet = lexicon.get(lemma);
            for (String entry : entrySet) {
                if (entry.equals(posTag)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Get lemmas from dictionary matching given PoS tag.
     *
     * @param posTag {@link String} Part of speech tag of the lemmas to
     *                              retrieve.
     * @return {@code String[]} matching entries.
     */
    public String[] retrieveLemmas(String posTag) {
        HashSet<String> lexemes = new HashSet<>();
        Set<Map.Entry<String, HashSet<String>>> entrySet = lexicon.entrySet();
        for (Map.Entry<String, HashSet<String>> entry : entrySet) {
            if (entry.getValue().contains(posTag)) {
                lexemes.add(entry.getKey());
            }
        }
        return lexemes.toArray(new String[0]);
    }

    /**
     * Get PoS tag from dictionary matching given lemma.
     *
     * @param lemma {@link String} lemma of the entries to retrieve.
     * @return {@code String[]} matching entries.
     */
    public String[] retrievePoSTags(String lemma) {
        HashSet<String> posTags = new HashSet<>();
        Set<Map.Entry<String, HashSet<String>>> entrySet = lexicon.entrySet();
        for (Map.Entry<String, HashSet<String>> entry : entrySet) {
            if (entry.getKey().equals(lemma)) {
                posTags.addAll(entry.getValue());
            }
        }
        return posTags.toArray(new String[0]);
    }

    /**
     * Get size of the lexicon.
     *
     * @return size of the lexicon
     */
    public int size() {
        return lexicon.size();
    }
}
