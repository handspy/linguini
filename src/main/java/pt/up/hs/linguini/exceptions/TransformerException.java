package pt.up.hs.linguini.exceptions;

/**
 * General exception thrown by when a transformation fails.
 *
 * @author José Carlos Paiva <code>josepaiva94@gmail.com</code>
 */
public class TransformerException extends Exception {

    public TransformerException() {
        super("An exception occurred during a transformation.");
    }

    public TransformerException(String message) {
        super(message);
    }

    public TransformerException(String message, Throwable cause) {
        super(message, cause);
    }
}
