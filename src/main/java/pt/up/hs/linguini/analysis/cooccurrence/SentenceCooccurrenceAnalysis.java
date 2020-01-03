package pt.up.hs.linguini.analysis.cooccurrence;

import pt.up.hs.linguini.models.HasWord;
import pt.up.hs.linguini.pipeline.Step;
import pt.up.hs.linguini.utils.UnorderedPair;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Analysis to find word co-occurrences' value in a single sentence.
 *
 * @author José Carlos Paiva <code>josepaiva94@gmail.com</code>
 */
public class SentenceCooccurrenceAnalysis<T extends HasWord>
        implements Step<List<T>, Map<UnorderedPair<String>, Double>> {
    private static final int DEFAULT_WINDOW_SIZE = 5;

    private int windowSize;

    public SentenceCooccurrenceAnalysis() {
        this(DEFAULT_WINDOW_SIZE);
    }

    public SentenceCooccurrenceAnalysis(int windowSize) {
        this.windowSize = windowSize;
    }

    @Override
    public Map<UnorderedPair<String>, Double> execute(List<T> tokens) {

        Map<UnorderedPair<String>, Double> cooccurrenceValues = new HashMap<>();

        for (int center_i = 0; center_i < tokens.size(); center_i++) {

            T center_id = tokens.get(center_i);

            int start = Math.max(0, center_i - windowSize);

            List<T> context_ids = tokens.subList(start, center_i);
            int context_len = context_ids.size();

            for (int left_i = 0; left_i < context_len; left_i++) {

                T left_id = context_ids.get(left_i);

                UnorderedPair<String> pair1 = new UnorderedPair<>(
                        left_id.word(), center_id.word());
                double xij = cooccurrenceValues.getOrDefault(pair1, 0.0);
                xij += 1.0 / (context_len - left_i);
                cooccurrenceValues.put(pair1, xij);
            }
        }

        return cooccurrenceValues;
    }
}
