package pt.up.hs.linguini.analysis.ideadensity.rulesets.misc;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pt.up.hs.linguini.analysis.ideadensity.Engine;
import pt.up.hs.linguini.analysis.ideadensity.Relation;
import pt.up.hs.linguini.analysis.ideadensity.rulesets.Ruleset;
import pt.up.hs.linguini.analysis.ideadensity.rulesets.noun.NounPhraseRuleset;
import pt.up.hs.linguini.analysis.ideadensity.rulesets.verb.VerbPhraseRuleset;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A ruleset that processes the 'conj' relation.
 *
 * @author José Carlos Paiva <code>josepaiva94@gmail.com</code>
 */
public class ConjRuleset extends Ruleset<Object> {
    private static final Logger LOGGER =
            LoggerFactory.getLogger(ConjRuleset.class.getSimpleName());

    public ConjRuleset() {
        super("conj");
    }

    @Override
    public Object extract(
            List<Relation> relations, int index, int[] context,
            Engine engine, Map<String, Object> info) {

        if (info == null) {
            info = new HashMap<>();
        }

        if (info.containsKey("class")) {

            if (((String) info.get("class")).equalsIgnoreCase("NP")) {
                LOGGER.debug("ConjRuleset is processing node as NP");
                NounPhraseRuleset npr = new NounPhraseRuleset();

                // TODO return the first element in the list
                Map<String, Object> d = npr.extract(
                        relations, index, context, engine, new HashMap<>());
                if (!d.containsKey("ids_for_preconj") ||
                        ((int[]) d.get("ids_for_preconj")).length == 0) {
                    return d.get("return_list");
                }
            } else if (((String) info.get("class")).equalsIgnoreCase("VP")) {
                LOGGER.debug("ConjRuleset is processing node as VP");
                VerbPhraseRuleset vpr = new VerbPhraseRuleset();
                Map<String, Object> d = vpr.extract(
                        relations, index, context, engine, info);
                return d;
            } else  {
                LOGGER.warn(
                        String.format("Expected NP or VP class but found %s.",
                                info.get("class"))
                );
                return null;
            }
        }

        LOGGER.warn("Expected class in info dictionary but it was not there.");

        return null;
    }
}
