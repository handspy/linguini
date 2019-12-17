package pt.up.hs.linguini.normalizers;

import pt.up.hs.linguini.models.Token;

/**
 * Token transformer that converts all characters to lower-case.
 *
 * @author José Carlos Paiva <code>josepaiva94@gmail.com</code>
 */
public class LowercaseTokenNormalizer implements TokenNormalizer {

    @Override
    public void normalize(Token token) {
        token.setWord(token.getWord().toLowerCase());
    }

    @Override
    public void normalize(Token token, String tag) {
        normalize(token);
    }
}
