package pt.up.hs.linguini.analysis.ideadensity.transformations;

import pt.up.hs.linguini.Config;
import pt.up.hs.linguini.analysis.ideadensity.Relation;
import pt.up.hs.linguini.utils.ArrayUtils;

import java.util.Arrays;
import java.util.List;

/**
 * Handles reflexive pronouns following nouns. Here, we connect the pronoun to
 * the previous noun as an adjectival modifier.
 *
 * @author José Carlos Paiva <code>josepaiva94@gmail.com</code>
 */
public class FixReflexivePronouns extends BaseTransformation {

    public FixReflexivePronouns(Config config) {
        super(config);
    }

    @Override
    public void transform(List<Relation> relations) {

        String reflexivePronouns = config.get("reflexivePronouns");

        for (int i = 0; i < relations.size(); i++) {
            Relation relation = relations.get(i);

            if (relation.tag().equalsIgnoreCase("PRON") &&
                    relation.word().matches("^" + reflexivePronouns + "$") &&
                    relations.get(i - 1).tag().matches("NOUN|PROPN")) {

                relations.get(relation.head()).deps(
                        ArrayUtils.remove(relations.get(relation.head()).deps(), i)
                );
                relation.head(i - 1);
                relation.rel("amod");
                relations.get(i - 1).deps(ArrayUtils.add(relations.get(i - 1).deps(), i));
                Arrays.sort(relations.get(i - 1).deps());
            }
        }
    }
}
