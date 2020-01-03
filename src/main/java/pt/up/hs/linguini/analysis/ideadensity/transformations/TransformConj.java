package pt.up.hs.linguini.analysis.ideadensity.transformations;

import pt.up.hs.linguini.Config;
import pt.up.hs.linguini.analysis.ideadensity.Relation;

import java.util.List;

/**
 * Replace conjunction with the head's head and relation.
 *
 * @author José Carlos Paiva <code>josepaiva94@gmail.com</code>
 */
public class TransformConj extends BaseTransformation {

    public TransformConj(Config config) {
        super(config);
    }

    @Override
    public void transform(List<Relation> relations) {
        for (Relation relation: relations) {
            if (relation.rel().equalsIgnoreCase("conj")) {
                Relation head = relations.get(relation.head());
                int newHead = head.head();
                String newRel = head.rel();
                relation.rel(newRel);
                relation.head(newHead);
            }
        }
    }
}
