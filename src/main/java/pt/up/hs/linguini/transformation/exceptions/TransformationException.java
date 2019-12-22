package pt.up.hs.linguini.transformation.exceptions;

import pt.up.hs.linguini.exceptions.LinguiniException;

/**
 * Exception thrown when a problem occurs while transforming
 * a token.
 *
 * @author José Carlos Paiva <code>josepaiva94@gmail.com</code>
 */
public class TransformationException extends LinguiniException {

    public TransformationException() {
        this("Could not transform token");
    }

    public TransformationException(Throwable e) {
        this("Could not transform token", e);
    }

    public TransformationException(String message) {
        super(message);
    }

    public TransformationException(String message, Throwable cause) {
        super(message, cause);
    }
}
