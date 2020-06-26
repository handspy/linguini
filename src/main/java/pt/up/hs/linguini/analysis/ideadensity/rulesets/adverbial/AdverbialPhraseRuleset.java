package pt.up.hs.linguini.analysis.ideadensity.rulesets.adverbial;

import pt.up.hs.linguini.analysis.ideadensity.Engine;
import pt.up.hs.linguini.analysis.ideadensity.Relation;
import pt.up.hs.linguini.analysis.ideadensity.rulesets.Ruleset;
import pt.up.hs.linguini.analysis.ideadensity.utils.Tuple;
import pt.up.hs.linguini.utils.ArrayUtils;

import java.util.*;

/**
 * A base class for AdvP-like dependency substructures.
 *
 * @author José Carlos Paiva <code>josepaiva94@gmail.com</code>
 */
public class AdverbialPhraseRuleset extends Ruleset<String> {

    public AdverbialPhraseRuleset(String rel) {
        super(rel);
    }

    @Override
    public String extract(
            List<Relation> relations, int index, int[] context,
            Engine engine, Map<String, Object> info) {

        if (info.containsKey("num")) {
            // Advmod modifying number. Treat as quantmod.
            engine.emit(
                    new Tuple(info.get("num"), relations.get(index).word()),
                    "M"
            );
            return relations.get(index).word();
        }

        AdverbialPhraseRuleset
                .processNpAdvMods(relations, index, context, engine, info);

        AdverbialPhraseRuleset
                .processAdvMods(relations, index, context, engine, info);

        AdverbialPhraseRuleset
                .processPreps(relations, index, context, engine, info);

        if (!info.containsKey("no_emit")) {
            engine.emit(new Tuple(relations.get(index).word()), "M");
        }

        return relations.get(index).word();
    }

    /**
     * Process adverbial modifiers (e.g., very slowly).
     *
     * @param relations  {@link List} the list of relations in a sentence
     * @param index {@code int} the index of the relation to be processed
     * @param context {@link List} a list of indices representing the path from
     *                             the TOP node to the current one
     * @param engine {@link Engine} the engine that is running the analysis
     * @param info {@link Map} a dictionary containing already parsed
     *                         contextual information
     */
    private static void processAdvMods(
            List<Relation> relations, int index, int[] context,
            Engine engine, Map<String, Object> info) {

        int[] advmodIndices = Relation.getChildrenWithDep(
                "advmod", relations, index);

        int[] newContext = ArrayUtils.add(context, index);

        Map<String, Object> newInfo = new HashMap<>();
        newInfo.put("no_emit", true);

        for (int i: advmodIndices) {
            String advmod = engine.analyze(relations, i, newContext, newInfo);
            engine.emit(new Tuple(relations.get(index).word(), advmod), "M");
        }
    }

    /**
     * Process noun phrases as adverbial modifiers.
     *
     * @param relations  {@link List} the list of relations in a sentence
     * @param index {@code int} the index of the relation to be processed
     * @param context {@link List} a list of indices representing the path from
     *                             the TOP node to the current one
     * @param engine {@link Engine} the engine that is running the analysis
     * @param info {@link Map} a dictionary containing already parsed
     *                         contextual information
     */
    private static void processNpAdvMods(
            List<Relation> relations, int index, int[] context,
            Engine engine, Map<String, Object> info) {

        int[] nmodIndices = Relation.getChildrenWithDep(
                "nmod", relations, index);

        if (nmodIndices.length > 0) {

            int[] newContext = ArrayUtils.add(context, index);

            String nmod = engine.analyze(
                    relations, nmodIndices[0], newContext, new HashMap<>());

            engine.emit(new Tuple(relations.get(index).word(), nmod), "M");
        }
    }

    /**
     * Process adpositional modifiers.
     *
     * @param relations {@link String[]} the list of relations in a sentence
     * @param index {@code int} the index of the relation to be processed
     * @param context {@link int[]} a list of indices representing the path
     *                              from the TOP node to the current one
     * @param engine {@link Engine} the engine that is running the analysis
     * @param info {@link Map} a dictionary containing already parsed
     *                         contextual information
     */
    private static void processPreps(
            List<Relation> relations, int index, int[] context,
            Engine engine, Map<String, Object> info) {

        int[] adpmodIndices = Relation.getChildrenWithDep(
                "prep", relations, index);

        int[] newContext = ArrayUtils.add(context, index);

        for (int i: adpmodIndices) {
            engine.analyze(relations, i, newContext, new HashMap<>());
        }
    }
}
