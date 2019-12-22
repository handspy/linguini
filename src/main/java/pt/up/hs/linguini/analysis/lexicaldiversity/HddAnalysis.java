package pt.up.hs.linguini.analysis.lexicaldiversity;

import pt.up.hs.linguini.analysis.Analysis;
import pt.up.hs.linguini.analysis.exceptions.AnalysisException;
import pt.up.hs.linguini.models.HasWord;
import pt.up.hs.linguini.utils.MathUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * HD-D is an idealized version of voc-D. For more information see McCarthy, P.M.
 * &amp; Jarvis, S. (2007). vocd: A theoretical and empirical evaluation.
 * Language Testing, 24(4), 459-488.
 *
 * @author José Carlos Paiva <code>josepaiva94@gmail.com</code>
 */
public class HddAnalysis<T extends HasWord>
        implements Analysis<List<T>, Double> {
    private static final int MINIMUM_TOKENS = 50;
    private static final int DEFAULT_HDD_SAMPLE_SIZE = 42;

    private int sampleSize;

    public HddAnalysis() {
        this.sampleSize = DEFAULT_HDD_SAMPLE_SIZE;
    }

    public HddAnalysis(int sampleSize) {
        this.sampleSize = sampleSize;
    }

    @Override
    public Double execute(List<T> tokens) throws AnalysisException {

        if (tokens.size() < MINIMUM_TOKENS) {
            throw new AnalysisException("Cannot calculate lexical" +
                    " diversity in texts with less than " + MINIMUM_TOKENS +
                    " words.");
        }

        Map<String, Integer> typeCounts = new HashMap<>();
        for (T token : tokens) {
            if (typeCounts.containsKey(token.word())) {
                typeCounts.put(token.word(), typeCounts.get(token.word()) + 1);
            } else {
                typeCounts.put(token.word(), 1);
            }
        }
        double hdd = 0.0;
        for (String word : typeCounts.keySet()) {
            double contribution = (
                    1.0 - MathUtils.hypergeometric(
                            tokens.size(), sampleSize, typeCounts.get(word), 0)
            ) / (double) sampleSize;
            hdd += contribution;
        }
        return hdd;
    }
}
