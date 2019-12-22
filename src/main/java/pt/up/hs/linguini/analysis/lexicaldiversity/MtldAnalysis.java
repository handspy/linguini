package pt.up.hs.linguini.analysis.lexicaldiversity;

import pt.up.hs.linguini.analysis.Analysis;
import pt.up.hs.linguini.analysis.exceptions.AnalysisException;
import pt.up.hs.linguini.models.HasWord;
import pt.up.hs.linguini.models.Token;

import java.util.*;

/**
 * MTLD (Measure of Textual Lexical Diversity, or LDAT, Lexical Diversity
 * Assessment Tool) is derived from the average length of continuous text
 * units above a certain Type-Token Ratio.
 *
 * @author José Carlos Paiva <code>josepaiva94@gmail.com</code>
 */
public class MtldAnalysis<T extends HasWord> implements Analysis<List<T>, Double> {
    private static final int MINIMUM_TOKENS = 50;
    private static final double DEFAULT_MTLD_THRESHOLD = 0.72;

    private double threshold;

    public MtldAnalysis() {
        this.threshold = DEFAULT_MTLD_THRESHOLD;
    }

    public MtldAnalysis(double threshold) {
        this.threshold = threshold;
    }

    @Override
    public Double execute(List<T> tokens)  throws AnalysisException {

        if (tokens.size() < MINIMUM_TOKENS) {
            throw new AnalysisException("Cannot calculate lexical" +
                    " diversity in texts with less than " + MINIMUM_TOKENS +
                    " words.");
        }

        List<T> reversed = new ArrayList<>(tokens);
        Collections.reverse(reversed);
        return (calculateMtld(tokens, threshold) +
                calculateMtld(reversed, threshold)) / 2;
    }

    private double calculateMtld(List<T> tokens, double threshold) {
        double currentTtr = 1.0;
        Set<String> types = new HashSet<>();
        double factors = 0.0;
        int tokenCount = 0;
        for (T token : tokens) {
            types.add(token.word());
            currentTtr = (double) types.size() / (++tokenCount);
            if (currentTtr <= threshold) {
                factors += 1;
                tokenCount = 0;
                currentTtr = 1.0;
                types.clear();
            }
        }
        double excess = 1.0 - currentTtr;
        double excessValue = 1.0 - threshold;
        factors += excess / excessValue;
        if (factors > 0)
            return (double) tokens.size() / factors;
        return -1;
    }
}
