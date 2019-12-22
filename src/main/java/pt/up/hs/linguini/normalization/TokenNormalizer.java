package pt.up.hs.linguini.normalization;

import pt.up.hs.linguini.pipeline.Step;
import pt.up.hs.linguini.models.AnnotatedToken;

/**
 * Token normalizer.
 *
 * @author José Carlos Paiva <code>josepaiva94@gmail.com</code>
 */
public interface TokenNormalizer
        extends Step<AnnotatedToken<String>, AnnotatedToken<String>> {
}
