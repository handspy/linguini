package pt.up.hs.linguini.models;

/**
 * Class for describing tokens annotated with additional
 * information.
 *
 * @author José Carlos Paiva <code>josepaiva94@gmail.com</code>
 */
public class AnnotatedToken<T> {

    private Token token;
    private T info;

    public AnnotatedToken() {
    }

    public AnnotatedToken(Token token, T info) {
        this.token = token;
        this.info = info;
    }

    public Token getToken() {
        return token;
    }

    public void setToken(Token token) {
        this.token = token;
    }

    public T getInfo() {
        return info;
    }

    public void setInfo(T info) {
        this.info = info;
    }
}
