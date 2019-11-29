package pt.up.hs.linguini.processing.exceptions;

/**
 * [Description here]
 *
 * @author José Carlos Paiva <code>josepaiva94@gmail.com</code>
 */
public class ProcessorException extends Exception {

    public ProcessorException() {
        super("An exception occurred during processing.");
    }

    public ProcessorException(String message) {
        super(message);
    }

    public ProcessorException(String message, Throwable cause) {
        super(message, cause);
    }
}
