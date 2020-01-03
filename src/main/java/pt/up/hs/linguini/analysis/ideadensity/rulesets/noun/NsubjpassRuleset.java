package pt.up.hs.linguini.analysis.ideadensity.rulesets.noun;

import pt.up.hs.linguini.analysis.ideadensity.Engine;
import pt.up.hs.linguini.analysis.ideadensity.Relation;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A ruleset that processes the 'nsubj' relation.
 *
 * @author José Carlos Paiva <code>josepaiva94@gmail.com</code>
 */
public class NsubjpassRuleset extends NounPhraseRuleset {

    public NsubjpassRuleset() {
        super("nsubj:pass");
    }

    @Override
    public Map<String, Object> extract(
            List<Relation> relations, int index, int[] context,
            Engine engine, Map<String, Object> info) {

        if (info != null) {
            info = new HashMap<>();
        }

        return super.extract(
                relations, index, context, engine, info);
    }
}
