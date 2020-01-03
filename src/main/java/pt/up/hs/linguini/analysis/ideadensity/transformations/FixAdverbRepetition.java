package pt.up.hs.linguini.analysis.ideadensity.transformations;

import pt.up.hs.linguini.Config;
import pt.up.hs.linguini.analysis.ideadensity.Relation;
import pt.up.hs.linguini.utils.ArrayUtils;

import java.util.Arrays;
import java.util.List;

/**
 * Handles adverb repetition as intensifier (e.g., he is very very sick). In
 * this case, we make sure the first adverb is connected to the second, not to
 * the following word (usually an adjective).
 *
 * @author José Carlos Paiva <code>josepaiva94@gmail.com</code>
 */
public class FixAdverbRepetition extends BaseTransformation {

    public FixAdverbRepetition(Config config) {
        super(config);
    }

    @Override
    public void transform(List<Relation> relations) {

        for (int i = 0; i < relations.size(); i++) {
            Relation relation = relations.get(i);

            if (relation.tag().equalsIgnoreCase("ADV")) {
                if (i + 1 < relations.size() &&
                    relations.get(i + 1).tag().equalsIgnoreCase("ADV") &&
                    relations.get(i + 1).word().equalsIgnoreCase(relation.word()) &&
                    relations.get(i + 1).rel().equalsIgnoreCase(relation.rel()) &&
                    relation.head() != i + 1) {

                    relation.head(i + 1);
                    relations.get(i + 1).deps(ArrayUtils.add(relations.get(i + 1).deps(), i));
                    Arrays.sort(relations.get(i + 1).deps());
                }
            }
        }
    }
}
