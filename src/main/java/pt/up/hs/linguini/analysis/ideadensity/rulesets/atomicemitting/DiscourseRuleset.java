package pt.up.hs.linguini.analysis.ideadensity.rulesets.atomicemitting;

import pt.up.hs.linguini.analysis.ideadensity.Engine;
import pt.up.hs.linguini.analysis.ideadensity.Relation;
import pt.up.hs.linguini.analysis.ideadensity.utils.Tuple;

import java.util.List;
import java.util.Map;

/**
 * A ruleset that processes the 'neg' relation.
 *
 * @author José Carlos Paiva <code>josepaiva94@gmail.com</code>
 */
public class DiscourseRuleset extends AtomicEmittingRuleset {

    public DiscourseRuleset() {
        super("discourse");
    }

    @Override
    public Void extract(
            List<Relation> relations, int index, int[] context,
            Engine engine, Map<String, Object> info) {

        engine.emit(
                new Tuple(relations.get(index).word()),
                "M"
        );

        return null;
    }
}
