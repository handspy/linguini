package pt.up.hs.linguini.analysis;

import pt.up.hs.linguini.analysis.exceptions.AnalysisException;
import pt.up.hs.linguini.filtering.PunctuationTokenFilter;
import pt.up.hs.linguini.filtering.StopTokenFilter;
import pt.up.hs.linguini.filtering.WhitespaceTokenFilter;
import pt.up.hs.linguini.filtering.exceptions.FilteringException;
import pt.up.hs.linguini.lemmatization.Lemmatizer;
import pt.up.hs.linguini.lemmatization.exceptions.LemmatizationException;
import pt.up.hs.linguini.models.AnnotatedToken;
import pt.up.hs.linguini.models.Token;
import pt.up.hs.linguini.pipeline.BatchStep;
import pt.up.hs.linguini.pipeline.Step;
import pt.up.hs.linguini.pos.PoSTagger;
import pt.up.hs.linguini.tokenization.Tokenizer;
import pt.up.hs.linguini.tokenization.exceptions.TokenizationException;
import pt.up.hs.linguini.utils.MathUtils;

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
public class LexicalDiversityAnalysis/* implements Analysis<String, Double>*/ {
    /*private static final int MINIMUM_TOKENS = 50;
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
    public Double execute(String text) throws FilteringException, LemmatizationException, TokenizationException {

        Step<String, List<Token>> preprocessPipeline =
                // 1. tokenize text
                new Tokenizer(locale)
                        // 2. remove whitespaces
                        .pipe(new WhitespaceTokenFilter<>());

        if (lemmatize) {
            preprocessPipeline
                    .pipe(
                            // 3. PoS Tagging
                            new PoSTagger(locale)
                                // 4. remove punctuation
                                .pipe(new PunctuationTokenFilter<>())
                                // 5. remove stopwords
                                .pipe(new StopTokenFilter<>(locale))
                                // 6. lemmatize text
                                .pipe(new BatchStep<>(new Lemmatizer(locale)))
                    );
        } else {
            preprocessPipeline
                    // 3. remove punctuation
                    .pipe(new PunctuationTokenFilter<>())
                    // 4. remove stopwords
                    .pipe(new StopTokenFilter<>(locale));
        }

        Step<String, Double> pipeline = preprocessPipeline.pipe()

        return null;
    }

    @Override
    public Analysis<Void, Double> preprocess(
            List<Token> tokens) throws AnalysisException {

        // 1.
        WhitespaceTokenFilter whitespaceFilter = new WhitespaceTokenFilter();
        List<Token> nonWhitespaceTokens = tokens
                .parallelStream()
                .filter(whitespaceFilter::accept)
                .collect(Collectors.toList());

        // 2. annotate tokens with PoS tag (if required)
        // 3. remove punctuation
        // 4. remove stop-words
        PunctuationTokenFilter punctuationFilter = new PunctuationTokenFilter();
        StopTokenFilter stopFilter = new StopTokenFilter(locale);
        if (lemmatize) {
            PoSTagger poSTagger = new PoSTagger(locale);
            List<AnnotatedToken<String>> taggedTokens =
                    poSTagger.tag(nonWhitespaceTokens);

            System.out.println(taggedTokens.stream()
                    .map(tt -> tt.getToken().getWord() + "_" + tt.getInfo())
                    .collect(Collectors.joining(" ")));

            Stream<AnnotatedToken<String>> tokenStream = taggedTokens
                    .parallelStream()
                    .filter(t -> punctuationFilter.accept(t.getToken()))
                    .filter(t -> stopFilter.accept(t.getToken()));

            try {
                final Lemmatizer lemmatizer = new Lemmatizer(locale);
                tokenStream = tokenStream
                        .peek(at -> lemmatizer.lemmatize(
                                at.getToken(), at.getInfo()
                        ));
            } catch (LemmatizationException e) {
                throw new AnalysisException("Could not initialize lemmatizer", e);
            }

            this.tokens = tokenStream
                    .map(AnnotatedToken::getToken)
                    .collect(Collectors.toList());

            System.out.println(this.tokens.stream().map(Token::getWord).collect(Collectors.joining(" ")));
        } else {
            Stream<Token> tokenStream = nonWhitespaceTokens
                    .parallelStream()
                    .filter(punctuationFilter::accept)
                    .filter(stopFilter::accept);
            this.tokens = tokenStream
                    .collect(Collectors.toList());
        }

        return this;
    }

    @Override
    public Analysis<Void, Double> skipPreprocessing(List<Token> tokens, Void v) {
        this.tokens = tokens;
        return this;
    }

    @Override
    public LexicalDiversityAnalysis execute() throws AnalysisException {

        if (tokens.size() < MINIMUM_TOKENS) {
            throw new AnalysisException("Cannot calculate lexical" +
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
                throw new AnalysisException("Unknown algorithm to calculate lexical" +
                        " diversity '" + algorithm + "'.");
        }

        return this;
    }

    @Override
    public Double getResult() {
        return result;
    }*/

    /**
     * HD-D is an idealized version of voc-D. For more information see McCarthy, P.M.
     * & Jarvis, S. (2007). vocd: A theoretical and empirical evaluation. Language
     * Testing, 24(4), 459-488.
     *
     * @param tokens {@link List} tokens from text
     * @param sampleSize {@code int} sample size
     * @return TTR
     */
    /*private double hdd(List<Token> tokens, int sampleSize) throws AnalysisException {
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
            ) / (double) sampleSize;
            hdd += contribution;
        }
        return hdd;
    }*/

    /**
     * MTLD (Measure of Textual Lexical Diversity, or LDAT, Lexical Diversity
     * Assessment Tool) is derived from the average length of continuous text
     * units above a certain Type-Token Ratio.
     *
     * @param tokens {@link List} tokens from text
     * @param threshold {@code double} threshold ratio to increment counter
     * @return TTR
     */
    /*private double mtld(List<Token> tokens, double threshold) {
        List<Token> reversed = new ArrayList<>(tokens);
        Collections.reverse(reversed);
        return (calculateMtld(tokens, threshold) + calculateMtld(reversed, threshold)) / 2;
    }

    private double calculateMtld(List<Token> tokens, double threshold) {
        double currentTtr = 1.0;
        Set<String> types = new HashSet<>();
        double factors = 0.0;
        int tokenCount = 0;
        for (Token token : tokens) {
            types.add(token.getWord());
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

    public enum Algorithm {
        MTLD,
        HDD
    }*/
}
