package pt.up.hs.linguini.filters;

import pt.up.hs.linguini.models.Token;

import java.util.Collection;

/**
 * Token filter.
 *
 * @author José Carlos Paiva <code>josepaiva94@gmail.com</code>
 */
public interface TokenFilter {

    boolean accept(Token token);

    void apply(Collection<Token> collection);
}
