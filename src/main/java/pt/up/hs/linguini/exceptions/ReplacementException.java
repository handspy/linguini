package pt.up.hs.linguini.exceptions;

/**
 * Exception thrown when a problem occurs while reading a
 * replacements' file.
 *
 * @author José Carlos Paiva <code>josepaiva94@gmail.com</code>
 */
public class ReplacementException extends Exception {

    public ReplacementException() {
        this("Could not read replacements' file");
    }

    public ReplacementException(Throwable e) {
        this("Could not read replacements' file", e);
    }

    public ReplacementException(String message) {
        super(message);
    }

    public ReplacementException(String message, Throwable cause) {
        super(message, cause);
    }
}
