package pt.up.hs.linguini.exceptions;

/**
 * Exception thrown if a problem occurs with the configuration.
 *
 * @author José Carlos Paiva <code>josepaiva94@gmail.com</code>
 */
public class ConfigException extends LinguiniException {

    public ConfigException() {
        this("Could not load configuration");
    }

    public ConfigException(Throwable e) {
        super(e);
    }

    public ConfigException(String message) {
        super(message);
    }

    public ConfigException(String message, Throwable cause) {
        super(message, cause);
    }
}
