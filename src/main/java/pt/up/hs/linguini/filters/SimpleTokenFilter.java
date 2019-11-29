package pt.up.hs.linguini.filters;

import pt.up.hs.linguini.models.Token;

import java.util.Collection;

/**
 * Token filter that accepts everything.
 *
 * @author José Carlos Paiva <code>josepaiva94@gmail.com</code>
 */
public class SimpleTokenFilter implements TokenFilter {

    @Override
    public boolean accept(Token token) {
        return true;
    }

    @Override
    public void apply(Collection<Token> collection) {
        collection.removeIf(token -> !accept(token));
    }
}
