package pt.up.hs.linguini.analysis.ideadensity.rulesets.atomic;

import pt.up.hs.linguini.analysis.ideadensity.Engine;
import pt.up.hs.linguini.analysis.ideadensity.Relation;
import pt.up.hs.linguini.analysis.ideadensity.rulesets.Ruleset;

import java.util.List;
import java.util.Map;

/**
 * A base ruleset for atomic relations that just returns the associated word.
 *
 * @author José Carlos Paiva <code>josepaiva94@gmail.com</code>
 */
public class AtomicRuleset extends Ruleset<String> {

    public AtomicRuleset(String rel) {
        super(rel);
    }

    @Override
    public String extract(
            List<Relation> relations, int index, int[] context,
            Engine engine, Map<String, Object> info) {
        return relations.get(index).word();
    }
}
