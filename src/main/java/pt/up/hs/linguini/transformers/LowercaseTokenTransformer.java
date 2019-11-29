package pt.up.hs.linguini.transformers;

import pt.up.hs.linguini.models.Token;

/**
 * Token transformer that converts all characters to lower-case.
 *
 * @author José Carlos Paiva <code>josepaiva94@gmail.com</code>
 */
public class LowercaseTokenTransformer implements TokenTransformer {

    @Override
    public Token transform(Token token) {
        token.setWord(token.getWord().toLowerCase());
        return token;
    }
}
