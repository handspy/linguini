package pt.up.hs.linguini.normalization.selection.exceptions;

/**
 * General exception thrown if selection fails.
 *
 * @author José Carlos Paiva <code>josepaiva94@gmail.com</code>
 */
public class SelectionException extends Exception {

    public SelectionException() {
        super("An exception occurred during the selection.");
    }

    public SelectionException(String message) {
        super(message);
    }

    public SelectionException(String message, Throwable cause) {
        super(message, cause);
    }
}
