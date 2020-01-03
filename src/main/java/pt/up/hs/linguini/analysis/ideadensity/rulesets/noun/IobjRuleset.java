package pt.up.hs.linguini.analysis.ideadensity.rulesets.noun;

import pt.up.hs.linguini.analysis.ideadensity.Engine;
import pt.up.hs.linguini.analysis.ideadensity.Relation;
import pt.up.hs.linguini.analysis.ideadensity.utils.Tuple;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A ruleset that processes the 'iobj' relation.
 *
 * @author José Carlos Paiva <code>josepaiva94@gmail.com</code>
 */
public class IobjRuleset extends NounPhraseRuleset {

    public IobjRuleset() {
        super("iobj");
    }

    @Override
    public Map<String, Object> extract(
            List<Relation> relations, int index, int[] context,
            Engine engine, Map<String, Object> info) {

        if (info == null) {
            info = new HashMap<>();
        }

        Map<String, Object> d = super.extract(
                relations, index, context, engine, info);
        if (!d.containsKey("ids_for_preconj") ||
                ((int[]) d.get("ids_for_preconj")).length == 0) {
            for (String value: (String[]) d.get("return_list")) {
                engine.emit(
                        new Tuple("(to) " + value),
                        "M"
                );
            }
        }

        return null;
    }
}
