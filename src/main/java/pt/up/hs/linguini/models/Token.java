package pt.up.hs.linguini.models;

/**
 * A token of the text.
 *
 * @author José Carlos Paiva <code>josepaiva94@gmail.com</code>
 */
public class Token {
    private int start;
    private String original;

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

    public String getWord() {
        return word;
    }

    public void setWord(String word) {
        this.word = word;
    }
}
