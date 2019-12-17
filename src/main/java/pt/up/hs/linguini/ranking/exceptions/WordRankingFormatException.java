package pt.up.hs.linguini.ranking.exceptions;

/**
 * Exception thrown when the word ranking does not have the correct format.
 *
 * @author José Carlos Paiva <code>josepaiva94@gmail.com</code>
 */
public class WordRankingFormatException extends WordRankingReadException {

    public WordRankingFormatException() {
        super("Invalid word ranking format");
    }

    public WordRankingFormatException(int line) {
        super("Invalid word ranking format at line " + line);
    }
}
