package pt.up.hs.linguini.exceptions;

/**
 * Exception thrown by analyzers when the called method is
 * not yet implemented.
 *
 * @author José Carlos Paiva <code>josepaiva94@gmail.com</code>
 */
public class MethodNotImplementedException extends LinguiniException {

    public MethodNotImplementedException() {
        super("Method not yet implemented.");
    }

    public MethodNotImplementedException(String message) {
        super(message);
    }

    public MethodNotImplementedException(String message, Throwable cause) {
        super(message, cause);
    }
}
