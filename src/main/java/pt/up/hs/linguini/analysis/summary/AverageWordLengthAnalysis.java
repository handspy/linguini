package pt.up.hs.linguini.analysis.summary;

import pt.up.hs.linguini.analysis.Analysis;
import pt.up.hs.linguini.models.HasWord;

import java.util.List;

/**
 * Analysis that calculates the average word length.
 *
 * @author José Carlos Paiva <code>josepaiva94@gmail.com</code>
 */
public class AverageWordLengthAnalysis<T extends HasWord>
        implements Analysis<List<T>, Double> {

    @Override
    public Double execute(List<T> tokens) {
        return tokens.parallelStream()
                .mapToInt(t -> t.word().length())
                .average()
                .orElse(-1);
    }
}
