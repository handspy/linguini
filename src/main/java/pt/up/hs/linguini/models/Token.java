package pt.up.hs.linguini.models;

import edu.stanford.nlp.ling.CoreLabel;

import java.util.Objects;

/**
 * A token of the text.
 *
 * @author José Carlos Paiva <code>josepaiva94@gmail.com</code>
 */
public class Token extends CoreLabel implements HasWord {
    private final int start;
    private final String original;

    private String word;

    public Token(int start, String original) {
        this.start = start;
        this.original = original;
        this.word = original;
    }

    public int getStart() {
        return start;
    }

    public String getOriginal() {
        return original;
    }

    @Override
    public String originalText() {
        return original;
    }

    public String getWord() {
        return word;
    }

    public void setWord(String word) {
        this.word = word;
    }

    @Override
    public String word() {
        return word;
    }

    @Override
    public void word(String word) {
        this.word = word;
    }

    @Override
    public String original() {
        return original;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Token)) return false;
        Token token = (Token) o;
        return start == token.start &&
                Objects.equals(original, token.original) &&
                Objects.equals(word, token.word);
    }

    @Override
    public int hashCode() {
        return Objects.hash(start, original, word);
    }
}
