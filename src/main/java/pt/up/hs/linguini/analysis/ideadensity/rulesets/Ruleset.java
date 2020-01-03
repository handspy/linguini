package pt.up.hs.linguini.analysis.ideadensity.rulesets;

import pt.up.hs.linguini.analysis.ideadensity.Engine;
import pt.up.hs.linguini.analysis.ideadensity.Relation;

import java.util.List;
import java.util.Map;

/**
 * A ruleset is responsible for processing relations of a certain label.
 *
 * @author José Carlos Paiva <code>josepaiva94@gmail.com</code>
 */
public abstract class Ruleset<T> {

    protected String rel;

    public Ruleset(String rel) {
        this.rel = rel;
    }

    /**
     * Check whether this ruleset applies to a particular relation.
     *
     * @param rel {@link Relation} the relation label
     * @return {@code boolean} true if this ruleset applies to the relation;
     * false otherwise.
     */
    public boolean applies(String rel) {
        return this.rel.equalsIgnoreCase(rel);
    }

    /**
     * Extract the corresponding propositions from a relation.
     *
     * @param relations {@link List} the list of relations in a sentence
     * @param index     {@code int} the index of the relation to be processed
     * @param context   {@link int[]} a list of indices representing the path
     *                               from the TOP node to the current one
     * @param engine    {@link Engine} the engine that is running the analysis
     *                  process
     * @param info      {@link Map} a dictionary containing already parsed
     *                  contextual information
     * @return {@link String} representation that can be embedded in other
     * propositions.
     */
    public abstract T extract(
            List<Relation> relations, int index, int[] context,
            Engine engine, Map<String, Object> info);
}
