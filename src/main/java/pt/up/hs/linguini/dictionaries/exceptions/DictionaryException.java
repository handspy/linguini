package pt.up.hs.linguini.dictionaries.exceptions;

import pt.up.hs.linguini.exceptions.LinguiniException;

/**
 * Exception thrown when a problem occurs while using the dictionary.
 *
 * @author José Carlos Paiva <code>josepaiva94@gmail.com</code>
 */
public class DictionaryException extends LinguiniException {

    public DictionaryException(String message) {
        super(message);
    }

    public DictionaryException(String message, Throwable cause) {
        super(message, cause);
    }
}
