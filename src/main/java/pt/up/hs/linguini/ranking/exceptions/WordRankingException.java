package pt.up.hs.linguini.ranking.exceptions;

import pt.up.hs.linguini.exceptions.LinguiniException;

/**
 * Exception thrown when a problem occurs while using word ranking.
 *
 * @author José Carlos Paiva <code>josepaiva94@gmail.com</code>
 */
public class WordRankingException extends LinguiniException {

    public WordRankingException() {
        super("Could not read word ranking");
    }

    public WordRankingException(String message) {
        super(message);
    }

    public WordRankingException(Throwable e) {
        super("Could not read word ranking", e);
    }

    public WordRankingException(String message, Throwable cause) {
        super(message, cause);
    }
}
