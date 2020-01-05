package pt.up.hs.linguini.analysis.ideadensity;

import pt.up.hs.linguini.Config;
import pt.up.hs.linguini.analysis.exceptions.AnalysisException;
import pt.up.hs.linguini.analysis.ideadensity.rulesets.Ruleset;
import pt.up.hs.linguini.analysis.ideadensity.rulesets.adjectival.AcompRuleset;
import pt.up.hs.linguini.analysis.ideadensity.rulesets.adjectival.AdjectivalPhraseRuleset;
import pt.up.hs.linguini.analysis.ideadensity.rulesets.adjectival.AmodRuleset;
import pt.up.hs.linguini.analysis.ideadensity.rulesets.adverbial.AdvmodRuleset;
import pt.up.hs.linguini.analysis.ideadensity.rulesets.atomic.*;
import pt.up.hs.linguini.analysis.ideadensity.rulesets.atomicemitting.DiscourseRuleset;
import pt.up.hs.linguini.analysis.ideadensity.rulesets.atomicemitting.NegRuleset;
import pt.up.hs.linguini.analysis.ideadensity.rulesets.misc.*;
import pt.up.hs.linguini.analysis.ideadensity.rulesets.noun.*;
import pt.up.hs.linguini.analysis.ideadensity.rulesets.verb.*;
import pt.up.hs.linguini.analysis.ideadensity.transformations.RemovePunctuationTransformation;
import pt.up.hs.linguini.analysis.ideadensity.transformations.Transformation;
import pt.up.hs.linguini.analysis.ideadensity.utils.Tuple;
import pt.up.hs.linguini.exceptions.ConfigException;
import pt.up.hs.linguini.utils.Predicates;

import java.util.*;
import java.util.stream.Collectors;

/**
 * An engine is responsible for running the analysis process, by calling the
 * right rulesets and collecting the emitted propositions.
 *
 * @author José Carlos Paiva <code>josepaiva94@gmail.com</code>
 */
public class Engine {

    private Locale locale;
    private Config config;

    private List<Ruleset> rulesets;
    private List<Transformation> transformations;

    // calculated during analysis
    private Map<String, Ruleset> rulesetsDict;
    private List<Proposition> props;

    public Engine() throws AnalysisException {
        this(Locale.getDefault());
    }

    public Engine(Locale locale) throws AnalysisException {
        this.locale = locale;
        try {
            this.config = Config.getInstance(locale);
        } catch (ConfigException e) {
            throw new AnalysisException("Failed to load configuration properties", e);
        }
        this.rulesets = Arrays.asList(
                // adjectival
                new AdjectivalPhraseRuleset(),
                new AcompRuleset(),
                new AmodRuleset(),

                // adverbial
                new AdvmodRuleset(),

                // atomic
                new AdpRuleset(),
                new AuxpassRuleset(),
                new AuxRuleset(),
                new CcRuleset(),
                new CaseRuleset(),
                new ComplmRuleset(),
                new CopRuleset(),
                new MarkRuleset(),
                new NumberRuleset(),
                new PreconjRuleset(),
                new PredetRuleset(),
                new PrtRuleset(),

                // atomic emitting
                new DiscourseRuleset(),
                new NegRuleset(),

                // noun
                new AdpobjRuleset(),
                new ApposRuleset(),
                new AttrRuleset(),
                new DobjRuleset(),
                new IobjRuleset(),
                new ObjRuleset(),
                new OblRuleset(),
                new NmodRuleset(),
                new NsubjpassRuleset(),
                new NsubjRuleset(),
                new PossRuleset(),
                new TmodRuleset(),

                // verb
                new AdpcompRuleset(),
                new AdvclRuleset(),
                new CcompRuleset(),
                new CsubjRuleset(),
                new ParataxisRuleset(),
                new RcmodRuleset(),
                new AclRelclRuleset(),
                new VmodRuleset(),
                new XcompRuleset(),

                // misc
                new ConjRuleset(),
                new DetRuleset(),
                new NnJoinRuleset(),
                new NnRuleset(),
                new NumRuleset(),
                new PrepRuleset(),
                new QuantmodRuleset(),
                new TopRuleset(),
                new WhatRuleset(),

                new RootRuleset()
        );
        this.transformations = Arrays.asList(
                new RemovePunctuationTransformation(config)/*,
                new FixAdjectiveRepetition(config),
                new FixAdverbRepetition(config),
                new FixReflexivePronouns(config),
                new FixXcompAttributions(config),
                new TransformConj(config),
                new TransformNnJoin(config)*/
        );
    }

    public Engine(
            Locale locale,
            List<Ruleset> rulesets,
            List<Transformation> transformations)
            throws AnalysisException {
        this.locale = locale;
        this.rulesets = rulesets;
        this.transformations = transformations;

        try {
            this.config = Config.getInstance(locale);
        } catch (ConfigException e) {
            throw new AnalysisException("Failed to load configuration properties", e);
        }
    }

    /**
     * Mark a relation as processed.
     *
     * @param relations {@link List} list of relations
     * @param index     {@code int} index of the relation
     */
    public static void markProcessed(List<Relation> relations, int index) {
        relations.get(index).setProcessed(true);
    }

    /**
     * Get only unprocessed relations from a list of relations.
     *
     * @param relations {@link List} list of relations
     * @return {@link List} unprocessed relations from a list of
     * relations
     */
    public static List<Relation> getUnprocessedRelations(
            List<Relation> relations) {

        return relations.parallelStream()
                .filter(Predicates.not(Relation::isProcessed))
                .collect(Collectors.toList());
    }

    public String getConfigValue(String prop) {
        return config.get(prop);
    }

    /**
     * Emit a new proposition, storing it in this instance's 'props' attribute.
     *
     * @param content {@link List} the proposition content.
     * @return {@code int} the number of propositions already stored.
     */
    public int emit(Tuple content) {
        props.add(new Proposition(content, "PROP"));
        return props.size();
    }

    /**
     * Emit a new proposition, storing it in this instance's 'props' attribute.
     *
     * @param content {@link Tuple} the proposition content.
     * @param kind    {@link String} the kind of proposition.
     * @return {@code int} the number of propositions already stored.
     */
    public int emit(Tuple content, String kind) {
        props.add(new Proposition(content, kind));
        return props.size();
    }

    /**
     * Analyze a sentence, using this instance's ruleset set.
     *
     * @param <T>       type of object returned by analysis
     * @param relations {@link List} the relations in a sentence
     * @param index     {@code int} the index of the relation to be analyzed
     * @param context   {@link int[]} the path from the TOP relation to the
     *                  current one
     * @param info      {@link Map} a dictionary containing already parsed
     *                  contextual information
     * @return {@link List} value of the corresponding ruleset's extract
     * method
     */
    public <T> T analyze(
            List<Relation> relations, int index, int[] context,
            Map<String, Object> info) {

        if (context == null) {
            context = new int[0];
        }

        if (info == null) {
            info = new HashMap<>();
        }

        // Clear results from previous executions, apply transformations,
        // and prepare for starting.
        if (relations.get(index).rel().equalsIgnoreCase("TOP")) {

            for (Transformation transformation : transformations) {
                transformation.transform(relations);
            }

            props = new ArrayList<>();
            for (Relation relation : relations) {
                relation.setProcessed(false);
            }

            buildRulesetsDict(relations);
        }

        Ruleset<T> ruleset = rulesetsDict.get(relations.get(index).rel());

        T value = null;
        if (ruleset != null) {
            value = ruleset.extract(relations, index, context, this, info);
        }

        markProcessed(relations, index);

        return value;
    }

    /**
     * Create a dictionary associating relation labels to their corresponding
     * ruleset instance.
     *
     * @param relations {@link List<Relation>} the list of relations in a sentence
     */
    private void buildRulesetsDict(List<Relation> relations) {

        rulesetsDict = new HashMap<>();
        for (Relation relation : relations) {

            for (Ruleset rs : rulesets) {
                if (rs.applies(relation.rel())) {
                    rulesetsDict.put(relation.rel(), rs);
                    break;
                }
            }
        }
    }

    public List<Proposition> getProps() {
        return props;
    }
}
