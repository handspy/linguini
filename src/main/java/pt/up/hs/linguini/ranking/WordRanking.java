package pt.up.hs.linguini.ranking;

import pt.up.hs.linguini.ranking.exceptions.WordRankingException;
import pt.up.hs.linguini.resources.ResourceLoader;
import pt.up.hs.linguini.resources.exceptions.ResourceLoadingException;

import java.util.*;

/**
 * Ranking of words.
 *
 * @author José Carlos Paiva <code>josepaiva94@gmail.com</code>
 */
public class WordRanking {
    private static final String FILE_PATH_FORMAT =
            "/%s/frequencies/total.txt";

    private Locale locale;

    private Map<String, WordRankingEntry> entries;

    private WordRanking() throws WordRankingException {
        this(Locale.getDefault());
    }

    public WordRanking(Locale locale) throws WordRankingException {
        this.locale = locale;
        load();
    }

    private void load() throws WordRankingException {
        try {
            this.entries = ResourceLoader.readWordRankingEntries(
                    String.format(FILE_PATH_FORMAT, locale));
        } catch (ResourceLoadingException e) {
            throw new WordRankingException("Could not load word ranking entries", e);
        }
    }

    /**
     * Get the frequency of a word.
     *
     * @param word {@link String} the word to get the frequency of.
     * @return {@code int} the frequency of the word.
     */
    public int getFrequency(String word) {
        if (entries.get(word) != null) {
            return entries.get(word).getFrequency();
        }
        return -1;
    }

    /**
     * Get the ranking of a word.
     *
     * @param word {@link String} the word to get the ranking of.
     * @return {@code int} the ranking of the word.
     */
    public int getRank(String word) {
        if (entries.get(word) != null) {
            return entries.get(word).getRank();
        }
        return -1;
    }

    /**
     * Sort the given words by ranking.
     *
     * @param words {@code String[]} the words to sort.
     * @return {@code String[]} the sorted words.
     */
    public String[] rank(String[] words) {
        ArrayList<WordRankingEntry> rankedList = new ArrayList<>();
        for (String word : words) {
            rankedList.add(new WordRankingEntry(word, getFrequency(word),
                    getRank(word)));
        }
        Collections.sort(rankedList);
        String[] rankedWords = new String[rankedList.size()];
        for (int i = 0; i < rankedWords.length; i++) {
            rankedWords[i] = rankedList.get(i).getWord();
        }
        return rankedWords;
    }

    /**
     * Sort the top {@code limit} of the given words by ranking.
     *
     * @param words {@code String[]} the words to sort
     * @param limit {@code int} the limit
     * @return {@code String[]} the sorted words.
     */
    public String[] rank(String[] words, int limit) {
        ArrayList<WordRankingEntry> rankedList = new ArrayList<>();
        for (String word : words) {
            rankedList.add(new WordRankingEntry(word, getFrequency(word),
                    getRank(word)));
        }
        Collections.sort(rankedList);
        int length = rankedList.size();
        if (length > limit) {
            length = limit;
        }
        String[] rankedWords = new String[length];
        for (int i = 0; i < length; i++) {
            rankedWords[i] = rankedList.get(i).getWord();
        }
        return rankedWords;
    }

    /**
     * Retrieves the most frequent word from a list.
     *
     * @param words {@code String[]} the list of words.
     * @return {@link String} the most frequent word from the list
     */
    public String retrieveTopWord(String[] words) {
        String topWord = null;
        if (words.length > 0) {
            topWord = rank(words, 1)[0];
        }
        return topWord;
    }
}
