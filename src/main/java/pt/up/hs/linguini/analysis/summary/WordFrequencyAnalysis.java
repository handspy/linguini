package pt.up.hs.linguini.analysis.summary;

import pt.up.hs.linguini.analysis.Analysis;
import pt.up.hs.linguini.models.HasWord;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Analysis that calculates the word frequency.
 *
 * @author José Carlos Paiva <code>josepaiva94@gmail.com</code>
 */
public class WordFrequencyAnalysis<T extends HasWord>
        implements Analysis<List<T>, Map<String, Integer>> {

    @Override
    public Map<String, Integer> execute(List<T> tokens) {
        return tokens.parallelStream()
                .collect(
                        Collectors
                                .toConcurrentMap(
                                        HasWord::word,
                                        t -> 1,
                                        Integer::sum)
                );
    }
}
