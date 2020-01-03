package pt.up.hs.linguini.analysis.ideadensity.rulesets.noun;

import pt.up.hs.linguini.analysis.ideadensity.Engine;
import pt.up.hs.linguini.analysis.ideadensity.Relation;
import pt.up.hs.linguini.analysis.ideadensity.rulesets.Ruleset;
import pt.up.hs.linguini.utils.ArrayUtils;

import java.util.*;

/**
 * A ruleset that processes the 'nmod' relation.
 *
 * @author José Carlos Paiva <code>josepaiva94@gmail.com</code>
 */
public class NmodRuleset extends Ruleset<String> {

    public NmodRuleset() {
        super("nmod");
    }

    @Override
    public String extract(
            List<Relation> relations, int index, int[] context,
            Engine engine, Map<String, Object> info) {

        // ruleset returns the NP modifier, without emitting any proposition

        int[] detIndices =
                Relation.getChildrenWithDep("det", relations, index);
        int[] possIndices =
                Relation.getChildrenWithDep("poss", relations, index);
        int[] nnIndices =
                Relation.getChildrenWithDep("nn", relations, index);
        int[] prepIndices =
                Relation.getChildrenWithDep("prep", relations, index);
        int[] amodIndices =
                Relation.getChildrenWithDep("amod", relations, index);
        int[] numIndices =
                Relation.getChildrenWithDep("num", relations, index);

        int[] wordIndices = ArrayUtils.add(
                ArrayUtils.concat(
                        detIndices,
                        possIndices,
                        nnIndices,
                        prepIndices,
                        amodIndices,
                        numIndices
                ), index);

        Arrays.sort(wordIndices);

        int[] newContext = ArrayUtils.add(context, index);

        String[] words = new String[0];
        for (int i: wordIndices) {
            Object wordsTmp;
            if (i == index) {
                wordsTmp = relations.get(index).word();
            } else {
                wordsTmp = engine.analyze(
                        relations, i, newContext, new HashMap<>());
            }

            if (wordsTmp instanceof String[]) {
                words = ArrayUtils.concat(words, (String[]) wordsTmp);
            } else {
                words = ArrayUtils.add(words, (String) wordsTmp);
            }
        }

        return String.join(" ", words);
    }
}
