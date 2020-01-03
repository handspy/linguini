package pt.up.hs.linguini.analysis.ideadensity.rulesets.noun;

import pt.up.hs.linguini.analysis.ideadensity.Engine;
import pt.up.hs.linguini.analysis.ideadensity.Relation;
import pt.up.hs.linguini.analysis.ideadensity.rulesets.models.Subject;
import pt.up.hs.linguini.analysis.ideadensity.utils.Tuple;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A ruleset that processes the 'appos' relation.
 *
 * @author José Carlos Paiva <code>josepaiva94@gmail.com</code>
 */
public class ApposRuleset extends NounPhraseRuleset {

    public ApposRuleset() {
        super("appos");
    }

    @Override
    public Map<String, Object> extract(
            List<Relation> relations, int index, int[] context,
            Engine engine, Map<String, Object> info) {

        // appositional modifiers generate a special kind of predications

        if (info == null) {
            info = new HashMap<>();
        }

        Map<String, Object> extracted = super.extract(
                relations, index, context, engine, info);

        String[] returnList = (String[]) extracted.get("return_list");

        for (String subj: ((Subject) info.get("subj")).getReturnList()) {
            for (String noun: returnList) {
                engine.emit(new Tuple("(APPOS)", subj, noun), "P");
            }
        }

        return null;
    }
}
