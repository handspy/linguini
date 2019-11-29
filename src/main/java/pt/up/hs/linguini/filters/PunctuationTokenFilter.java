package pt.up.hs.linguini.filters;

import pt.up.hs.linguini.models.Token;
import pt.up.hs.linguini.utils.StringUtils;

/**
 * Token filter that accepts only non-punctuation characters.
 *
 * @author José Carlos Paiva <code>josepaiva94@gmail.com</code>
 */
public class PunctuationTokenFilter extends SimpleTokenFilter {

    @Override
    public boolean accept(Token token) {
        return !StringUtils.isPunctuation(token.getWord());
    }
}
