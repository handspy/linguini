package pt.up.hs.linguini.analysis.ideadensity.transformations;

import pt.up.hs.linguini.analysis.ideadensity.Relation;
import pt.up.hs.linguini.utils.ArrayUtils;

import java.util.*;

/**
 * Utilities to deal with transformations on relations.
 *
 * @author José Carlos Paiva <code>josepaiva94@gmail.com</code>
 */
public class Transformations {

    public static void deleteAddresses(
            List<Relation> relations, Set<Integer> addresses) {

        // sort indices in reverse order
        List<Integer> addressesList = new ArrayList<>(addresses);
        addressesList.sort(Collections.reverseOrder());

        for (int address: addressesList) {
            relations.removeIf(r -> r.address() == address);

            for (Relation rel: relations) {
                if (rel.head() != null) {
                    if (rel.head() >= address) {
                        rel.head(rel.head() - 1);
                    }
                }
                if (rel.address() >= address) {
                    rel.address(rel.address() - 1);
                }
                rel.deps(new int[0]);
            }
        }

        for (Relation rel: relations) {
            if (rel.head() != null) {
                Optional<Relation> targetRel = relations.parallelStream()
                        .filter(r -> r.address() == rel.head())
                        .findFirst();
                if (!targetRel.isPresent()) {
                    continue;
                }
                targetRel.get().deps(
                        ArrayUtils.add(targetRel.get().deps(), rel.address())
                );
            }
        }

        for (Relation rel: relations) {
            Arrays.sort(rel.deps());
        }
    }
}
