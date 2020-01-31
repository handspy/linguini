package pt.up.hs.linguini.analysis.lexicaldiversity;

import pt.up.hs.linguini.analysis.Analysis;
import pt.up.hs.linguini.models.HasWord;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Calculate base TTR.
 *
 * @author José Carlos Paiva <code>josepaiva94@gmail.com</code>
 */
public class BaseTtrAnalysis<T extends HasWord>
        implements Analysis<List<T>, Double> {

    public BaseTtrAnalysis() {
    }

    @Override
    public Double execute(List<T> tokens) {
        Set<String> types = new HashSet<>();
        for (T el : tokens) {
            types.add(el.word());
        }
        /*System.out.println("Tokens: " + String.join(", ", tokens.stream().map(T::word).collect(Collectors.toList())));
        System.out.println("Types: " + String.join(", ", types));*/
        return (double) types.size() / tokens.size();
    }
}
