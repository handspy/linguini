package pt.up.hs.linguini.analysis.ideadensity.transformations;

import pt.up.hs.linguini.Config;
import pt.up.hs.linguini.analysis.ideadensity.Relation;
import pt.up.hs.linguini.utils.ArrayUtils;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * Handles adjective repetition as intensifier (e.g., she was gone a long long
 * time.). In this case, the first adjective is turned into an adverb and
 * connected to the second adjective.
 *
 * @author José Carlos Paiva <code>josepaiva94@gmail.com</code>
 */
public class FixAdjectiveRepetition extends BaseTransformation {

    public FixAdjectiveRepetition(Config config) {
        super(config);
    }

    @Override
    public void transform(List<Relation> relations) {

        for (int i = 0; i < relations.size(); i++) {
            Relation relation = relations.get(i);

            if (relation.tag().equalsIgnoreCase("ADJ")) {
                if (i + 1 < relations.size() &&
                    relations.get(i + 1).tag().equalsIgnoreCase("ADJ") &&
                    relations.get(i + 1).word().equalsIgnoreCase(relation.word()) &&
                    Objects.equals(relations.get(i + 1).head(), relation.head()) &&
                    relations.get(i + 1).rel().equalsIgnoreCase(relation.rel())) {

                    relation.tag("ADV");
                    relation.head(i + 1);
                    relation.rel("advmod");
                    relations.get(i + 1).deps(ArrayUtils.add(relations.get(i + 1).deps(), i));
                    Arrays.sort(relations.get(i + 1).deps());
                }
            }
        }
    }
}
