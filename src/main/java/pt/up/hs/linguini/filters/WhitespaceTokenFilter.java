package pt.up.hs.linguini.filters;

import pt.up.hs.linguini.models.Token;
import pt.up.hs.linguini.utils.StringUtils;

/**
 * Token filter that accepts only non-whitespace characters.
 *
 * @author José Carlos Paiva <code>josepaiva94@gmail.com</code>
 */
public class WhitespaceTokenFilter extends SimpleTokenFilter {

    @Override
    public boolean accept(Token token) {
        return !StringUtils.isBlankString(token.getWord());
    }
}
