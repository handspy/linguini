package pt.up.hs.linguini.analysis.ideadensity.transformations;

import pt.up.hs.linguini.analysis.ideadensity.Relation;

import java.util.List;

/**
 * Transforms a given dependency tree to facilitate its processing.
 *
 * @author José Carlos Paiva <code>josepaiva94@gmail.com</code>
 */
public interface Transformation {

    /**
     * Apply the corresponding transformation in place.
     *
     * @param relations {@link List} the list of relations in a sentence
     */
    void transform(List<Relation> relations);
}
