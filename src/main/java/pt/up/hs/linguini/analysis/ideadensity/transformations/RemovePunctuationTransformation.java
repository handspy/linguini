package pt.up.hs.linguini.analysis.ideadensity.transformations;

import pt.up.hs.linguini.Config;
import pt.up.hs.linguini.analysis.ideadensity.Relation;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Removes relations in place. This function does NOT change the head of
 * eventual relations that point to a removed relation; these head updates
 * have to be done elsewhere.
 *
 * @author José Carlos Paiva <code>josepaiva94@gmail.com</code>
 */
public class RemovePunctuationTransformation extends BaseTransformation {

    public RemovePunctuationTransformation(Config config) {
        super(config);
    }

    @Override
    public void transform(List<Relation> relations) {
        Set<Integer> addressesToRemove = new HashSet<>();
        for (Relation relation: relations) {
            if (relation.rel().equalsIgnoreCase("punct")) {
                addressesToRemove.add(relation.address());
            }
        }

        Transformations.deleteAddresses(relations, addressesToRemove);
    }
}
