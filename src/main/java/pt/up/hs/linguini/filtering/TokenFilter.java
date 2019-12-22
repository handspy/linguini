package pt.up.hs.linguini.filtering;

import pt.up.hs.linguini.models.HasWord;
import pt.up.hs.linguini.pipeline.Step;
import pt.up.hs.linguini.models.Token;

import java.util.List;

/**
 * Token filter.
 *
 * @author José Carlos Paiva <code>josepaiva94@gmail.com</code>
 */
public interface TokenFilter<T extends HasWord>
        extends Step<List<T>, List<T>> {
}
