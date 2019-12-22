package pt.up.hs.linguini.transformation;

import pt.up.hs.linguini.models.HasWord;
import pt.up.hs.linguini.pipeline.Step;
import pt.up.hs.linguini.models.Token;

/**
 * Token transformer.
 *
 * @author José Carlos Paiva <code>josepaiva94@gmail.com</code>
 */
public interface TokenTransformer<I extends HasWord, O extends HasWord> extends Step<I, O> {
}
