package pt.up.hs.linguini.analysis.ideadensity.rulesets.misc;

import pt.up.hs.linguini.analysis.ideadensity.Engine;
import pt.up.hs.linguini.analysis.ideadensity.Relation;
import pt.up.hs.linguini.analysis.ideadensity.rulesets.Ruleset;
import pt.up.hs.linguini.utils.ArrayUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A ruleset that processes the 'nn' relation.
 *
 * @author José Carlos Paiva <code>josepaiva94@gmail.com</code>
 */
public class NnRuleset extends Ruleset<String[]> {

    public NnRuleset() {
        super("nn");
    }

    @Override
    public String[] extract(
            List<Relation> relations, int index, int[] context,
            Engine engine, Map<String, Object> info) {

        int[] ccIndices = Relation
                .getChildrenWithDep("cc", relations, index);

        int[] newContext = ArrayUtils.add(context, index);

        if (ccIndices.length > 0) {
            engine.analyze(
                    relations, ccIndices[0], newContext, new HashMap<>());

            int[] conjIndices = Relation
                    .getChildrenWithDep("conj", relations, index);

            Map<String, Object> newInfo = new HashMap<>();
            newInfo.put("class", "NP");

            String[] conjs = new String[0];
            for (int i: conjIndices) {
                String[] conjsTmp = engine.analyze(
                        relations, i, newContext, newInfo);
                conjs = ArrayUtils.add(conjs, conjsTmp[0]);
            }

            return ArrayUtils.concat(
                    new String[] { relations.get(index).word() }, conjs);
        }

        return new String[] { relations.get(index).word() };
    }
}
