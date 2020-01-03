package pt.up.hs.linguini.analysis.ideadensity.rulesets.noun;

import pt.up.hs.linguini.analysis.ideadensity.Engine;
import pt.up.hs.linguini.analysis.ideadensity.Relation;
import pt.up.hs.linguini.analysis.ideadensity.rulesets.Ruleset;
import pt.up.hs.linguini.analysis.ideadensity.rulesets.models.Subject;
import pt.up.hs.linguini.analysis.ideadensity.utils.Tuple;
import pt.up.hs.linguini.utils.ArrayUtils;

import java.util.*;

/**
 * A base class for NP-like dependency substructures.
 *
 * @author José Carlos Paiva <code>josepaiva94@gmail.com</code>
 */
public class NounPhraseRuleset extends Ruleset<Map<String, Object>> {

    public NounPhraseRuleset() {
        super("np");
    }

    public NounPhraseRuleset(String rel) {
        super(rel);
    }

    /**
     * Process the determiners (e.g., the cat).
     *
     * @param relations {@link List<Relation>} the list of relations in a sentence
     * @param index     {@code int} the index of the relation to be processed
     * @param context   {@link int[]} a list of indices representing the path
     *                  from the TOP node to the current one
     * @param engine    {@link Engine} the engine that is running the analysis
     * @param info      {@link Map} a dictionary containing already parsed
     *                  contextual information
     */
    private static String processDeterminers(
            List<Relation> relations, int index, int[] context,
            Engine engine, Map<String, Object> info) {

        int[] detIndices = Relation
                .getChildrenWithDep("det", relations, index);

        if (detIndices.length == 0) {
            return null;
        } else {
            return engine.analyze(
                    relations, detIndices[0],
                    ArrayUtils.add(context, index),
                    new HashMap<>()
            );
        }
    }

    /**
     * Process possessive modifiers (e.g., John's house).
     *
     * @param relations {@link List<Relation>} the list of relations in a sentence
     * @param index     {@code int} the index of the relation to be processed
     * @param context   {@link int[]} a list of indices representing the path
     *                  from the TOP node to the current one
     * @param engine    {@link Engine} the engine that is running the analysis
     * @param info      {@link Map} a dictionary containing already parsed
     *                  contextual information
     */
    private static String processPossessives(
            List<Relation> relations, int index, int[] context,
            Engine engine, Map<String, Object> info) {

        int[] possIndices = Relation
                .getChildrenWithDep("poss", relations, index);

        if (possIndices.length == 0) {
            return null;
        } else {
            return engine.analyze(
                    relations, possIndices[0],
                    ArrayUtils.add(context, index),
                    new HashMap<>()
            );
        }
    }

    /**
     * Process compound nouns (e.g., West Germany).
     *
     * @param relations {@link List<Relation>} the list of relations in a sentence
     * @param index     {@code int} the index of the relation to be processed
     * @param context   {@link int[]} a list of indices representing the path
     *                  from the TOP node to the current one
     * @param engine    {@link Engine} the engine that is running the analysis
     * @param info      {@link Map} a dictionary containing already parsed
     *                  contextual information
     */
    private static Object[] processNounModifiers(
            List<Relation> relations, int index, int[] context,
            Engine engine, Map<String, Object> info) {

        int[] nnjoinIndices = Relation
                .getChildrenWithDep("nn-join", relations, index);

        List<String> nns = new ArrayList<>();

        int[] newContext = ArrayUtils.add(context, index);
        for (int i : nnjoinIndices) {
            String nn = engine.analyze(
                    relations, i, newContext, new HashMap<>());
            nns.add(nn);
        }

        return nns.toArray();
    }

    /**
     * Process composite NP with conjunction (e.g., John and Mary).
     *
     * @param relations {@link List<Relation>} the list of relations in a sentence
     * @param index     {@code int} the index of the relation to be processed
     * @param context   {@link int[]} a list of indices representing the path
     *                  from the TOP node to the current one
     * @param engine    {@link Engine} the engine that is running the analysis
     * @param info      {@link Map} a dictionary containing already parsed
     *                  contextual information
     */
    private static String[] processConjs(
            List<Relation> relations, int index, int[] context,
            Engine engine, Map<String, Object> info) {

        int[] conjIndices = Relation
                .getChildrenWithDep("conj", relations, index);

        String[] conjs;
        if (conjIndices.length > 0) {

            int[] newContext = ArrayUtils.add(context, index);

            // Consume the cc
            int[] ccIndices = Relation
                    .getChildrenWithDep("cc", relations, index);
            for (int i : ccIndices) {
                engine.analyze(relations, i, newContext, new HashMap<>());
            }

            Map<String, Object> newInfo = new HashMap<>();
            newInfo.put("class", "NP");

            // Get the conjs
            List<String[]> conjsTmp = new ArrayList<>();
            for (int i : conjIndices) {
                String[] res = engine.analyze(
                        relations, i, newContext, newInfo);
                conjsTmp.add(res);
            }

            conjs = conjsTmp.parallelStream()
                    .filter(c -> c != null && c.length > 0)
                    .map(c -> c[0])
                    .toArray(String[]::new);
        } else {
            conjs = new String[0];
        }

        return ArrayUtils.concat(
                new String[]{relations.get(index).word()},
                conjs
        );
    }

    /**
     * Process prepositions.
     *
     * @param relations {@link List<Relation>} the list of relations in a sentence
     * @param index     {@code int} the index of the relation to be processed
     * @param context   {@link int[]} a list of indices representing the path
     *                  from the TOP node to the current one
     * @param engine    {@link Engine} the engine that is running the analysis
     * @param info      {@link Map} a dictionary containing already parsed
     *                  contextual information
     */
    private static void processPreps(
            List<Relation> relations, int index, int[] context,
            Engine engine, Map<String, Object> info) {

        // VP modifiers

        int[] prepIndices = Relation
                .getChildrenWithDep("prep", relations, index);

        int[] newContext = ArrayUtils.add(context, index);

        for (int i : prepIndices) {
            engine.analyze(relations, i, newContext, new HashMap<>());
        }
    }

    /**
     * Process adjectival, numerical, and noun modifiers.
     *
     * @param relations {@link String[]} the list of relations in a sentence
     * @param index     {@code int} the index of the relation to be processed
     * @param context   {@link int[]} a list of indices representing the path
     *                  from the TOP node to the current one
     * @param engine    {@link Engine} the engine that is running the analysis
     * @param info      {@link Map} a dictionary containing already parsed
     * @return {@link List} adjectival, numerical, and noun modifiers
     */
    private static String[] processModifiers(
            List<Relation> relations, int index, int[] context,
            Engine engine, Map<String, Object> info) {

        // ADJP modifiers
        int[] amodIndices = Relation
                .getChildrenWithDep("amod", relations, index);
        int[] numIndices = Relation
                .getChildrenWithDep("num", relations, index);
        int[] nnIndices = Relation
                .getChildrenWithDep("nn", relations, index);

        int[] modIndices = ArrayUtils
                .concat(amodIndices, numIndices, nnIndices);
        Arrays.sort(modIndices);

        int[] newContext = ArrayUtils.add(context, index);

        Optional<String[]> mods = Arrays.stream(modIndices)
                .mapToObj(i -> {
                    Object obj = engine.analyze(
                            relations, i, newContext, new HashMap<>());
                    if (obj instanceof String[]) {
                        return ArrayUtils.concat((String[]) obj);
                    } else {
                        return new String[]{(String) obj};
                    }
                })
                .reduce(ArrayUtils::concat);

        return mods.orElse(new String[0]);
    }

    /**
     * Process adpositional modifiers (e.g., result of the action).
     *
     * @param relations  {@link List} the list of relations in a sentence
     * @param index {@code int} the index of the relation to be processed
     * @param context {@link List} a list of indices representing the path from
     *                             the TOP node to the current one
     * @param engine {@link Engine} the engine that is running the analysis
     * @param info {@link Map} a dictionary containing already parsed
     *                         contextual information
     */
    /*private static void processAdpMods(
            List<Relation> relations, int index, List<Integer> context,
            Engine engine, Map<String, Object> info) {

        List<Integer> adpIndices = Relation
                .getChildrenWithDep("adpmod", relations, index);

        List<Integer> childrenContext = new ArrayList<>(context);
        childrenContext.add(index);

        for (int i: adpIndices) {
            engine.analyze(relations, i, childrenContext, new HashMap<>());
        }
    }*/

    /**
     * Process a pre-conjunction.
     *
     * @param relations {@link String[]} the list of relations in a sentence
     * @param index     {@code int} the index of the relation to be processed
     * @param context   {@link int[]} a list of indices representing the path
     *                  from the TOP node to the current one
     * @param engine    {@link Engine} the engine that is running the analysis
     * @param info      {@link Map} a dictionary containing already parsed
     * @return {@link String} pre-conjunction
     */
    private static String processPreconj(
            List<Relation> relations, int index, int[] context,
            Engine engine, Map<String, Object> info) {

        int[] preconjIndices = Relation
                .getChildrenWithDep("cc", relations, index);

        int[] newContext = ArrayUtils.add(context, index);

        String preconj = null;
        if (preconjIndices.length > 0) {
            preconj = engine.analyze(
                    relations, preconjIndices[0], newContext,
                    new HashMap<>()
            );
        }
        return preconj;
    }

    /**
     * Process reduced non-finite verbal modifiers (e.g., points to establish).
     *
     * @param relations {@link String[]} the list of relations in a sentence
     * @param index     {@code int} the index of the relation to be processed
     * @param context   {@link int[]} a list of indices representing the path
     *                  from the TOP node to the current one
     * @param engine    {@link Engine} the engine that is running the analysis
     * @param info      {@link Map} a dictionary containing already parsed
     */
    private static void processVMod(
            List<Relation> relations, int index, int[] context,
            Engine engine, Map<String, Object> info) {

        int[] vmodIndices = Relation
                .getChildrenWithDep("vmod", relations, index);

        int[] newContext = ArrayUtils.add(context, index);

        Map<String, Object> newInfo = new HashMap<>();
        newInfo.put("subj", new Subject(
                new String[]{"NO_SUBJ"},
                null
        ));

        for (int i : vmodIndices) {
            engine.analyze(relations, i, newContext, newInfo);
        }
    }

    /**
     * Process reduced clause modifiers (e.g., the man that I saw).
     *
     * @param relations {@link List} the list of relations in a sentence
     * @param index     {@code int} the index of the relation to be processed
     * @param context   {@link List} a list of indices representing the path from
     *                  the TOP node to the current one
     * @param engine    {@link Engine} the engine that is running the analysis
     * @param info      {@link Map} a dictionary containing already parsed
     */
    private static Tuple processRcmod(
            List<Relation> relations, int index, int[] context,
            Engine engine, Map<String, Object> info) {

        int[] rcmodIndices = Relation
                .getChildrenWithDep("rcmod", relations, index);

        int[] newContext = ArrayUtils.add(context, index);

        int[] ids = new int[0];
        Subject wdt = null;
        for (int i : rcmodIndices) {
            Map<String, Object> ret =
                    engine.analyze(relations, i, newContext, info);
            if (ret.containsKey("prop_ids")) {
                ids = (int[]) ret.get("prop_ids");
            }
            if (ret.containsKey("subjs")) {
                wdt = (Subject) ret.get("subjs");
            }
        }

        return new Tuple(ids, wdt);
    }

    /**
     * Process negations.
     *
     * @param relations {@link List<Relation>} the list of relations in a sentence
     * @param index     {@code int} the index of the relation to be processed
     * @param context   {@link int[]} a list of indices representing the path
     *                  from the TOP node to the current one
     * @param engine    {@link Engine} the engine that is running the analysis
     * @param info      {@link Map} a dictionary containing already parsed
     */
    private static void processNegs(
            List<Relation> relations, int index, int[] context,
            Engine engine, Map<String, Object> info) {

        int[] negIndices = Relation
                .getChildrenWithDep("neg", relations, index);

        int[] newContext = ArrayUtils.add(context, index);
        for (int i : negIndices) {
            engine.analyze(relations, i, newContext, new HashMap<>());
        }
    }

    /**
     * Process noun phrase modifiers (e.g., 5 feet long).
     *
     * @param relations {@link List<Relation>} the list of relations in a sentence
     * @param index     {@code int} the index of the relation to be processed
     * @param context   {@link int[]} a list of indices representing the path
     *                  from the TOP node to the current one
     * @param engine    {@link Engine} the engine that is running the analysis
     * @param info      {@link Map} a dictionary containing already parsed
     */
    private static void processNmods(
            List<Relation> relations, int index, int[] context,
            Engine engine, Map<String, Object> info) {

        int[] nmodIndices = Relation.getChildrenWithDep(
                "nmod", relations, index);

        int[] newContext = ArrayUtils.add(context, index);
        for (int i : nmodIndices) {
            String nmod = engine.analyze(
                    relations, i, newContext,
                    new HashMap<>()
            );
            engine.emit(new Tuple(nmod), "M");
        }
    }

    /**
     * Process adverbial modifiers.
     *
     * @param relations {@link List<Relation>} the list of relations in a sentence
     * @param index     {@code int} the index of the relation to be processed
     * @param context   {@link int[]} a list of indices representing the path
     *                  from the TOP node to the current one
     * @param engine    {@link Engine} the engine that is running the analysis
     * @param info      {@link Map} a dictionary containing already parsed
     */
    private static void processAdvMods(
            List<Relation> relations, int index, int[] context,
            Engine engine, Map<String, Object> info) {

        int[] advmodIndices = Relation.getChildrenWithDep(
                "advmod", relations, index);

        int[] newContext = ArrayUtils.add(context, index);
        for (int i : advmodIndices) {
            engine.analyze(relations, i, newContext, new HashMap<>());
        }
    }

    /**
     * Process appositional modifiers (e.g., John, my brother, is here).
     *
     * @param relations {@link List<Relation>} the list of relations in a sentence
     * @param index     {@code int} the index of the relation to be processed
     * @param context   {@link int[]} a list of indices representing the path
     *                  from the TOP node to the current one
     * @param engine    {@link Engine} the engine that is running the analysis
     * @param info      {@link Map} a dictionary containing already parsed
     */
    private static void processAppos(
            List<Relation> relations, int index, int[] context,
            Engine engine, Map<String, Object> info) {

        int[] apposIndices = Relation.getChildrenWithDep(
                "appos", relations, index);

        for (int i : apposIndices) {
            engine.analyze(
                    relations, i, ArrayUtils.add(context, index), info);
        }
    }

    /**
     * Process predeterminers. Predeterminers are used to express a proportion
     * (such as all, both, or half) of the whole indicated in the noun phrase.
     *
     * @param relations {@link List<Relation>} the list of relations in a sentence
     * @param index     {@code int} the index of the relation to be processed
     * @param context   {@link int[]} a list of indices representing the path
     *                  from the TOP node to the current one
     * @param engine    {@link Engine} the engine that is running the analysis
     * @param info      {@link Map} a dictionary containing already parsed
     * @return predeterminers
     */
    private static String[] processPredets(
            List<Relation> relations, int index, int[] context,
            Engine engine, Map<String, Object> info) {

        int[] predetsIndices = Relation.getChildrenWithDep(
                "predet", relations, index);

        List<String> predets = new ArrayList<>();
        for (int i : predetsIndices) {
            String predet = engine.analyze(relations, i,
                    ArrayUtils.add(context, index), info);
            predets.add(predet);
        }

        return predets.toArray(new String[0]);
    }

    /**
     * Assemble the return list of this NP, given its modifiers (determiners,
     * possessives, compound nouns, and cc/conj modifiers).
     *
     * @param det   {@link String} determiner modifier
     * @param poss  {@link String} possessive modifier
     * @param nns   {@link String[][]} compound nouns modifiers
     * @param conjs {@link String[]} cc/conj modifiers
     * @return {@link Tuple} (proposition list, ids of propositions for
     * reference by eventual preconj propositions)
     */
    private static Tuple assembleReturnList(
            String det, String poss, String[][] nns,
            String[] conjs) {

        // TODO properly handle distribution of possessives

        List<String> returnList = new ArrayList<>();
        List<Integer> idsForPreconj = new ArrayList<>();

        String[] detAndPoss = new String[0];
        if (det != null) {
            detAndPoss = ArrayUtils.add(detAndPoss, det);
        }
        if (poss != null) {
            detAndPoss = ArrayUtils.add(detAndPoss, poss);
        }

        for (int i = 0; i < conjs.length; i++) {
            String conj = conjs[i];
            if (nns.length > 0) {
                // single nn with cc/conj. Emit different propositions.
                for (String nn : nns[0]) {
                    String[] words = ArrayUtils.add(detAndPoss, nn, conj);
                    returnList.add(String.join(" ", words));
                    idsForPreconj.add(returnList.size() - 1);
                }
            } else {
                // no nn
                String[] words = ArrayUtils.add(detAndPoss, conj);
                returnList.add(String.join(" ", words));
            }
        }

        return new Tuple(
                returnList.toArray(new String[0]),
                idsForPreconj.parallelStream().mapToInt(Integer::intValue).toArray()
        );
    }

    /**
     * Assemble the return list of this NP, given its modifiers (determiners,
     * possessives, compound nouns, and cc/conj modifiers).
     *
     * @param det   {@link String} determiner modifier
     * @param poss  {@link String} possessive modifier
     * @param nns   {@link String[]} nouns modifiers
     * @param conjs {@link String[]} cc/conj modifiers
     * @return {@link Tuple} (proposition list, ids of propositions for
     * reference by eventual preconj propositions)
     */
    private static Tuple assembleReturnList(
            String det, String poss, String[] nns,
            String[] conjs) {

        // TODO properly handle distribution of possessives

        List<String> returnList = new ArrayList<>();

        String[] detAndPoss = new String[0];
        if (det != null) {
            detAndPoss = ArrayUtils.add(detAndPoss, det);
        }
        if (poss != null) {
            detAndPoss = ArrayUtils.add(detAndPoss, poss);
        }

        for (int i = 0; i < conjs.length; i++) {
            String conj = conjs[i];

            if (nns.length > 0) {
                // multiple nn modifying the same noun. Join to conj.
                String[] words = ArrayUtils.add(
                        ArrayUtils.concat(detAndPoss, nns), conj);
                returnList.add(String.join(" ", words));
            } else {
                // no nn
                String[] words = ArrayUtils.add(detAndPoss, conj);
                returnList.add(String.join(" ", words));
            }
        }

        return new Tuple(returnList.toArray(new String[0]), new int[0]);
    }

    /**
     * Handle noun phrases that start with 'of' phrases, such as 'some of'.
     *
     * @param relations {@link List<Relation>} the list of relations in a sentence
     * @param index     {@code int} the index of the relation to be processed
     * @param context   {@link int[]} a list of indices representing the path
     *                  from the TOP node to the current one
     * @param engine    {@link Engine} the engine that is running the analysis
     * @param info      {@link Map} a dictionary containing already parsed
     */
    private static Map<String, Object> handleNpWithOfPhrase(
            List<Relation> relations, int index, int[] context,
            Engine engine, Map<String, Object> info) {

        int prepIndex = Relation
                .getChildrenWithDep("prep", relations, index)[0];

        int[] pobjIndices = Relation
                .getChildrenWithDep("pobj", relations, index);

        int pobjIndex;
        if (pobjIndices != null && pobjIndices.length > 0) {
            pobjIndex = pobjIndices[0];
        } else {
            return null;
        }

        Map<String, Object> pobjValue = engine.analyze(
                relations, pobjIndex,
                ArrayUtils.concat(context, new int[]{index, prepIndex}),
                new HashMap<>()
        );

        for (String noun : (String[]) pobjValue.get("return_list")) {
            engine.emit(new Tuple(
                    noun,
                    relations.get(index).word() + " " +
                            relations.get(prepIndex).word()), "M");
        }

        Engine.markProcessed(relations, prepIndex);

        return pobjValue;
    }

    @Override
    public Map<String, Object> extract(
            List<Relation> relations, int index, int[] context,
            Engine engine, Map<String, Object> info) {

        if (info == null) {
            info = new HashMap<>();
        }

        String npWithOfPhrases = engine.getConfigValue("npWithOfPhrases");

        if (relations.get(index).word().toLowerCase()
                .matches(npWithOfPhrases)) {
            int[] deps = relations.get(index).deps();
            if (deps != null && deps.length > 0) {
                if (relations.get(deps[0]).rel().equalsIgnoreCase("prep")) {
                    return handleNpWithOfPhrase(
                            relations,
                            index,
                            context,
                            engine,
                            new HashMap<>()
                    );
                }
            }
        }

        String det = processDeterminers(
                relations, index, context, engine, info);

        String poss = processPossessives(
                relations, index, context, engine, info);

        Object[] nns = processNounModifiers(
                relations, index, context, engine, info);

        String[] conjs = processConjs(
                relations, index, context, engine, info);

        processPreps(relations, index, context, engine, info);

        String[] mods = processModifiers(
                relations, index, context, engine, info);

        processVMod(relations, index, context, engine, info);

        processNegs(relations, index, context, engine, info);

        processNmods(relations, index, context, engine, info);

        processAdvMods(relations, index, context, engine, info);

        Tuple tuple = null;
        if (nns.length == 0) {
            tuple = assembleReturnList(det, poss, new String[0], conjs);
        } else if (nns[0] instanceof String) {
            tuple = assembleReturnList(
                    det,
                    poss,
                    Arrays.copyOf(nns, nns.length, String[].class),
                    conjs);
        } else if (nns[0] instanceof String[]) {
            tuple = assembleReturnList(det, poss, (String[][]) nns, conjs);
        }

        String[] returnList;
        int[] idsForPreconj;
        if (tuple != null) {
            returnList = (String[]) tuple.get(0);
            idsForPreconj = (int[]) tuple.get(1);
        } else {
            returnList = new String[0];
            idsForPreconj = new int[0];
        }

        Map<String, Object> newInfo = new HashMap<>();
        newInfo.put("subj", new Subject(returnList, null));

        processAppos(relations, index, context, engine, newInfo);

        String[] predets = processPredets(
                relations, index, context, engine, info);

        // emit propositions for modifiers
        for (String amod : mods) {
            for (String noun : returnList) {
                engine.emit(new Tuple(noun, amod), "M");
            }
        }

        // emit propositions for predeterminers
        for (String predet : predets) {
            for (String noun : returnList) {
                engine.emit(new Tuple(noun, predet), "M");
            }
        }

        String preconj = processPreconj(
                relations, index, context, engine, info);

        newInfo.put("subj", new Subject(returnList, null));

        Tuple rcmod = processRcmod(
                relations, index, context, engine, newInfo);

        int[] ids = (int[]) rcmod.get(0);
        Subject wdt = (Subject) rcmod.get(1);

        Map<String, Object> ret = new HashMap<>();
        ret.put("return_list", returnList);
        ret.put("preconj", preconj);
        ret.put("ids_for_preconj", idsForPreconj);
        ret.put("rcmod_wdt", wdt);
        ret.put("rcmod_ids", ids);

        return ret;
    }
}
