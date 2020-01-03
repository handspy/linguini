package pt.up.hs.linguini.analysis.ideadensity.rulesets.misc;

import pt.up.hs.linguini.analysis.ideadensity.Engine;
import pt.up.hs.linguini.analysis.ideadensity.Relation;
import pt.up.hs.linguini.analysis.ideadensity.rulesets.Ruleset;
import pt.up.hs.linguini.analysis.ideadensity.utils.Tuple;
import pt.up.hs.linguini.utils.ArrayUtils;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

/**
 * Prepositional phrases always generate new propositions, according to
 * Chand et al.'s manual.
 *
 * @author José Carlos Paiva <code>josepaiva94@gmail.com</code>
 */
public class PrepRuleset extends Ruleset<Void> {

    public PrepRuleset() {
        super("prep");
    }

    @Override
    public Void extract(
            List<Relation> relations, int index, int[] context,
            Engine engine, Map<String, Object> info) {

        int[] newContext = ArrayUtils.add(context, index);

        // pobj
        int[] pobjIndices = Relation
                .getChildrenWithDep("pobj", relations, index);
        if (pobjIndices.length > 0) {
            Map<String, Object> pobjs = engine.analyze(
                    relations, pobjIndices[0], newContext, new HashMap<>());
            if (pobjs != null) {
                int[] emittedPropIds = new int[0];
                for (String pobj: (String[]) pobjs.get("return_list")) {
                    int propId = engine.emit(
                            new Tuple(relations.get(index).word() + " " + pobj),
                            "M"
                    );
                    emittedPropIds = ArrayUtils.add(emittedPropIds, propId);
                }
                int[] idsForPreconj = (int[]) pobjs.get("ids_for_preconj");
                if (idsForPreconj.length > 0) {
                    Arrays.sort(idsForPreconj);
                    int[] finalEmittedPropIds = emittedPropIds;
                    int[] indices = IntStream.range(0, emittedPropIds.length)
                            .filter(i -> Arrays.binarySearch(idsForPreconj, i) != -1)
                            .map(i -> finalEmittedPropIds[i])
                            .toArray();
                    Tuple proposition = new Tuple((String) pobjs.get("cc"));
                    Arrays.stream(indices).forEachOrdered(proposition::add);
                    engine.emit(proposition, "C");
                }
            }
        }

        // pcomp
        int[] pcompIndices = Relation
                .getChildrenWithDep("pcomp", relations, index);
        if (pcompIndices.length > 0) {
            String pcomp = (String) ((Map) engine.analyze(
                    relations, pcompIndices[0], newContext, new HashMap<>()))
                    .get("return_value");
            if (pcomp != null) {
                engine.emit(new Tuple(relations.get(index).word() + ' ' + pcomp), "M");
            }
        }

        return null;
    }
}
