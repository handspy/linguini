package pt.up.hs.linguini.ranking;

import pt.up.hs.linguini.ranking.exceptions.WordRankingFormatException;
import pt.up.hs.linguini.ranking.exceptions.WordRankingReadException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;

/**
 * Ranking of words.
 *
 * @author José Carlos Paiva <code>josepaiva94@gmail.com</code>
 */
public class WordRanking {
    private static final String FORMS_FILE_PATH_FORMAT =
            "/%s/frequencies/forms.total.txt";
    private static final String LEMMAS_FILE_PATH_FORMAT =
            "/%s/frequencies/lemmas.total.txt";

    private static Map<Locale, WordRanking> wordRanking =
            new HashMap<>();

    private Locale locale;

    private Map<String, WordRankingEntry> formsEntries;
    private Map<String, WordRankingEntry> lemmasEntries;

    private WordRanking(Locale locale) {
        this(locale, new HashMap<>(), new HashMap<>());
    }

    public WordRanking(
            Locale locale,
            Map<String, WordRankingEntry> formsEntries,
            Map<String, WordRankingEntry> lemmasEntries
    ) {
        this.locale = locale;
        this.formsEntries = formsEntries;
        this.lemmasEntries = lemmasEntries;
    }

    public static WordRanking getInstance() throws WordRankingReadException {
        return getInstance(Locale.ENGLISH);
    }

    public static WordRanking getInstance(Locale locale)
            throws WordRankingReadException {
        if (!wordRanking.containsKey(locale)) {
            String formsPath = String.format(FORMS_FILE_PATH_FORMAT,
                    locale.toString());
            InputStream formsIs = WordRanking.class.getResourceAsStream(formsPath);
            Map<String, WordRankingEntry> formsEntries =
                    readWordRankingFile(formsIs);

            String lemmasPath = String.format(LEMMAS_FILE_PATH_FORMAT, locale);
            InputStream lemmasIs = WordRanking.class
                    .getResourceAsStream(lemmasPath);
            Map<String, WordRankingEntry> lemmasEntries =
                    readWordRankingFile(lemmasIs);

            wordRanking.put(locale, new WordRanking(locale, formsEntries, lemmasEntries));
        }
        return wordRanking.get(locale);
    }

    private static Map<String, WordRankingEntry> readWordRankingFile(
            InputStream is) throws WordRankingReadException {

        try (
                BufferedReader reader = new BufferedReader(
                        new InputStreamReader(is))
        ) {
            Map<String, WordRankingEntry> entries = new HashMap<>();

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
                    throw new WordRankingFormatException(lineNumber);
                }
            }

            return entries;
        } catch (IOException e) {
            throw new WordRankingReadException(e);
        }
    }

    /**
     * Get the frequency of a word.
     *
     * @param word {@link String} the word to get the frequency of.
     * @return {@code int} the frequency of the word.
     */
    public int getFrequency(String word) {
        if (lemmasEntries.get(word) != null) {
            return lemmasEntries.get(word).getFrequency();
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
        if (lemmasEntries.get(word) != null) {
            return lemmasEntries.get(word).getRank();
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
