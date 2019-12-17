package pt.up.hs.linguini.lemmatization.exceptions;

/**
 * Exception thrown when a problem occurs during lemmatization.
 *
 * @author José Carlos Paiva <code>josepaiva94@gmail.com</code>
 */
public class LemmatizationException extends Exception {

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
