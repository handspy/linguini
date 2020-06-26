package pt.up.hs.linguini.analysis.ideadensity.rulesets.adjectival;

import pt.up.hs.linguini.analysis.ideadensity.Engine;
import pt.up.hs.linguini.analysis.ideadensity.Relation;
import pt.up.hs.linguini.analysis.ideadensity.rulesets.Ruleset;
import pt.up.hs.linguini.analysis.ideadensity.rulesets.models.Subject;
import pt.up.hs.linguini.analysis.ideadensity.utils.Tuple;
import pt.up.hs.linguini.utils.ArrayUtils;

import java.util.*;

/**
 * A base class for AdjP-like dependency substructures.
 *
 * @author José Carlos Paiva <code>josepaiva94@gmail.com</code>
 */
public class AdjectivalPhraseRuleset extends Ruleset<String[]> {

    public AdjectivalPhraseRuleset() {
        super("adj");
    }

    public AdjectivalPhraseRuleset(String rel) {
        super(rel);
    }

    @Override
    public String[] extract(
            List<Relation> relations, int index, int[] context,
            Engine engine, Map<String, Object> info) {

        if (info == null) {
            info = new HashMap<>();
        }

        String[] advmods = AdjectivalPhraseRuleset
                .processAdvMods(relations, index, context, engine, info);

        String[] nmods = AdjectivalPhraseRuleset
                .processNMods(relations, index, context, engine, info);

        AdjectivalPhraseRuleset
                .processXComp(relations, index, context, engine, info);

        AdjectivalPhraseRuleset
                .processPreps(relations, index, context, engine, info);

        // TODO handle cc/conj
        String[] words = new String[] { relations.get(index).word() };

        String[] allmods = ArrayUtils.concat(advmods, nmods);
        for (String advmod: allmods) {
            for (String word: words) {
                engine.emit(new Tuple(word, advmod), "M");
            }
        }

        return words;
    }

    /**
     * Process adverbial modifiers (e.g., very difficult).
     *
     * @param relations {@link String[]} the list of relations in a sentence
     * @param index {@code int} the index of the relation to be processed
     * @param context {@link int[]} a list of indices representing the path
     *                              from the TOP node to the current one
     * @param engine {@link Engine} the engine that is running the analysis
     * @param info {@link Map} a dictionary containing already parsed
     *                         contextual information
     */
    private static String[] processAdvMods(
            List<Relation> relations, int index, int[] context,
            Engine engine, Map<String, Object> info) {

        int[] advmodIndices = Relation.getChildrenWithDep(
                "advmod", relations, index);

        int[] newContext = ArrayUtils.add(context, index);

        Map<String, Object> newInfo = new HashMap<>();
        newInfo.put("no_emit", true);

        List<String> advmods = new ArrayList<>();
        for (int i: advmodIndices) {
            String advmod = engine.analyze(relations, i, newContext, newInfo);
            advmods.add(advmod);
        }

        return advmods.toArray(new String[0]);
    }

    /**
     * Process nominal modifiers (e.g., 5 years old).
     *
     * @param relations {@link String[]} the list of relations in a sentence
     * @param index {@code int} the index of the relation to be processed
     * @param context {@link int[]} a list of indices representing the path
     *                              from the TOP node to the current one
     * @param engine {@link Engine} the engine that is running the analysis
     * @param info {@link Map} a dictionary containing already parsed
     *                         contextual information
     */
    private static String[] processNMods(
            List<Relation> relations, int index, int[] context,
            Engine engine, Map<String, Object> info) {

        int[] nmodIndices = Relation.getChildrenWithDep(
                "nmod", relations, index);

        int[] newContext = ArrayUtils.add(context, index);

        List<String> nmods = new ArrayList<>();
        for (Integer i: nmodIndices) {
            String nmod = engine.analyze(
                    relations, i, newContext, new HashMap<>());
            nmods.add(nmod);
        }

        return nmods.toArray(new String[0]);
    }

    /**
     * Process reduced clausal modifiers (e.g., hard to imagine).
     *
     * @param relations {@link String[]} the list of relations in a sentence
     * @param index {@code int} the index of the relation to be processed
     * @param context {@link int[]} a list of indices representing the path
     *                              from the TOP node to the current one
     * @param engine {@link Engine} the engine that is running the analysis
     * @param info {@link Map} a dictionary containing already parsed
     *                         contextual information
     */
    private static void processXComp(
            List<Relation> relations, int index, int[] context,
            Engine engine, Map<String, Object> info) {

        int[] xcompIndices = Relation.getChildrenWithDep(
                "xcomp", relations, index);

        int[] newContext = ArrayUtils.add(context, index);

        for (Integer i: xcompIndices) {

            if (!info.containsKey("subj")) {
                info.put("subj", new Subject(new String[] { "NO_SUBJ" }, null));
            }

            engine.analyze(relations, i, newContext, info);
        }
    }

    /**
     * Process adpositional modifiers (e.g., angry with you).
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

        int[] prepIndices = Relation.getChildrenWithDep(
                "prep", relations, index);

        int[] newContext = ArrayUtils.add(context, index);

        for (int i: prepIndices) {
            engine.analyze(relations, i, newContext, new HashMap<>());
        }
    }
}
