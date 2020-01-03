package pt.up.hs.linguini.analysis.ideadensity.transformations;

import pt.up.hs.linguini.Config;
import pt.up.hs.linguini.analysis.ideadensity.Relation;
import pt.up.hs.linguini.utils.ArrayUtils;

import java.util.Arrays;
import java.util.List;

/**
 * Turns xcomp relations with no cop children to 'what'.
 *
 * @author José Carlos Paiva <code>josepaiva94@gmail.com</code>
 */
public class FixXcompAttributions extends BaseTransformation {

    public FixXcompAttributions(Config config) {
        super(config);
    }

    @Override
    public void transform(List<Relation> relations) {

        for (int i = 0; i < relations.size(); i++) {
            Relation relation = relations.get(i);
            if (relation.rel().equalsIgnoreCase("xcomp") &&
                    relation.tag().matches("NOUN|PROPN|ADJ")) {

                int[] copIndices = Relation
                        .getChildrenWithDep("cop", relations, i);

                if (copIndices.length == 0) {
                    relation.rel("what");

                    // the subject of this xcomp should become the direct object of
                    // the main verb.

                    int[] xcompSubj = Relation
                            .getChildrenWithDep("nsubj", relations, i);

                    if (xcompSubj.length > 0) {
                        relations.get(xcompSubj[0]).head(relation.head());
                        relations.get(xcompSubj[0]).rel("obj");
                        relations.get(relation.head()).deps(
                                ArrayUtils.add(
                                        relations.get(relation.head()).deps(),
                                        relations.get(xcompSubj[0]).address()
                                )
                        );
                        Arrays.sort(relations.get(relation.head()).deps());
                        relation.deps(
                                ArrayUtils.remove(
                                        relation.deps(),
                                        relations.get(xcompSubj[0]).address()
                                )
                        );
                    }
                }
            }
        }
    }
}
