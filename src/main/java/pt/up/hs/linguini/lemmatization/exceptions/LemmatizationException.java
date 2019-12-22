package pt.up.hs.linguini.lemmatization.exceptions;

import pt.up.hs.linguini.exceptions.LinguiniException;

/**
 * Exception thrown when a problem occurs during lemmatization.
 *
 * @author José Carlos Paiva <code>josepaiva94@gmail.com</code>
 */
public class LemmatizationException extends LinguiniException {

    public LemmatizationException() {
        this("Could not lemmatize words");
    }

    public LemmatizationException(String message) {
        super(message);
    }

    public LemmatizationException(String message, Throwable cause) {
        super(message, cause);
    }
}
