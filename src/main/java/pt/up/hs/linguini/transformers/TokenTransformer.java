package pt.up.hs.linguini.transformers;

import pt.up.hs.linguini.models.Token;
import pt.up.hs.linguini.exceptions.TransformerException;

/**
 * Token transformer.
 *
 * @author José Carlos Paiva <code>josepaiva94@gmail.com</code>
 */
public interface TokenTransformer {

    Token transform(Token token) throws TransformerException;
}
