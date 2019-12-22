package pt.up.hs.linguini.normalization.exceptions;

import pt.up.hs.linguini.exceptions.LinguiniException;

/**
 * Exception thrown when a problem occurs while reading a
 * replacements' file.
 *
 * @author José Carlos Paiva <code>josepaiva94@gmail.com</code>
 */
public class NormalizationException extends LinguiniException {

    public NormalizationException() {
        this("Could not normalize tokens");
    }

    public NormalizationException(Throwable e) {
        this("Could not normalize tokens", e);
    }

    public NormalizationException(String message) {
        super(message);
    }

    public NormalizationException(String message, Throwable cause) {
        super(message, cause);
    }
}
