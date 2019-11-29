package pt.up.hs.linguini.analysis;

import pt.up.hs.linguini.exceptions.AnalyzerException;
import pt.up.hs.linguini.filters.PunctuationTokenFilter;
import pt.up.hs.linguini.filters.StopTokenFilter;
import pt.up.hs.linguini.jspell.JSpellWordAnnotator;
import pt.up.hs.linguini.models.Token;
import pt.up.hs.linguini.transformers.LemmaTokenTransformer;
import pt.up.hs.linguini.utils.MathUtils;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Analysis to calculate lexical diversity. Lexical diversity is a measure of
 * how many different words are used in a text.
 *
 * @see "MTLD, vocd-D, and HD-D: A validation study of sophisticated approaches
 * to lexical diversity assessment. Behavior research methods, 42(2), 381-392."
 *
 * @author José Carlos Paiva <code>josepaiva94@gmail.com</code>
 */
public class LexicalDiversityAnalysis implements Analysis<Void, Double> {
    private static final int MINIMUM_TOKENS = 50;
    private static final double DEFAULT_MTLD_THRESHOLD = 0.72;
    private static final int DEFAULT_HDD_SAMPLE_SIZE = 42;

    private Locale locale;

    private Algorithm algorithm;
    private boolean lemmatize;

    private double mtldThreshold = DEFAULT_MTLD_THRESHOLD;
    private int hddSampleSize = DEFAULT_HDD_SAMPLE_SIZE;

    private List<Token> tokens = null;

    private Double result = null;

    public LexicalDiversityAnalysis(Locale locale, Algorithm algorithm, boolean lemmatize) {
        this.locale = locale;
        this.algorithm = algorithm;
        this.lemmatize = lemmatize;
    }

    public LexicalDiversityAnalysis(Locale locale, Algorithm algorithm, boolean lemmatize, double mtldThreshold) {
        this.locale = locale;
        this.algorithm = algorithm;
        this.lemmatize = lemmatize;
        this.mtldThreshold = mtldThreshold;
    }

    public LexicalDiversityAnalysis(Locale locale, Algorithm algorithm, boolean lemmatize, int hddSampleSize) {
        this.locale = locale;
        this.algorithm = algorithm;
        this.lemmatize = lemmatize;
        this.hddSampleSize = hddSampleSize;
    }

    @Override
    public Analysis<Void, Double> preprocess(
            List<Token> tokens) throws AnalyzerException {

        // 1. remove punctuation
        // 2. remove stop-words
        PunctuationTokenFilter punctuationFilter = new PunctuationTokenFilter();
        StopTokenFilter stopFilter = new StopTokenFilter(locale);
        Stream<Token> tokenStream = tokens.parallelStream()
                .filter(punctuationFilter::accept)
                .filter(stopFilter::accept);

        // 3. lemmatize (if required)
        if (lemmatize) {
            JSpellWordAnnotator wordAnnotator;
            try {
                wordAnnotator = new JSpellWordAnnotator(locale);
            } catch (IOException | URISyntaxException e) {
                throw new AnalyzerException("Could not lemmatize words", e);
            }
            LemmaTokenTransformer transformer = new LemmaTokenTransformer(wordAnnotator);
            tokenStream = tokenStream.map(transformer::transform);
        }

        this.tokens = tokenStream.collect(Collectors.toList());

        return this;
    }

    @Override
    public Analysis<Void, Double> skipPreprocessing(List<Token> tokens, Void v) {
        this.tokens = tokens;
        return this;
    }

    @Override
    public LexicalDiversityAnalysis execute() throws AnalyzerException {

        if (tokens.size() < MINIMUM_TOKENS) {
            throw new AnalyzerException("Cannot calculate lexical" +
                    " diversity in texts with less than " + MINIMUM_TOKENS +
                    " words.");
        }

        switch (algorithm) {
            case HDD:
                result = hdd(tokens, hddSampleSize);
                break;
            case MTLD:
                result = mtld(tokens, mtldThreshold);
                break;
            default:
                throw new AnalyzerException("Unknown algorithm to calculate lexical" +
                        " diversity '" + algorithm + "'.");
        }

        return this;
    }

    @Override
    public Double getResult() {
        return result;
    }

    /**
     * HD-D is an idealized version of voc-D. For more information see McCarthy, P.M.
     * & Jarvis, S. (2007). vocd: A theoretical and empirical evaluation. Language
     * Testing, 24(4), 459-488.
     *
     * @param tokens {@link List} tokens from text
     * @param sampleSize {@code int} sample size
     * @return TTR
     */
    private double hdd(List<Token> tokens, int sampleSize) throws AnalyzerException {
        Map<String, Integer> typeCounts = new HashMap<>();
        for (Token token : tokens) {
            if (typeCounts.containsKey(token.getWord())) {
                typeCounts.put(token.getWord(), typeCounts.get(token.getWord()) + 1);
            } else {
                typeCounts.put(token.getWord(), 1);
            }
        }
        double hdd = 0.0;
        for (String word : typeCounts.keySet()) {
            double contribution = (
                    1.0 - MathUtils.hypergeometric(
                            tokens.size(), sampleSize, typeCounts.get(word), 0)
            ) / sampleSize;
            hdd += contribution;
        }
        return hdd;
    }

    /**
     * MTLD (Measure of Textual Lexical Diversity, or LDAT, Lexical Diversity
     * Assessment Tool) is derived from the average length of continuous text
     * units above a certain Type-Token Ratio.
     *
     * @param tokens {@link List} tokens from text
     * @param threshold {@code double} threshold ratio to increment counter
     * @return TTR
     */
    private double mtld(List<Token> tokens, double threshold) {
        double currentTtr = 1.0;
        Set<String> types = new HashSet<>();
        double factors = 0.0;
        for (Token token : tokens) {
            types.add(token.getWord());
            currentTtr = types.size() / tokens.size();
            if (currentTtr <= threshold) {
                factors += 1;
                currentTtr = 1.0;
                types.clear();
            }
        }
        double excess = 1.0 - currentTtr;
        double excessValue = 1.0 - threshold;
        factors += excess / excessValue;
        if (factors > 0)
            return tokens.size() / factors;
        return -1;
    }

    public enum Algorithm {
        MTLD,
        HDD
    }
}
