package pt.up.hs.linguini.lemmatization;

import java.util.Objects;

/**
 * Cache key for lemmas.
 *
 * @author José Carlos Paiva <code>josepaiva94@gmail.com</code>
 */
public class LemmaCacheKey {

    private String token;
    private String tag;

    public LemmaCacheKey(String token, String tag) {
        this.token = token;
        this.tag = tag;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof LemmaCacheKey)) return false;
        LemmaCacheKey that = (LemmaCacheKey) o;
        return token.equals(that.token) &&
                tag.equals(that.tag);
    }

    @Override
    public int hashCode() {
        return Objects.hash(token, tag);
    }
}
