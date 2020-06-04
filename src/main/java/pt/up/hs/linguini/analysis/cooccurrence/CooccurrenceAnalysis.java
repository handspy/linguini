package pt.up.hs.linguini.analysis.cooccurrence;

import pt.up.hs.linguini.exceptions.LinguiniException;
import pt.up.hs.linguini.models.HasWord;
import pt.up.hs.linguini.pipeline.Step;
import pt.up.hs.linguini.utils.UnorderedPair;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Analysis to find word co-occurrences' value.
 *
 * @author José Carlos Paiva <code>josepaiva94@gmail.com</code>
 */
public class CooccurrenceAnalysis<T extends HasWord>
        implements Step<List<List<T>>, List<Cooccurrence>> {
    private static final double DEFAULT_COOCCURRENCE_THRESHOLD = 2.0;

    private final double threshold;

    private final SentenceCooccurrenceAnalysis<T> step;

    public CooccurrenceAnalysis() {
        this(DEFAULT_COOCCURRENCE_THRESHOLD);
    }

    public CooccurrenceAnalysis(double threshold) {
        this.threshold = threshold;
        this.step = new SentenceCooccurrenceAnalysis<>();
    }

    public CooccurrenceAnalysis(double threshold, int windowSize) {
        this.threshold = threshold;
        this.step = new SentenceCooccurrenceAnalysis<>(windowSize);
    }

    @Override
    public List<Cooccurrence> execute(List<List<T>> sentences) throws LinguiniException {

        final Map<UnorderedPair<String>, Double> cooccurrenceValues = new HashMap<>();

        for (List<T> sentence: sentences) {
            Map<UnorderedPair<String>, Double> values = step.execute(sentence);
            for (Map.Entry<UnorderedPair<String>, Double> entry: values.entrySet()) {
                double v = cooccurrenceValues
                        .getOrDefault(entry.getKey(), 0.0);
                cooccurrenceValues.put(entry.getKey(), v + entry.getValue());
            }
        }

        List<Cooccurrence> coocurrences = new ArrayList<>();
        for (Map.Entry<UnorderedPair<String>, Double> entry :
                cooccurrenceValues.entrySet()) {

            if (entry.getValue() < threshold)
                continue;

            Cooccurrence c = new Cooccurrence(entry.getKey().getFirst(),
                    entry.getKey().getSecond(),
                    entry.getValue());

            coocurrences.add(c);
        }

        return coocurrences;
    }
}
