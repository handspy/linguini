package pt.up.hs.linguini.resources.exceptions;

import pt.up.hs.linguini.exceptions.LinguiniException;

/**
 * Exception thrown when an error occurs while loading a resource.
 *
 * @author José Carlos Paiva <code>josepaiva94@gmail.com</code>
 */
public class ResourceLoadingException extends LinguiniException {

    public ResourceLoadingException() {
        this("Could not load resource");
    }

    public ResourceLoadingException(Throwable e) {
        this("Could not load resource", e);
    }

    public ResourceLoadingException(String message) {
        super(message);
    }

    public ResourceLoadingException(String message, Throwable cause) {
        super(message, cause);
    }
}
