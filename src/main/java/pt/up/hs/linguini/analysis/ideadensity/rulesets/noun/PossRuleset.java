package pt.up.hs.linguini.analysis.ideadensity.rulesets.noun;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pt.up.hs.linguini.analysis.ideadensity.Engine;
import pt.up.hs.linguini.analysis.ideadensity.Relation;
import pt.up.hs.linguini.analysis.ideadensity.rulesets.Ruleset;
import pt.up.hs.linguini.analysis.ideadensity.utils.Tuple;
import pt.up.hs.linguini.utils.ArrayUtils;

import java.util.*;

/**
 * A ruleset that processes the 'poss' relation.
 *
 * @author José Carlos Paiva <code>josepaiva94@gmail.com</code>
 */
public class PossRuleset extends Ruleset<String> {

    private static final Logger LOGGER =
            LoggerFactory.getLogger(PossRuleset.class.getSimpleName());

    public PossRuleset() {
        super("poss");
    }

    @Override
    public String extract(
            List<Relation> relations, int index, int[] context,
            Engine engine, Map<String, Object> info) {

        if (info == null) {
            info = new HashMap<>();
        }

        if (relations.get(index).tag().equalsIgnoreCase("PRP$")) {
            return relations.get(index).word();
        }

        if (relations.get(index).tag().equalsIgnoreCase("NN|NNS|NNP")) {
            Map<String, Object> d = new NounPhraseRuleset()
                            .extract(relations, index, context, engine, info);
            if (d.containsKey("ids_for_preconj")) {

                String[] thisOut = (String[]) d.get("return_list");

                int[] possessiveIndices = Relation
                        .getChildrenWithDep("possessive", relations, index);
                if (possessiveIndices.length > 0) {
                    int possessiveIndex = possessiveIndices[0];

                    int[] newContext = ArrayUtils.add(context, index);

                    engine.analyze(relations, possessiveIndex, newContext,
                            new HashMap<>());

                    String referent = relations.get(context[context.length - 1])
                            .word();
                    for (String item: thisOut) {
                        engine.emit(new Tuple(referent, item), "M");
                    }
                }

                return null;
            }

            return null;
        }

        LOGGER.warn(String.format(
                "poss cannot handle %s yet", relations.get(index).tag()));

        return null;
    }
}
