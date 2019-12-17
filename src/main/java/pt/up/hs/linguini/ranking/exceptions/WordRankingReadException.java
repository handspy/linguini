package pt.up.hs.linguini.ranking.exceptions;

/**
 * Exception thrown when a problem occurs while reading a word ranking.
 *
 * @author José Carlos Paiva <code>josepaiva94@gmail.com</code>
 */
public class WordRankingReadException extends Exception {

    public WordRankingReadException() {
        super("Could not read word ranking");
    }

    public WordRankingReadException(String message) {
        super(message);
    }

    public WordRankingReadException(Throwable e) {
        super("Could not read word ranking", e);
    }
}
