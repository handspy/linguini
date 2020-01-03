package pt.up.hs.linguini.analysis.ideadensity.rulesets.misc;

import pt.up.hs.linguini.analysis.ideadensity.Engine;
import pt.up.hs.linguini.analysis.ideadensity.Relation;
import pt.up.hs.linguini.analysis.ideadensity.rulesets.Ruleset;
import pt.up.hs.linguini.analysis.ideadensity.utils.Tuple;

import java.util.List;
import java.util.Map;

/**
 * A ruleset that processes the 'det' relation. A determiner may or may not
 * emit a new proposition. Determiners like the, a, an, this, and these get
 * joined to the noun they precede; others, like some and any, generate their
 * own proposition.
 *
 * @author José Carlos Paiva <code>josepaiva94@gmail.com</code>
 */
public class DetRuleset extends Ruleset<String> {

    public DetRuleset() {
        super("det");
    }

    @Override
    public String extract(
            List<Relation> relations, int index, int[] context,
            Engine engine, Map<String, Object> info) {

        String nonEmittedDets = engine.getConfigValue("nonEmittedDets");
        if (relations.get(index).word().toLowerCase().matches(nonEmittedDets)) {
            return relations.get(index).word();
        } else {
            // TODO: maybe get the subject from info.
            engine.emit(
                    new Tuple(
                            relations.get(context[context.length - 1]).word(),
                            relations.get(index).word()
                    ),
                    "M"
            );
            return null;
        }
    }
}
