package pt.up.hs.linguini.analysis.ideadensity.rulesets.atomicemitting;

import pt.up.hs.linguini.analysis.ideadensity.Engine;
import pt.up.hs.linguini.analysis.ideadensity.Relation;
import pt.up.hs.linguini.analysis.ideadensity.rulesets.Ruleset;
import pt.up.hs.linguini.analysis.ideadensity.utils.Tuple;

import java.util.List;
import java.util.Map;

/**
 * A base ruleset for atomic relations that just emits the associated word as
 * a proposition.
 *
 * @author José Carlos Paiva <code>josepaiva94@gmail.com</code>
 */
public class AtomicEmittingRuleset extends Ruleset<Void> {

    public AtomicEmittingRuleset(String rel) {
        super(rel);
    }

    @Override
    public Void extract(
            List<Relation> relations, int index, int[] context,
            Engine engine, Map<String, Object> info) {

        engine.emit(new Tuple(relations.get(index).word()));

        return null;
    }
}
