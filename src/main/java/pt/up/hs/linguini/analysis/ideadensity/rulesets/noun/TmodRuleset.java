package pt.up.hs.linguini.analysis.ideadensity.rulesets.noun;

import pt.up.hs.linguini.analysis.ideadensity.Engine;
import pt.up.hs.linguini.analysis.ideadensity.Relation;
import pt.up.hs.linguini.analysis.ideadensity.utils.Tuple;

import java.util.List;
import java.util.Map;

/**
 * A ruleset that processes the 'tmod' relation.
 *
 * @author José Carlos Paiva <code>josepaiva94@gmail.com</code>
 */
public class TmodRuleset extends NounPhraseRuleset {

    public TmodRuleset() {
        super("tmod");
    }

    @Override
    public Map<String, Object> extract(
            List<Relation> relations, int index, int[] context,
            Engine engine, Map<String, Object> info) {

        // a temporal modifier always generates a proposition

        Map<String, Object> ret = super.extract(
                relations, index, context, engine, info);

        engine.emit(new Tuple(
                ((String[]) ret.get("return_list"))[0]), "M");

        return null;
    }
}
