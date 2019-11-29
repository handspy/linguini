package pt.up.hs.linguini.exceptions;

/**
 * General exception thrown by analyzers if analysis fails.
 *
 * @author José Carlos Paiva <code>josepaiva94@gmail.com</code>
 */
public class AnalyzerException extends Exception {

    public AnalyzerException() {
        super("An exception occurred during the analysis.");
    }

    public AnalyzerException(String message) {
        super(message);
    }

    public AnalyzerException(String message, Throwable cause) {
        super(message, cause);
    }
}
