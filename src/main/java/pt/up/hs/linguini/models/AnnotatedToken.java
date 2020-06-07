package pt.up.hs.linguini.models;

/**
 * Class for describing tokens annotated with additional
 * information.
 *
 * @author José Carlos Paiva <code>josepaiva94@gmail.com</code>
 */
public class AnnotatedToken<T> implements HasWord {

    private Token token;
    private T info;

    public AnnotatedToken() {
        this(null, null);
    }

    public AnnotatedToken(Token token) {
        this(token, null);
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

    @Override
    public String word() {
        if (token == null) {
            return null;
        }
        return token.word();
    }

    @Override
    public void word(String word) {
        token.word(word);
    }

    @Override
    public String original() {
        if (token == null) {
            return null;
        }
        return token.getOriginal();
    }
}
