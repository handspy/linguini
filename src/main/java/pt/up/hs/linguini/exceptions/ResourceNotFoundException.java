package pt.up.hs.linguini.exceptions;

/**
 * Exception thrown by analyzers when a necessary resource is
 * not found.
 *
 * @author José Carlos Paiva <code>josepaiva94@gmail.com</code>
 */
public class ResourceNotFoundException extends LinguiniException {

    public ResourceNotFoundException() {
        super("Resource not found.");
    }

    public ResourceNotFoundException(String message) {
        super(message);
    }

    public ResourceNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
