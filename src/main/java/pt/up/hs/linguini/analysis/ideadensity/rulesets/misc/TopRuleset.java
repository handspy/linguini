package pt.up.hs.linguini.analysis.ideadensity.rulesets.misc;

import pt.up.hs.linguini.analysis.ideadensity.Engine;
import pt.up.hs.linguini.analysis.ideadensity.Relation;
import pt.up.hs.linguini.analysis.ideadensity.rulesets.Ruleset;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A dummy ruleset that starts the analysis process.
 *
 * @author José Carlos Paiva <code>josepaiva94@gmail.com</code>
 */
public class TopRuleset extends Ruleset<Object> {

    public TopRuleset() {
        super("TOP");
    }

    @Override
    public Object extract(
            List<Relation> relations, int index, int[] context,
            Engine engine, Map<String, Object> info) {

        int[] deps = relations.get(index).deps();

        if (deps != null && deps.length > 0) {
            return engine.analyze(
                    relations,
                    deps[0],
                    new int[] { index },
                    new HashMap<>()
            );
        } else {
            return null;
        }
    }
}
