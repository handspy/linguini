package pt.up.hs.linguini.dictionaries.exceptions;

/**
 * Exception thrown when a problem occurs while reading the dictionary.
 *
 * @author José Carlos Paiva <code>josepaiva94@gmail.com</code>
 */
public class DictionaryReadException extends Exception {

    public DictionaryReadException() {
        this("Could not read dictionary");
    }

    public DictionaryReadException(Throwable e) {
        this("Could not read dictionary", e);
    }

    public DictionaryReadException(String message) {
        super(message);
    }

    public DictionaryReadException(String message, Throwable cause) {
        super(message, cause);
    }
}
