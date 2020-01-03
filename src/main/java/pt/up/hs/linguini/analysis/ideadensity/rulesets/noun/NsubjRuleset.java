package pt.up.hs.linguini.analysis.ideadensity.rulesets.noun;

import pt.up.hs.linguini.analysis.ideadensity.Engine;
import pt.up.hs.linguini.analysis.ideadensity.Relation;

import java.util.List;
import java.util.Map;

/**
 * A ruleset that processes the 'nsubj' relation.
 *
 * @author José Carlos Paiva <code>josepaiva94@gmail.com</code>
 */
public class NsubjRuleset extends NounPhraseRuleset {

    public NsubjRuleset() {
        super("nsubj");
    }

    @Override
    public Map<String, Object> extract(
            List<Relation> relations, int index, int[] context,
            Engine engine, Map<String, Object> info) {

        return super.extract(relations, index, context, engine, info);
    }
}
