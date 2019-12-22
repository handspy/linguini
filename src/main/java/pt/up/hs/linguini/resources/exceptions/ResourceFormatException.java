package pt.up.hs.linguini.resources.exceptions;

/**
 * Exception thrown when an error occurs while loading a resource
 * due to its format.
 *
 * @author José Carlos Paiva <code>josepaiva94@gmail.com</code>
 */
public class ResourceFormatException extends ResourceLoadingException {

    public ResourceFormatException() {
        super("Invalid format for resource");
    }

    public ResourceFormatException(int line) {
        super("Invalid format for resource at line " + line);
    }
}
