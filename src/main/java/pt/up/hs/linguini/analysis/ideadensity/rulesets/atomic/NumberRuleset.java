package pt.up.hs.linguini.analysis.ideadensity.rulesets.atomic;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pt.up.hs.linguini.analysis.ideadensity.Engine;
import pt.up.hs.linguini.analysis.ideadensity.Relation;

import java.util.List;
import java.util.Map;

/**
 * A ruleset that processes the 'number' relation.
 *
 * @author José Carlos Paiva <code>josepaiva94@gmail.com</code>
 */
public class NumberRuleset extends AtomicRuleset {
    private static final Logger LOGGER = LoggerFactory.getLogger(
            NumberRuleset.class.getSimpleName());

    public NumberRuleset() {
        super("number");
    }

    @Override
    public String extract(
            List<Relation> relations, int index, int[] context,
            Engine engine, Map<String, Object> info) {
        LOGGER.warn(
                "The \"number\" relation is not present in the Universal" +
                " Dependencies, and is thus deprecated. It was replaced by " +
                "\"num\".");
        return super.extract(relations, index, context, engine, info);
    }
}
