package pt.up.hs.linguini.analysis.ideadensity.rulesets.atomic;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pt.up.hs.linguini.analysis.ideadensity.Engine;
import pt.up.hs.linguini.analysis.ideadensity.Relation;

import java.util.List;
import java.util.Map;

/**
 * A ruleset that processes the 'cop' relation.
 *
 * @author José Carlos Paiva <code>josepaiva94@gmail.com</code>
 */
public class ComplmRuleset extends AtomicRuleset {
    private static final Logger LOGGER = LoggerFactory.getLogger(
            ComplmRuleset.class.getSimpleName());

    public ComplmRuleset() {
        super("complm");
    }

    @Override
    public String extract(
            List<Relation> relations, int index, int[] context,
            Engine engine, Map<String, Object> info) {
        LOGGER.warn(
                "The \"complm\" relation is not present in the Universal" +
                " Dependencies, and is thus deprecated. It was replaced by " +
                "\"mark\".");
        return super.extract(relations, index, context, engine, info);
    }
}
