package pt.up.hs.linguini.ranking;

import java.util.Objects;

/**
 * Entry in the ranking of words.
 *
 * @author José Carlos Paiva <code>josepaiva94@gmail.com</code>
 */
public class WordRankingEntry implements Comparable<WordRankingEntry> {
    private String word;
    private int frequency;
    private int rank;

    public WordRankingEntry(String word, int frequency, int rank) {
        this.word = word;
        this.frequency = frequency;
        this.rank = rank;
    }

    public String getWord() {
        return word;
    }

    public int getFrequency() {
        return frequency;
    }

    public int getRank() {
        return rank;
    }

    public String toString() {
        return rank + "\t" + frequency + "\t" + word;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof WordRankingEntry)) return false;
        WordRankingEntry that = (WordRankingEntry) o;
        return frequency == that.frequency &&
                rank == that.rank &&
                Objects.equals(word, that.word);
    }

    @Override
    public int hashCode() {
        return Objects.hash(word, frequency, rank);
    }

    public int compareTo(WordRankingEntry other) {
        if (this.frequency < other.getFrequency()) {
            return 1;
        } else if (this.frequency > other.getFrequency()) {
            return -1;
        }
        return Integer.compare(this.rank, other.getRank());
    }
}
