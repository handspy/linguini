package pt.up.hs.linguini.analysis.exceptions;

import pt.up.hs.linguini.exceptions.LinguiniException;

/**
 * General exception thrown by analyzers if analysis fails.
 *
 * @author José Carlos Paiva <code>josepaiva94@gmail.com</code>
 */
public class AnalysisException extends LinguiniException {

    public AnalysisException() {
        super("An exception occurred during the analysis.");
    }

    public AnalysisException(String message) {
        super(message);
    }

    public AnalysisException(String message, Throwable cause) {
        super(message, cause);
    }
}
