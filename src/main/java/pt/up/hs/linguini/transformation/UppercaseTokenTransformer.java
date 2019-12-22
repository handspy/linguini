package pt.up.hs.linguini.transformation;

import pt.up.hs.linguini.models.HasWord;
import pt.up.hs.linguini.models.Token;

import java.util.Locale;

/**
 * Token transformer that converts all characters to upper-case.
 *
 * @author José Carlos Paiva <code>josepaiva94@gmail.com</code>
 */
public class UppercaseTokenTransformer<T extends HasWord>
        implements TokenTransformer<T, T> {

    private Locale locale;

    public UppercaseTokenTransformer() {
        this(Locale.getDefault());
    }

    public UppercaseTokenTransformer(Locale locale) {
        this.locale = locale;
    }

    @Override
    public T execute(T token) {
        token.word(token.word().toUpperCase(locale));
        return token;
    }
}
