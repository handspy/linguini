package pt.up.hs.linguini.exceptions;

/**
 * General exception thrown by Linguini.
 *
 * @author José Carlos Paiva <code>josepaiva94@gmail.com</code>
 */
public class LinguiniException extends Exception {

    public LinguiniException() {
        super();
    }

    public LinguiniException(String message) {
        super(message);
    }

    public LinguiniException(String message, Throwable cause) {
        super(message, cause);
    }

    public LinguiniException(Throwable cause) {
        super(cause);
    }
}
