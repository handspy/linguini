package pt.up.hs.linguini.analysis.ideadensity.transformations;

import pt.up.hs.linguini.Config;
import pt.up.hs.linguini.analysis.ideadensity.Relation;

import java.util.List;

/**
 * Transforms NN into NN-join if both words start with capital letters.
 *
 * @author José Carlos Paiva <code>josepaiva94@gmail.com</code>
 */
public class TransformNnJoin extends BaseTransformation {

    public TransformNnJoin(Config config) {
        super(config);
    }

    @Override
    public void transform(List<Relation> relations) {

        for (Relation relation: relations) {
            if (relation.rel().equalsIgnoreCase("nn") ||
                    relation.rel().equalsIgnoreCase("flat:name")) {
                if (Character.isUpperCase(relation.word().charAt(0)) &&
                        Character.isUpperCase(
                                relations.get(relation.head()).word().charAt(0))) {
                    relation.rel("nn-join");
                }
            }
        }
    }
}
