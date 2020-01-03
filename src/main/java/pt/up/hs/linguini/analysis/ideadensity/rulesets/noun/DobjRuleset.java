package pt.up.hs.linguini.analysis.ideadensity.rulesets.noun;

import pt.up.hs.linguini.analysis.ideadensity.Engine;
import pt.up.hs.linguini.analysis.ideadensity.Relation;
import pt.up.hs.linguini.analysis.ideadensity.rulesets.Ruleset;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A ruleset that processes the 'dobj' relation.
 *
 * @author José Carlos Paiva <code>josepaiva94@gmail.com</code>
 */
public class DobjRuleset extends Ruleset<String[]> {

    public DobjRuleset() {
        super("dobj");
    }

    @Override
    public String[] extract(
            List<Relation> relations, int index, int[] context,
            Engine engine, Map<String, Object> info) {

        if (info == null) {
            info = new HashMap<>();
        }

        Map<String, Object> d = new NounPhraseRuleset().extract(
                relations, index, context, engine, info);
        if (!d.containsKey("ids_for_preconj") ||
                ((int[]) d.get("ids_for_preconj")).length == 0) {
            return (String[]) d.get("return_list");
        }

        return null;
    }
}
