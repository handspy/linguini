package pt.up.hs.linguini.analysis.cooccurrence;

import java.util.Objects;

/**
 * Representation of a word-word co-occurrence.
 *
 * @author José Carlos Paiva <code>josepaiva94@gmail.com</code>
 */
public class Cooccurrence {

    private String firstWord;
    private String secondWord;

    private double value;

    public Cooccurrence() {
    }

    public Cooccurrence(String firstWord, String secondWord, double value) {
        this.firstWord = firstWord;
        this.secondWord = secondWord;
        this.value = value;
    }

    public String getFirstWord() {
        return firstWord;
    }

    public void setFirstWord(String firstWord) {
        this.firstWord = firstWord;
    }

    public String getSecondWord() {
        return secondWord;
    }

    public void setSecondWord(String secondWord) {
        this.secondWord = secondWord;
    }

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Cooccurrence)) return false;
        Cooccurrence that = (Cooccurrence) o;
        return Double.compare(that.value, value) == 0 &&
                Objects.equals(firstWord, that.firstWord) &&
                Objects.equals(secondWord, that.secondWord);
    }

    @Override
    public int hashCode() {
        return Objects.hash(firstWord, secondWord, value);
    }
}
