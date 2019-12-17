package pt.up.hs.linguini.dictionaries.exceptions;

/**
 * Exception thrown when dictionary does not have the correct format.
 *
 * @author José Carlos Paiva <code>josepaiva94@gmail.com</code>
 */
public class DictionaryFormatException extends DictionaryReadException {

    public DictionaryFormatException() {
        super("Invalid dictionary format");
    }

    public DictionaryFormatException(int line) {
        super("Invalid dictionary format at line " + line);
    }
}
