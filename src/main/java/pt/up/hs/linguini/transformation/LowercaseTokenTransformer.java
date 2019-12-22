package pt.up.hs.linguini.transformation;

import pt.up.hs.linguini.models.HasWord;
import pt.up.hs.linguini.models.Token;

import java.util.Locale;

/**
 * Token transformer that converts all characters to upper-case.
 *
 * @author José Carlos Paiva <code>josepaiva94@gmail.com</code>
 */
public class LowercaseTokenTransformer<T extends HasWord>
        implements TokenTransformer<T, T> {

    private Locale locale;

    public LowercaseTokenTransformer() {
        this(Locale.getDefault());
    }

    public LowercaseTokenTransformer(Locale locale) {
        this.locale = locale;
    }

    @Override
    public T execute(T token) {
        token.word(token.word().toLowerCase(locale));
        return token;
    }
}
