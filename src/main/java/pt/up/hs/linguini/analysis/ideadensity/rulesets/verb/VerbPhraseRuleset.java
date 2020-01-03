package pt.up.hs.linguini.analysis.ideadensity.rulesets.verb;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pt.up.hs.linguini.analysis.ideadensity.Engine;
import pt.up.hs.linguini.analysis.ideadensity.Relation;
import pt.up.hs.linguini.analysis.ideadensity.rulesets.Ruleset;
import pt.up.hs.linguini.analysis.ideadensity.rulesets.adjectival.AdjectivalPhraseRuleset;
import pt.up.hs.linguini.analysis.ideadensity.rulesets.models.Subject;
import pt.up.hs.linguini.analysis.ideadensity.rulesets.noun.NounPhraseRuleset;
import pt.up.hs.linguini.analysis.ideadensity.utils.Tuple;
import pt.up.hs.linguini.utils.ArrayUtils;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A base class for VP-like dependency substructures.
 *
 * @author José Carlos Paiva <code>josepaiva94@gmail.com</code>
 */
public class VerbPhraseRuleset extends Ruleset<Map<String, Object>> {

    private static final Logger LOGGER = LoggerFactory.getLogger(
            VerbPhraseRuleset.class.getSimpleName());

    private Subject subjs;
    private String[] auxs;

    public VerbPhraseRuleset() {
        super("vp");
    }

    public VerbPhraseRuleset(String rel) {
        super(rel);
    }

    @Override
    public Map<String, Object> extract(
            List<Relation> relations, int index, int[] context,
            Engine engine, Map<String, Object> info) {

        // process discourse markers
        processDiscourseMarkers(relations, index, context, engine, info);

        Map<String, Object> returnDict;
        if (relations.get(index).word().matches(
                "^" + engine.getConfigValue("beForms") + "$")) {
            returnDict = handleBeAsRoot(
                    relations, index, context, engine, info);
        } else if (relations.get(index).tag().toUpperCase()
                .matches("VERB")) { // VBZ|VBD|VBN|VB|VBG|VBP
            returnDict = handleActionVerb(
                    relations, index, context, engine, info);
        } else if (relations.get(index).tag().toUpperCase()
                .matches("NOUN|NUM|PRON")) { // NN|NNS|NNP|NNPS|CD|WP|PRP
            returnDict = handleCopWithNp(
                    relations, index, context, engine, info);
        } else if (relations.get(index).tag().toUpperCase()
                .equalsIgnoreCase("ADJ")) { // JJ
            // DMH: treat JJR and JJS the same as JJ for now?
            returnDict = handleCopWithAdjp(
                    relations, index, context, engine, info);
        } else {
            LOGGER.error(String.format("VP cannot handle %s yet.",
                    relations.get(index).tag()));
            returnDict = new HashMap<>();
            returnDict.put("prop_ids", new int[0]);
            return returnDict;
        }

        // adverbial clauses
        processAdvcl(relations, index, context, engine, info,
                (int[]) returnDict.get("prop_ids"));

        // conjunctions
        processConjs(relations, index, context, engine, info,
                subjs, auxs, (int[]) returnDict.get("prop_ids"));

        // process parataxical clauses
        processParataxes(relations, index, context, engine, info);

        // process ignorable elements
        processIgnorables(relations, index, context, engine, info);

        returnDict.put("subjs", subjs);

        // process rcmods
        emitPropositionsRcmods(returnDict, engine);

        return returnDict;
    }

    /**
     * Process the subject of the verb phrase.
     *
     * @param relations {@link List<Relation>} the list of relations in a sentence
     * @param index {@code int} the index of the relation to be processed
     * @param context {@link int[]} a list of indices representing the path
     *                              from the TOP node to the current one
     * @param engine {@link Engine} the engine that is running the analysis
     * @param info {@link Map} a dictionary containing already parsed
     *                         contextual information
     */
    private static Subject processSubj(
            List<Relation> relations, int index, int[] context,
            Engine engine, Map<String, Object> info) {

        int[] newContext = ArrayUtils.add(context, index);

        // nsubj
        int[] subjIndices = Relation
                .getChildrenWithDep("nsubj", relations, index);
        Subject subj = null;
        if (subjIndices.length == 0) {
            if (info.containsKey("subj")) {
                Subject oldSubject = (Subject) info.get("subj");

                subj = new Subject(
                        Arrays.stream(oldSubject.getReturnList())
                            .map(s -> String.format("(%s)", s))
                            .toArray(String[]::new),
                        null
                );
            } else {
                subj = new Subject(new String[]{ "(NO_SUBJ)" }, null);
            }
        } else {
            Map<String, Object> result = engine.analyze(
                    relations, subjIndices[0], newContext, new HashMap<>());
            subj = new Subject(
                    (String[]) result.get("return_list"),
                    (Map<String, Object>) result.get("rcmod_wdt"));
            if (result.containsKey("rcmod_ids")) {
                subj.setRcmodIds((int[]) result.get("rcmod_ids"));
            }
        }

        // nsubjpass
        subjIndices = Relation
                .getChildrenWithDep("nsubjpass", relations, index);
        if (subjIndices.length > 0) {
            subj = engine.analyze(
                    relations, subjIndices[0], context, new HashMap<>());
        }

        // csubj
        subjIndices = Relation
                .getChildrenWithDep("csubj", relations, index);
        if (subjIndices.length > 0) {
            String value = (String) ((Map) engine.analyze(
                    relations, subjIndices[0], newContext, new HashMap<>()))
                    .get("return_value");
            subj = new Subject(new String[]{ value }, null);
        }

        if (subj == null) {
            subj = new Subject(new String[]{ "(NO_SUBJ)" }, null);
        }

        // replace NoneType with NO SUBJECT string
        if (subj.getReturnList().length == 0 ||
                subj.getReturnList()[0] == null) {
            subj.getReturnList()[0] = "(NO_SUBJ)";
        }

        // resolve relative pronouns in subordinate clauses.
        String relativePronouns = engine.getConfigValue("relativePronouns");
        if (subj.getReturnList().length > 0 &&
                subj.getReturnList()[0].matches(relativePronouns) &&
                info.containsKey("subj")) {
            subj.getReturnList()[0] += String.format("(=%s)",
                    ((Subject) info.get("subj")).getReturnList()[0]);
        }

        return subj;
    }

    /**
     * Process auxiliaries and modals.
     *
     * @param relations {@link List<Relation>} the list of relations in a sentence
     * @param index {@code int} the index of the relation to be processed
     * @param context {@link int[]} a list of indices representing the path
     *                              from the TOP node to the current one
     * @param engine {@link Engine} the engine that is running the analysis
     * @param info {@link Map} a dictionary containing already parsed
     *                         contextual information
     */
    private static String[] processAuxs(
            List<Relation> relations, int index, int[] context,
            Engine engine, Map<String, Object> info) {

        int[] auxIndices = Relation
                .getChildrenWithDep("aux", relations, index);
        int[] auxpassIndices = Relation
                .getChildrenWithDep("auxpass", relations, index);

        int[] auxsIndices = ArrayUtils.concat(auxIndices, auxpassIndices);
        Arrays.sort(auxsIndices);

        int[] newContext = ArrayUtils.add(context, index);

        String[] auxs = new String[0];
        for (int i: auxsIndices) {
            String auxsTmp = engine.analyze(
                    relations, i, newContext, new HashMap<>());
            auxs = ArrayUtils.add(auxs, auxsTmp);
        }

        if (auxs.length == 0 && info.containsKey("aux"))  {
            auxs = (String[]) info.get("aux");
        }

        return auxs;
    }

    /**
     * Process phrasal verb particles.
     *  @param relations {@link List<Relation>} the list of relations in a sentence
     * @param index {@code int} the index of the relation to be processed
     * @param context {@link int[]} a list of indices representing the path
     *                              from the TOP node to the current one
     * @param engine {@link Engine} the engine that is running the analysis
     * @param info {@link Map} a dictionary containing already parsed
     */
    private static String processPrt(
            List<Relation> relations, int index, int[] context,
            Engine engine, Map<String, Object> info) {

        int[] prtIndices = Relation
                .getChildrenWithDep("prt", relations, index);

        int[] newContext = ArrayUtils.add(context, index);

        String prt = null;
        if (prtIndices.length > 0) {
            prt = engine.analyze(
                    relations, prtIndices[0], newContext, new HashMap<>());
        }
        return prt;
    }

    /**
     * Process complements (direct objects, open clausal complements,
     * adjectival complements, and subject predicates).
     *
     * @param relations  {@link List} the list of relations in a sentence
     * @param index {@code int} the index of the relation to be processed
     * @param context {@link List} a list of indices representing the path from
     *                             the TOP node to the current one
     * @param engine {@link Engine} the engine that is running the analysis
     * @param info {@link Map} a dictionary containing already parsed
     *                         contextual information
     */
    private static String[] processComps(
            List<Relation> relations, int index, int[] context,
            Engine engine, Map<String, Object> info) {

        int[] objIndices = Relation
                .getChildrenWithDep("obj", relations, index);
        int[] xcompIndices = Relation
                .getChildrenWithDep("xcomp", relations, index);
        int[] acompIndices = Relation
                .getChildrenWithDep("acomp", relations, index);
        int[] attrIndices = Relation
                .getChildrenWithDep("attr", relations, index);

        int[] compsIndices = ArrayUtils.concat(
                objIndices,
                xcompIndices,
                acompIndices,
                attrIndices
        );

        int[] newContext = ArrayUtils.add(context, index);

        String[] comps = new String[0];
        for (int i: compsIndices) {
            Object obj = engine.analyze(relations, i, newContext, info);
            if (obj instanceof Map) {
                Map<String, Object> compTmp = ((Map) obj);
                if (compTmp.containsKey("return_value")) {
                    comps = ArrayUtils.add(comps, (String) compTmp.get("return_value"));
                } else if (compTmp.containsKey("return_list")) {
                    comps = ArrayUtils.concat(comps, (String[]) compTmp.get("return_list"));
                }
            } else if (obj instanceof String[]) {
                comps = ArrayUtils.concat(comps, (String[]) obj);
            } else if (obj != null) {
                comps = ArrayUtils.add(comps, (String) obj);
            }
        }

        return comps;
    }

    /**
     * Process clausal complements.
     *
     * @param relations {@link List<Relation>} the list of relations in a sentence
     * @param index {@code int} the index of the relation to be processed
     * @param context {@link int[]} a list of indices representing the path
     *                              from the TOP node to the current one
     * @param engine {@link Engine} the engine that is running the analysis
     * @param info {@link Map} a dictionary containing already parsed
     *                         contextual information
     */
    private static void processCcomp(
            List<Relation> relations, int index, int[] context,
            Engine engine, Map<String, Object> info) {

        int[] ccompIndices = Relation
                .getChildrenWithDep("ccomp", relations, index);

        int[] newContext = ArrayUtils.add(context, index);

        if (ccompIndices.length > 0) {
            engine.analyze(relations, ccompIndices[0], newContext, info);
        }
    }

    /**
     * Process the indirect object.
     *
     * @param relations  {@link List} the list of relations in a sentence
     * @param index {@code int} the index of the relation to be processed
     * @param context {@link List} a list of indices representing the path from
     *                             the TOP node to the current one
     * @param engine {@link Engine} the engine that is running the analysis
     * @param info {@link Map} a dictionary containing already parsed
     *                         contextual information
     */
    private static void processIobj(
            List<Relation> relations, int index, int[] context,
            Engine engine, Map<String, Object> info) {

        int[] newContext = ArrayUtils.add(context, index);

        // adpmod + adpobj
        int[] prepIndices = Relation.getChildrenWithDep(
                "prep", relations, index);

        for (int i: prepIndices) {
            engine.analyze(relations, i, newContext, new HashMap<>());
        }

        // iobj
        int[] iobjIndices = Relation.getChildrenWithDep(
                "iobj", relations, index);
        if (iobjIndices.length > 0) {
            engine.analyze(
                    relations, iobjIndices[0], newContext, new HashMap<>());
        }
    }

    /**
     * Process the indirect object.
     *
     * @param relations  {@link List} the list of relations in a sentence
     * @param index {@code int} the index of the relation to be processed
     * @param context {@link List} a list of indices representing the path from
     *                             the TOP node to the current one
     * @param engine {@link Engine} the engine that is running the analysis
     * @param info {@link Map} a dictionary containing already parsed
     *                         contextual information
     */
    private static void processOblique(
            List<Relation> relations, int index, int[] context,
            Engine engine, Map<String, Object> info) {

        int[] newContext = ArrayUtils.add(context, index);

        // advmod
        int[] prepIndices = Relation.getChildrenWithDep(
                "advmod", relations, index);

        for (int i: prepIndices) {
            engine.analyze(relations, i, newContext, new HashMap<>());
        }

        // advcl
        int[] advclIndices = Relation.getChildrenWithDep(
                "advcl", relations, index);

        for (int i: advclIndices) {
            engine.analyze(relations, i, newContext, new HashMap<>());
        }

        // nmod
        int[] nmodIndices = Relation.getChildrenWithDep(
                "nmod", relations, index);

        for (int i: nmodIndices) {
            engine.analyze(relations, i, newContext, new HashMap<>());
        }

        // det
        int[] detIndices = Relation.getChildrenWithDep(
                "det", relations, index);

        for (int i: detIndices) {
            engine.analyze(relations, i, newContext, new HashMap<>());
        }

        // cc
        int[] ccIndices = Relation.getChildrenWithDep(
                "cc", relations, index);

        for (int i: ccIndices) {
            engine.analyze(relations, i, newContext, new HashMap<>());
        }

        // obl
        int[] oblIndices = Relation.getChildrenWithDep(
                "obl", relations, index);
        if (oblIndices.length > 0) {
            engine.analyze(
                    relations, oblIndices[0], newContext, new HashMap<>());
        }
    }

    /**
     * Process adverbial modifiers (advmod, tmod - DEPRECATED, and neg).
     *
     * @param relations  {@link List} the list of relations in a sentence
     * @param index {@code int} the index of the relation to be processed
     * @param context {@link List} a list of indices representing the path from
     *                             the TOP node to the current one
     * @param engine {@link Engine} the engine that is running the analysis
     * @param info {@link Map} a dictionary containing already parsed
     *                         contextual information
     */
    private static void processAdvs(
            List<Relation> relations, int index, int[] context,
            Engine engine, Map<String, Object> info) {

        int[] newContext = ArrayUtils.add(context, index);

        // advmod
        int[] advmodIndices = Relation
                .getChildrenWithDep("advmod", relations, index);
        for (int i: advmodIndices) {
            engine.analyze(relations, i, newContext, new HashMap<>());
        }

        // tmod
        int[] tmodIndices = Relation
                .getChildrenWithDep("tmod", relations, index);
        for (int i: tmodIndices) {
            engine.analyze(relations, i, newContext, new HashMap<>());
        }

        // neg
        int[] negIndices = Relation
                .getChildrenWithDep("neg", relations, index);
        for (int i: negIndices) {
            engine.analyze(relations, i, newContext, new HashMap<>());
        }
    }

    /**
     * Process elements that can be ignored (complm - DEPRECATED, and mark).
     *
     * @param relations {@link List<Relation>} the list of relations in a sentence
     * @param index {@code int} the index of the relation to be processed
     * @param context {@link int[]} a list of indices representing the path
     *                              from the TOP node to the current one
     * @param engine {@link Engine} the engine that is running the analysis
     * @param info {@link Map} a dictionary containing already parsed
     *                         contextual information
     */
    private static void processIgnorables(
            List<Relation> relations, int index, int[] context,
            Engine engine, Map<String, Object> info) {

        int[] newContext = ArrayUtils.add(context, index);

        // complm
        int[] complmIndices = Relation
                .getChildrenWithDep("complm", relations, index);
        for (int i: complmIndices) {
            engine.analyze(relations, i, newContext, new HashMap<>());
        }

        // mark
        int[] markIndices = Relation
                .getChildrenWithDep("mark", relations, index);
        for (int i: markIndices) {
            engine.analyze(relations, i, newContext, new HashMap<>());
        }
    }

    /**
     * Process noun phrase modifiers.
     *
     * @param relations {@link List<Relation>} the list of relations in a sentence
     * @param index {@code int} the index of the relation to be processed
     * @param context {@link int[]} a list of indices representing the path
     *                              from the TOP node to the current one
     * @param engine {@link Engine} the engine that is running the analysis
     * @param info {@link Map} a dictionary containing already parsed
     *                         contextual information
     */
    private static void processNmods(
            List<Relation> relations, int index, int[] context,
            Engine engine, Map<String, Object> info) {

        int[] newContext = ArrayUtils.add(context, index);

        // nmod
        int[] nmodIndices = Relation
                .getChildrenWithDep("nmod", relations, index);

        String[] mods = new String[0];
        for (int i: nmodIndices) {
            Object obj = engine.analyze(
                    relations, i, newContext, new HashMap<>());
            if (obj instanceof String[]) {
                mods = ArrayUtils.concat(mods, (String[]) obj);
            } else {
                mods = ArrayUtils.add(mods, (String) obj);
            }
        }

        for (String mod: mods) {
            engine.emit(
                    new Tuple(relations.get(index).word(), mod),
                    "M"
            );
        }
    }

    /**
     * Process prepositional phrases when be is root.
     *
     * @param relations  {@link List} the list of relations in a sentence
     * @param index {@code int} the index of the relation to be processed
     * @param context {@link List} a list of indices representing the path from
     *                             the TOP node to the current one
     * @param engine {@link Engine} the engine that is running the analysis
     * @param info {@link Map} a dictionary containing already parsed
     *                         contextual information
     */
    private static String[] processPpWhenBeIsRoot(
            List<Relation>relations, int index, int[] context,
            Engine engine, Map<String, Object> info,
            Subject subjs) {

        // prep
        int[] prepIndices = Relation
                .getChildrenWithDep("prep", relations, index);

        int[] newContext = ArrayUtils.add(context, index);

        if (prepIndices.length == 0) {
            return new String[0];
        }

        if (subjs.getReturnList()[0].toLowerCase()
                .equalsIgnoreCase("it")) {
            // TODO adapt for other countries
            int prepIndex = prepIndices[0];
            int[] pobjChildren = Relation
                    .getChildrenWithDep("pobj", relations, prepIndex);
            int pobjIndex;
            if (pobjChildren.length > 0)
                pobjIndex = pobjChildren[0];
            else
                return new String[0];

            Map<String, Object> pobjReturnValue = engine.analyze(
                    relations, pobjIndex,
                    ArrayUtils.add(context, index, prepIndex),
                    new HashMap<>());

            String[] returnList = new String[0];
            if (pobjReturnValue.containsKey("return_list")) {
                for (String noun:
                        (String[]) pobjReturnValue.get("return_list")) {
                    returnList = ArrayUtils.add(returnList,
                            relations.get(prepIndex).word() + " " + noun);
                }
            }

            Engine.markProcessed(relations, prepIndex);

            return returnList;
        }

        for (int i: prepIndices) {
            engine.analyze(relations, i, newContext, new HashMap<>());
        }

        return new String[0];
    }

    /**
     * Process adverbial modifiers when be is root.
     *
     * @param relations  {@link List} the list of relations in a sentence
     * @param index {@code int} the index of the relation to be processed
     * @param context {@link List} a list of indices representing the path from
     *                             the TOP node to the current one
     * @param engine {@link Engine} the engine that is running the analysis
     * @param info {@link Map} a dictionary containing already parsed
     *                         contextual information
     */
    private static String[] processAdvmodWhenBeIsRoot(
            List<Relation> relations, int index, int[] context,
            Engine engine, Map<String, Object> info,
            Subject subjs) {

        int[] advmodIndices = Relation
                .getChildrenWithDep("advmod", relations, index);

        int[] newContext = ArrayUtils.add(context, index);

        String[] advmods = new String[0];
        if (advmodIndices.length > 0) {
            Map<String, Object> newInfo = new HashMap<>();
            if (subjs.getReturnList()[0].toLowerCase()
                    .equalsIgnoreCase("it")) {
                // TODO adapt for other countries
                newInfo.put("no_emit", true);
            }

            Object obj = engine.analyze(relations, advmodIndices[0],
                    newContext, newInfo);
            if (obj instanceof String[]) {
                advmods = ArrayUtils.concat(advmods, (String[]) obj);
            } else {
                advmods = ArrayUtils.add(advmods, (String) obj);
            }
        }

        return advmods;
    }

    /**
     * Process discourse markers.
     *
     * @param relations {@link List<Relation>} the list of relations in a sentence
     * @param index {@code int} the index of the relation to be processed
     * @param context {@link int[]} a list of indices representing the path
     *                              from the TOP node to the current one
     * @param engine {@link Engine} the engine that is running the analysis
     * @param info {@link Map} a dictionary containing already parsed
     *                         contextual information
     */
    private static void processDiscourseMarkers(
            List<Relation> relations, int index, int[] context,
            Engine engine, Map<String, Object> info) {

        int[] discourseIndices = Relation
                .getChildrenWithDep("discourse", relations, index);

        int[] newContext = ArrayUtils.add(context, index);

        for (int i: discourseIndices) {
            engine.analyze(relations, i, newContext, new HashMap<>());
        }
    }

    /**
     * Process adverbial clauses.
     *
     * @param relations  {@link List} the list of relations in a sentence
     * @param index {@code int} the index of the relation to be processed
     * @param context {@link List} a list of indices representing the path from
     *                             the TOP node to the current one
     * @param engine {@link Engine} the engine that is running the analysis
     * @param info {@link Map} a dictionary containing already parsed
     *                         contextual information
     */
    private static void processAdvcl(
            List<Relation> relations, int index, int[] context,
            Engine engine, Map<String, Object> info,
            int[] propIds) {

        int[] advclIndices = Relation
                .getChildrenWithDep("advcl", relations, index);

        int[] newContext = ArrayUtils.add(context, index);

        for (int i: advclIndices) {
            Map<String, Object> ret = engine
                    .analyze(relations, i, newContext, new HashMap<>());
            if (ret.containsKey("marker")) {
                for (int p: propIds) {
                    Tuple prop = new Tuple(ret.get("marker"), p);
                    int[] retPropIds = (int[]) ret.get("prop_ids");
                    for (int retPropId: retPropIds) {
                        prop.add(retPropId);
                    }
                    engine.emit(prop, "C");
                }
            }
        }
    }

    /**
     * Process cc/conj.
     *
     * @param relations  {@link List} the list of relations in a sentence
     * @param index {@code int} the index of the relation to be processed
     * @param context {@link List} a list of indices representing the path from
     *                             the TOP node to the current one
     * @param engine {@link Engine} the engine that is running the analysis
     * @param info {@link Map} a dictionary containing already parsed
     *                         contextual information
     */
    private static void processConjs(
            List<Relation> relations, int index, int[] context,
            Engine engine, Map<String, Object> info,
            Subject subjs, String[] auxs,
            int[] propIds) {

        int[] conjIndices = Relation
                .getChildrenWithDep("conj", relations, index);

        int[] newContext = ArrayUtils.add(context, index);

        if (conjIndices.length > 0) {
            int[] ccIndices = Relation
                    .getChildrenWithDep("cc", relations, index);

            String conjunction = null;
            if (ccIndices.length > 0) {
                conjunction = engine.analyze(
                        relations, ccIndices[0], newContext, new HashMap<>());
            }

            int[] preconjIndices = Relation
                    .getChildrenWithDep("preconj", relations, index);
            if (preconjIndices.length > 0) {
                String preconj = engine.analyze(
                        relations, preconjIndices[0],
                        newContext, new HashMap<>());
                conjunction = preconj + "_" + conjunction;
            }

            for (int i: conjIndices) {
                Map<String, Object> conjInfo = new HashMap<>();
                conjInfo.put("class", "VP");
                conjInfo.put("subj", subjs);
                conjInfo.put("aux", auxs);

                Map<String, Object> ret = engine
                        .analyze(relations, i, newContext, conjInfo);
                propIds = ArrayUtils
                        .concat(propIds, (int[]) ret.get("prop_ids"));
            }

            if (conjunction != null) {
                Tuple conjProp = new Tuple(conjunction);
                for (int propId: propIds) {
                    conjProp.add(propId);
                }
                engine.emit(conjProp, "C");
            }
        }
    }

    /**
     * Process parataxical verb phrases.
     *
     * @param relations  {@link List} the list of relations in a sentence
     * @param index {@code int} the index of the relation to be processed
     * @param context {@link List} a list of indices representing the path from
     *                             the TOP node to the current one
     * @param engine {@link Engine} the engine that is running the analysis
     * @param info {@link Map} a dictionary containing already parsed
     *                         contextual information
     */
    private static void processParataxes(
            List<Relation> relations, int index, int[] context,
            Engine engine, Map<String, Object> info) {

        int[] parataxesIndices = Relation
                .getChildrenWithDep("parataxis", relations, index);

        int[] childrenContext = ArrayUtils.add(context, index);

        for (int i: parataxesIndices) {
            engine.analyze(relations, i, childrenContext, new HashMap<>());
        }
    }

    /**
     * Processes children with label 'what'.
     *
     * @param relations  {@link List} the list of relations in a sentence
     * @param index {@code int} the index of the relation to be processed
     * @param context {@link List} a list of indices representing the path from
     *                             the TOP node to the current one
     * @param engine {@link Engine} the engine that is running the analysis
     * @param info {@link Map} a dictionary containing already parsed
     *                         contextual information
     */
    private static void processWhats(
            List<Relation> relations, int index, int[] context,
            Engine engine, Map<String, Object> info) {

        int[] whatIndices = Relation
                .getChildrenWithDep("what", relations, index);

        int[] newContext = ArrayUtils.add(context, index);

        for (int i: whatIndices) {
            engine.analyze(relations, i, newContext, info);
        }
    }

    /**
     * Processes reduced non-finite verbal modifiers.
     *
     * @param relations  {@link List} the list of relations in a sentence
     * @param index {@code int} the index of the relation to be processed
     * @param context {@link List} a list of indices representing the path from
     *                             the TOP node to the current one
     * @param engine {@link Engine} the engine that is running the analysis
     * @param info {@link Map} a dictionary containing already parsed
     *                         contextual information
     */
    private static void processVmods(
            List<Relation> relations, int index, int[] context,
            Engine engine, Map<String, Object> info) {

        int[] vmodIndices = Relation
                .getChildrenWithDep("vmod", relations, index);

        int[] newContext = ArrayUtils.add(context, index);

        for (int i: vmodIndices) {
            engine.analyze(relations, i, newContext, info);
        }
    }

    /**
     * Emit propositions for action verbs.
     *
     * @param verb  {@link String} the list of relations in a sentence
     * @param subjs {@link List} the subjects
     * @param objs {@link List}
     * @param engine {@link Engine} the engine that is running the analysis
     * @param relation {@link Relation} relation to emit propositions from.
     */
    private static int[] emitPropositions(
            String verb,
            Subject subjs,
            String[] objs,
            Engine engine,
            Relation relation) {

        int[] propIds = new int[0];

        if (relation.tag().equalsIgnoreCase("VBG") &&
                relation.rel() != null &&
                !relation.rel().toLowerCase().matches("null|root|conj|vmod")) {

            if (objs == null || objs.length == 0) {
                int propId = engine.emit(new Tuple(verb), "P");
                propIds = ArrayUtils.add(propIds, propId);
            } else {
                for (String obj: objs) {
                    Tuple proposition = new Tuple(verb, obj);
                    int propId = engine.emit(proposition, "P");
                    propIds = ArrayUtils.add(propIds, propId);
                }
            }
        } else {
            for (String subj: subjs.getReturnList()) {
                if (objs.length > 0) {
                    for (String obj: objs) {
                        Tuple proposition = new Tuple(verb, subj, obj);
                        int propId = engine.emit(proposition, "P");
                        propIds = ArrayUtils.add(propIds, propId);
                    }
                } else {
                    int propId;
                    if (relation.rel().equalsIgnoreCase("vmod")) {
                        propId = engine.emit(new Tuple(verb), "P");
                    } else {
                        propId = engine.emit(new Tuple(verb, subj), "P");
                    }
                    propIds = ArrayUtils.add(propIds, propId);
                }
            }
        }

        return propIds;
    }

    /**
     * Emits propositions for rcmods.
     *
     * @param returnDict {@link Map} dictionary to return
     * @param engine {@link Engine} the engine that is running the analysis
     */
    private static void emitPropositionsRcmods(
            Map<String, Object> returnDict, Engine engine) {


        Subject subjs = (Subject) returnDict.get("subjs");


        if (subjs != null && subjs.getRcmodWdt() != null) {

            int[] mainPropIds = (int[]) returnDict.get("prop_ids");
            for (int mainPropId: mainPropIds) {
                int[] rcmodPropIds = subjs.getRcmodIds();
                for (int rcmodPropId: rcmodPropIds) {
                    Map<String, Object> rcmodWdt = subjs.getRcmodWdt();
                    String[] returnList = (String[]) rcmodWdt.get("return_list");
                    engine.emit(new Tuple(
                            returnList[0], mainPropId, rcmodPropId), "C");
                }
            }
        }

        if (returnDict.containsKey("this")) {

            // emit propositions for copula + NP
            int[] mainPropIds = (int[]) returnDict.get("prop_ids");
            for (int mainPropId: mainPropIds) {
                Map<String, Object> thisObj = (Map<String, Object>) returnDict.get("this");
                int[] rcmodPropIds = (int[]) thisObj.get("rcmod_ids");
                for (int rcmodPropId: rcmodPropIds) {
                    Map<String, Object> rcmodWdt = (Map<String, Object>) thisObj.get("rcmod_wdt");
                    String[] returnList = (String[]) rcmodWdt.get("return_list");
                    engine.emit(new Tuple(returnList[0], mainPropId, rcmodPropId), "C");
                }
            }
        }

            }

    /**
     * Handle 'to be' as the VP root.
     *
     * @param relations  {@link List} the list of relations in a sentence
     * @param index {@code int} the index of the relation to be processed
     * @param context {@link List} a list of indices representing the path from
     *                             the TOP node to the current one
     * @param engine {@link Engine} the engine that is running the analysis
     * @param info {@link Map} a dictionary containing already parsed
     *                         contextual information
     */
    private Map<String, Object> handleBeAsRoot(
            List<Relation> relations, int index, int[] context,
            Engine engine, Map<String, Object> info) {

        Subject subjs = processSubj(
                relations, index, context, engine, info);

        String[] auxs = processAuxs(
                relations, index, context, engine, info);

        String verb = String.join(" ",
                ArrayUtils.add(auxs, relations.get(index).word()));

        // prepositional modifiers
        String[] prepMods = processPpWhenBeIsRoot(
                relations, index, context, engine, info, subjs);

        // adverbial modifiers
        String[] advMods = processAdvmodWhenBeIsRoot(
                relations, index, context, engine, info, subjs);

        String[] mods = ArrayUtils.concat(prepMods, advMods);

        processIgnorables(relations, index, context, engine, info);

        processVmods(relations, index, context, engine, info);

        // emit propositions
        int[] propsIds = new int[0];
        if (mods.length > 0) {
            // check 'it'
            if (subjs.getReturnList()[0].equalsIgnoreCase("it")) {
                for (String subj: subjs.getReturnList()) {
                    for (String mod: mods) {
                        int propId = engine.emit(new Tuple(verb, subj, mod), "P");
                        propsIds = ArrayUtils.add(propsIds, propId);
                    }
                }
            } else {
                for (String subj: subjs.getReturnList()) {
                    int propId = engine.emit(new Tuple(verb, subj), "P");
                    propsIds = ArrayUtils.add(propsIds, propId);
                }
            }

        } else {
            for (String subj: subjs.getReturnList()) {
                int propId = engine.emit(new Tuple(verb, subj), "P");
                propsIds = ArrayUtils.add(propsIds, propId);
            }
        }

        this.subjs = subjs;
        this.auxs = auxs;

        Map<String, Object> ret = new HashMap<>();
        ret.put("prop_ids", propsIds);

        return ret;
    }

    /**
     * Handle an action verb as the VP root.
     *
     * @param relations  {@link List} the list of relations in a sentence
     * @param index {@code int} the index of the relation to be processed
     * @param context {@link List} a list of indices representing the path from
     *                             the TOP node to the current one
     * @param engine {@link Engine} the engine that is running the analysis
     * @param info {@link Map} a dictionary containing already parsed
     *                         contextual information
     * @return {@link Map} the result of processing an action verb
     */
    protected Map<String, Object> handleActionVerb(
            List<Relation> relations, int index, int[] context,
            Engine engine, Map<String, Object> info) {

        Subject subjs = processSubj(
                relations, index, context, engine, info);

        String[] auxs = processAuxs(
                relations, index, context, engine, info);

        String prt = processPrt(
                relations, index, context, engine, info);

        String[] toJoinVerb;
        if (auxs != null) {
            toJoinVerb = auxs;
        } else {
            toJoinVerb = new String[0];
        }

        toJoinVerb = ArrayUtils.add(toJoinVerb, relations.get(index).word());

        if (prt != null) {
            toJoinVerb = ArrayUtils.add(toJoinVerb, prt);
        }

        String verb = String.join(" ", toJoinVerb);

        Map<String, Object> compsInfo = new HashMap<>();
        compsInfo.put("subj", subjs);

        String[] comps = processComps(
                relations, index, context, engine, compsInfo);

        processCcomp(relations, index, context, engine, compsInfo);

        processIobj(relations, index, context, engine, info);

        processOblique(relations, index, context, engine, info);

        processAdvs(relations, index, context, engine, info);

        processIgnorables(relations, index, context, engine, info);

        processWhats(relations, index, context, engine, new HashMap<>());

        processVmods(relations, index, context, engine, info);

        this.subjs = subjs;
        this.auxs = auxs;

        // emit propositions
        int[] propIds = new int[0];
        if (relations.get(index).rel().toLowerCase()
                .matches("xcomp|ccomp|adpcomp|csubj")) {

            if (relations.get(index).tag().equalsIgnoreCase("VBG")) {
                if (comps.length > 0) {
                    propIds = emitPropositions(
                            verb, subjs, comps, engine, relations.get(index));
                }

                Map<String, Object> ret = new HashMap<>();
                ret.put("return_value", relations.get(index).word());
                ret.put("prop_ids", propIds);
                return ret;
            } else {
                propIds = emitPropositions(
                        verb, subjs, comps, engine, relations.get(index));

                Map<String, Object> ret = new HashMap<>();
                ret.put("return_value", relations.get(index).word());
                ret.put("prop_ids", propIds);
                return ret;
            }
        } else {
            propIds = emitPropositions(
                    verb, subjs, comps, engine, relations.get(index));

            Map<String, Object> ret = new HashMap<>();
            ret.put("return_value", relations.get(index).word());
            ret.put("prop_ids", propIds);
            return ret;
        }
    }

    /**
     * Handle copular verbs with NP complements.
     *
     * @param relations  {@link List} the list of relations in a sentence
     * @param index {@code int} the index of the relation to be processed
     * @param context {@link List} a list of indices representing the path from
     *                             the TOP node to the current one
     * @param engine {@link Engine} the engine that is running the analysis
     * @param info {@link Map} a dictionary containing already parsed
     *                         contextual information
     * @return {@link Map} the result of processing a copular verb
     */
    protected Map<String, Object> handleCopWithNp(
            List<Relation> relations, int index, int[] context,
            Engine engine, Map<String, Object> info) {

        Subject subjs = processSubj(
                relations, index, context, engine, info);

        int[] newContext = ArrayUtils.add(context, index);

        int[] copIndices = Relation
                .getChildrenWithDep("cop", relations, index);
        String cop;
        if (copIndices.length > 0) {
            cop = engine.analyze(
                    relations, copIndices[0], newContext, new HashMap<>());
        } else {
            Map<String, Object> ret = new HashMap<>();
            ret.put("prop_ids", new int[0]);
            return ret;
        }

        String[] auxs = processAuxs(
                relations, index, context, engine, info);

        String verb = String.join(" ", ArrayUtils.add(auxs, cop));

        processIgnorables(relations, index, context, engine, info);

        Map<String, Object> thisOut = new NounPhraseRuleset().extract(
                relations, index, context, engine, info);

        if (thisOut == null) {
            Map<String, Object> ret = new HashMap<>();
            ret.put("prop_ids", new int[0]);
            return ret;
        }

        // TODO handle cc/conj and preconj
        String[] complms = (String[]) thisOut.get("return_list");

        int[] propIds = new int[0];
        for (String subj: subjs.getReturnList()) {

            for (String compl: complms) {
                int propId = engine.emit(
                        new Tuple(verb, subj, compl), "P");
                propIds = ArrayUtils.add(propIds, propId);
            }
        }

        this.subjs = subjs;
        this.auxs = auxs;

        Map<String, Object> ret = new HashMap<>();
        ret.put("prop_ids", propIds);
        ret.put("this", thisOut);

        return ret;
    }

    /**
     * Handle copular verbs with AdjP complements.
     *
     * @param relations  {@link List} the list of relations in a sentence
     * @param index {@code int} the index of the relation to be processed
     * @param context {@link List} a list of indices representing the path from
     *                             the TOP node to the current one
     * @param engine {@link Engine} the engine that is running the analysis
     * @param info {@link Map} a dictionary containing already parsed
     *                         contextual information
     * @return {@link Map} the result of processing a copular verb with AdjP
     * complements.
     */
    protected Map<String, Object> handleCopWithAdjp(
            List<Relation> relations, int index, int[] context,
            Engine engine, Map<String, Object> info) {

        Subject subjs = processSubj(
                relations, index, context, engine, info);

        int[] newContext = ArrayUtils.add(context, index);

        int[] copIndices = Relation
                .getChildrenWithDep("cop", relations, index);

        String cop;
        if (copIndices.length > 0) {
            cop = engine.analyze(
                    relations, copIndices[0], newContext, new HashMap<>());
        } else {
            Map<String, Object> ret = new HashMap<>();
            ret.put("prop_ids", new int[0]);
            return ret;
        }

        String[] auxs = processAuxs(
                relations, index, context, engine, info);

        String[] toJoinVerb = auxs;
        if (cop != null) {
            toJoinVerb = ArrayUtils.add(auxs, cop);
        }

        String verb = String.join(" ", toJoinVerb);

        processIgnorables(relations, index, context, engine, info);

        processNmods(relations, index, context, engine, info);

        String[] thisOut = new AdjectivalPhraseRuleset().extract(
                relations, index, context, engine, info);

        int[] propIds = new int[0];
        for (String subj: subjs.getReturnList()) {

            for (String word: thisOut) {
                int propId = engine.emit(
                        new Tuple(verb, subj, word), "P");
                propIds = ArrayUtils.add(propIds, propId);
            }
        }

        this.subjs = subjs;
        this.auxs = auxs;

        Map<String, Object> ret = new HashMap<>();
        ret.put("prop_ids", propIds);

        return ret;
    }
}
