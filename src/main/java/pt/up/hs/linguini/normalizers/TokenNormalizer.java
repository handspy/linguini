package pt.up.hs.linguini.normalizers;

import pt.up.hs.linguini.models.Token;
import pt.up.hs.linguini.exceptions.TransformerException;

/**
 * Token normalizer.
 *
 * @author José Carlos Paiva <code>josepaiva94@gmail.com</code>
 */
public interface TokenNormalizer {

    void normalize(Token token) throws TransformerException;

    void normalize(Token token, String tag)
            throws TransformerException;
}
