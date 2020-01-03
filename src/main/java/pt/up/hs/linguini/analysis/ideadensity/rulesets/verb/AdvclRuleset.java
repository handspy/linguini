package pt.up.hs.linguini.analysis.ideadensity.rulesets.verb;

import pt.up.hs.linguini.analysis.ideadensity.Engine;
import pt.up.hs.linguini.analysis.ideadensity.Relation;
import pt.up.hs.linguini.utils.ArrayUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A ruleset that processes the 'advcl' relation.
 *
 * @author José Carlos Paiva <code>josepaiva94@gmail.com</code>
 */
public class AdvclRuleset extends VerbPhraseRuleset {

    public AdvclRuleset() {
        super("advcl");
    }

    @Override
    public Map<String, Object> extract(
            List<Relation> relations, int index, int[] context,
            Engine engine, Map<String, Object> info) {

        Map<String, Object> ret = super.extract(
                relations, index, context, engine, info);

        int[] markIndices = Relation
                .getChildrenWithDep("mark", relations, index);

        int[] newContext = ArrayUtils.add(context, index);

        String marker = null;
        if (markIndices != null && markIndices.length > 0) {
            marker = engine.analyze(
                    relations, markIndices[0], newContext, new HashMap<>());
        }

        ret.put("marker", marker);

        return ret;
    }
}
