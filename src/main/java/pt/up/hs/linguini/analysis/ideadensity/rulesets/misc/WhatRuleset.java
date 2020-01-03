package pt.up.hs.linguini.analysis.ideadensity.rulesets.misc;

import pt.up.hs.linguini.analysis.ideadensity.Engine;
import pt.up.hs.linguini.analysis.ideadensity.Relation;
import pt.up.hs.linguini.analysis.ideadensity.rulesets.Ruleset;
import pt.up.hs.linguini.analysis.ideadensity.rulesets.adjectival.AdjectivalPhraseRuleset;
import pt.up.hs.linguini.analysis.ideadensity.rulesets.noun.NounPhraseRuleset;
import pt.up.hs.linguini.analysis.ideadensity.utils.Tuple;

import java.util.List;
import java.util.Map;

/**
 * A ruleset that processes the 'what' relation.
 *
 * @author José Carlos Paiva <code>josepaiva94@gmail.com</code>
 */
public class WhatRuleset extends Ruleset<Void> {

    public WhatRuleset() {
        super("what");
    }

    @Override
    public Void extract(
            List<Relation> relations, int index, int[] context,
            Engine engine, Map<String, Object> info) {

        if (relations.get(index).tag().toUpperCase().matches("NN|NNS|NNP|NNPS")) {
            NounPhraseRuleset npr = new NounPhraseRuleset();
            Map<String, Object> ro = npr.extract(
                    relations, index, context, engine, info);
            for (String noun: (String[]) ro.get("return_list")) {
                engine.emit(new Tuple(noun), "WHAT");
            }
        } else if (relations.get(index).tag().equalsIgnoreCase("JJ")) {
            AdjectivalPhraseRuleset apr = new AdjectivalPhraseRuleset();
            String[] adjs = apr.extract(
                    relations, index, context, engine, info);
            for (String adj: adjs) {
                engine.emit(new Tuple(adj), "WHAT");
            }
        } else {
            // in case something weird happens, we just emit the word
            engine.emit(new Tuple(relations.get(index).word()), "WHAT");
        }

        return null;
    }
}
