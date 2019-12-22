package pt.up.hs.linguini.jspell.exceptions;

import pt.up.hs.linguini.exceptions.LinguiniException;

/**
 * Exception thrown when a problem occurs with the JSpell Annotator.
 *
 * @author José Carlos Paiva <code>josepaiva94@gmail.com</code>
 */
public class JSpellAnnotatorException extends LinguiniException {

    public JSpellAnnotatorException() {
    }

    public JSpellAnnotatorException(String message) {
        super(message);
    }

    public JSpellAnnotatorException(String message, Throwable cause) {
        super(message, cause);
    }

    public JSpellAnnotatorException(Throwable cause) {
        super(cause);
    }
}
