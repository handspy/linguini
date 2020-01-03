package pt.up.hs.linguini.analysis.ideadensity.rulesets.misc;

import pt.up.hs.linguini.analysis.ideadensity.Engine;
import pt.up.hs.linguini.analysis.ideadensity.Relation;
import pt.up.hs.linguini.analysis.ideadensity.rulesets.Ruleset;
import pt.up.hs.linguini.utils.ArrayUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A ruleset that processes the 'nn-join' relation.
 *
 * @author José Carlos Paiva <code>josepaiva94@gmail.com</code>
 */
public class NnJoinRuleset extends Ruleset<Object> {

    public NnJoinRuleset() {
        super("nn-join");
    }

    @Override
    public Object extract(
            List<Relation> relations, int index, int[] context,
            Engine engine, Map<String, Object> info) {

        int[] conjIndices = Relation
                .getChildrenWithDep("conj", relations, index);

        int[] newContext = ArrayUtils.add(context, index);

        if (conjIndices.length > 0) {
            // consume the conjunction
            int[] ccIndices = Relation
                    .getChildrenWithDep("cc", relations, index);
            for (int i: ccIndices) {
                engine.analyze(
                        relations, ccIndices[0], newContext, new HashMap<>());
            }

            String[] conjs = new String[0];
            for (int i: conjIndices) {
                Map<String, Object> newInfo = new HashMap<>();
                newInfo.put("class", "NP");
                Object obj = engine.analyze(relations, i, newContext, newInfo);
                if (obj instanceof String[]) {
                    // TODO is this OK?
                    conjs = ArrayUtils.add(conjs, ((String[]) obj)[0]);
                } else {
                    conjs = ArrayUtils.add(conjs, (String) obj);
                }
            }

            return ArrayUtils.concat(
                    new String[] { relations.get(index).word() }, conjs);
        }

        return relations.get(index).word();
    }
}
