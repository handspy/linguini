package pt.up.hs.linguini.analysis.ideadensity.rulesets.misc;

import pt.up.hs.linguini.analysis.ideadensity.Engine;
import pt.up.hs.linguini.analysis.ideadensity.Relation;
import pt.up.hs.linguini.analysis.ideadensity.rulesets.Ruleset;
import pt.up.hs.linguini.utils.ArrayUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A ruleset that processes the 'num' relation. Numerical modifiers are
 * treated in the same way as adjectives. This ruleset assembles and returns
 * the number, and it's up to the calling NounPhraseRuleset to emit the
 * propositions. This ruleset also emits propositions for quantifier phrase
 * modifiers.
 *
 * @author José Carlos Paiva <code>josepaiva94@gmail.com</code>
 */
public class NumRuleset extends Ruleset<String> {

    public NumRuleset() {
        super("num");
    }

    @Override
    public String extract(
            List<Relation> relations, int index, int[] context,
            Engine engine, Map<String, Object> info) {

        int[] numberIndices = Relation
                .getChildrenWithDep("number", relations, index);
        int[] ccIndices = Relation
                .getChildrenWithDep("cc", relations, index);
        int[] conjIndices = Relation
                .getChildrenWithDep("conj", relations, index);

        int[] indices = ArrayUtils.concat(
                new int[] { index },
                numberIndices,
                ccIndices,
                conjIndices
        );

        int[] newContext = ArrayUtils.add(context, index);

        String[] words = new String[0];
        for (int i: indices) {

            if (i != index) {
                Map<String, Object> newInfo = new HashMap<>();
                newInfo.put("class", "NP");
                Object word = engine.analyze(
                        relations, i, newContext, newInfo);
                if (word instanceof String[]) {
                    words = ArrayUtils.concat(words, (String[]) word);
                } else if (word instanceof String) {
                    words = ArrayUtils.add(words, (String) word);
                }
            } else {
                words = ArrayUtils
                        .add(words, (String) relations.get(index).word());
            }
        }

        String num = String.join(" ", words);

        // process quantmods
        int[] quantmodIndices = Relation
                .getChildrenWithDep("quantmod", relations, index);

        for (int i: quantmodIndices) {
            Map<String, Object> newInfo = new HashMap<>();
            newInfo.put("num", num);
            engine.analyze(relations, i, newContext, newInfo);
        }

        return num;
    }
}
