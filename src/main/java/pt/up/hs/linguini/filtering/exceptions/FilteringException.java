package pt.up.hs.linguini.filtering.exceptions;

import pt.up.hs.linguini.exceptions.LinguiniException;

/**
 * Exception thrown when a problem occurs while reading a
 * replacements' file.
 *
 * @author José Carlos Paiva <code>josepaiva94@gmail.com</code>
 */
public class FilteringException extends LinguiniException {

    public FilteringException() {
        this("Could not filter tokens");
    }

    public FilteringException(Throwable e) {
        this("Could not filter tokens", e);
    }

    public FilteringException(String message) {
        super(message);
    }

    public FilteringException(String message, Throwable cause) {
        super(message, cause);
    }
}
