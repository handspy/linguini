package pt.up.hs.linguini.tokenization.exceptions;

import pt.up.hs.linguini.exceptions.LinguiniException;

/**
 * Exception thrown when a problem occurs while tokenizing text.
 *
 * @author José Carlos Paiva <code>josepaiva94@gmail.com</code>
 */
public class TokenizationException extends LinguiniException {

    public TokenizationException() {
        this("Could not tokenize text");
    }

    public TokenizationException(Throwable e) {
        this("Could not tokenize text", e);
    }

    public TokenizationException(String message) {
        super(message);
    }

    public TokenizationException(String message, Throwable cause) {
        super(message, cause);
    }
}
