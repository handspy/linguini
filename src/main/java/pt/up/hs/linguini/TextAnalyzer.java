package pt.up.hs.linguini;

import pt.up.hs.linguini.analysis.cooccurrence.CoOccurrence;
import pt.up.hs.linguini.analysis.cooccurrence.CoOccurrenceAnalysis;
import pt.up.hs.linguini.analysis.emotional.EmotaixAnalysis;
import pt.up.hs.linguini.analysis.exceptions.AnalysisException;
import pt.up.hs.linguini.analysis.ideadensity.IdeaDensityAnalysis;
import pt.up.hs.linguini.analysis.ideadensity.Proposition;
import pt.up.hs.linguini.analysis.lexicaldiversity.*;
import pt.up.hs.linguini.analysis.summary.*;
import pt.up.hs.linguini.exceptions.LinguiniException;
import pt.up.hs.linguini.filtering.PunctuationTokenFilter;
import pt.up.hs.linguini.filtering.StopTokenFilter;
import pt.up.hs.linguini.filtering.WhitespaceTokenFilter;
import pt.up.hs.linguini.lemmatization.Lemmatizer;
import pt.up.hs.linguini.models.*;
import pt.up.hs.linguini.nndep.NNDepParser;
import pt.up.hs.linguini.pipeline.BatchStep;
import pt.up.hs.linguini.pipeline.Step;
import pt.up.hs.linguini.pos.PoSTagger;
import pt.up.hs.linguini.tokenization.SentenceSplitter;
import pt.up.hs.linguini.tokenization.Tokenizer;
import pt.up.hs.linguini.transformation.LowercaseTokenTransformer;

import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Text analyzer.
 *
 * @author José Carlos Paiva <code>josepaiva94@gmail.com</code>
 */
public class TextAnalyzer {

    /**
     * Simple text analysis that provides a summary of stats of the text,
     * including: frequency of each word, words by grammatical class, number of
     * sentences, average word length, ...
     *
     * @param locale {@link Locale} locale/language of text.
     * @param text   {@link String} text to analyze.
     * @return {@link TextSummary} text summary.
     * @throws LinguiniException if an error occurs during analysis.
     */
    public static TextSummary summarize(Locale locale, String text)
            throws LinguiniException {

        TextSummary textSummary = new TextSummary();

        // count sentences
        textSummary.setNrOfSentences(new SentenceSplitter(locale)
                .execute(text)
                .size());

        // count characters
        textSummary.setNrOfCharacters(text.length());

        // count non-blank characters
        textSummary.setNrOfNonBlankCharacters(
                text.replaceAll("\\s+", "").length());

        List<AnnotatedToken<String>> tokens =
                new Tokenizer(locale, true)
                        .pipe(new PoSTagger(locale))
                        .pipe(new WhitespaceTokenFilter<>())
                        .pipe(new PunctuationTokenFilter<>())
                        .pipe(new BatchStep<>(
                                new LowercaseTokenTransformer<>(locale)))
                        /*.pipe(new StopTokenFilter<>(locale))*/
                        .execute(text);

        // count words;
        textSummary.setNrOfWords(tokens.size());
        textSummary.setNrOfDistinctWords(
                (int) tokens.parallelStream()
                        .map(HasWord::word)
                        .distinct()
                        .count()
        );

        // count functional words
        List<AnnotatedToken<String>> functionalWords =
                new FunctionalWordAnalysis(locale)
                        .execute(tokens);
        textSummary.setNrOfFunctionalWords(functionalWords.size());

        // count content words
        List<AnnotatedToken<String>> contentWords =
                new ContentWordAnalysis(locale)
                        .execute(tokens);
        textSummary.setNrOfContentWords(contentWords.size());

        // calculate average word length, non-stopword length,
        // functional word length, and content word length
        AverageWordLengthAnalysis<AnnotatedToken<String>> avgWordLengthAnalysis =
                new AverageWordLengthAnalysis<>();
        textSummary.setAvgWordLength(
                avgWordLengthAnalysis.execute(tokens)
        );
        textSummary.setAvgNonStopWordLength(
                new StopTokenFilter<AnnotatedToken<String>>(locale)
                        .pipe(avgWordLengthAnalysis)
                        .execute(tokens)
        );
        textSummary.setAvgFunctionalWordLength(
                avgWordLengthAnalysis.execute(functionalWords)
        );
        textSummary.setAvgContentWordLength(
                avgWordLengthAnalysis.execute(contentWords)
        );

        // set functional words
        textSummary.setFunctionalWords(
                functionalWords.parallelStream()
                        .map(HasWord::word)
                        .collect(Collectors.toSet())
        );

        // set content words
        textSummary.setContentWords(
                contentWords.parallelStream()
                        .map(HasWord::word)
                        .collect(Collectors.toSet())
        );

        // count distinct content words
        textSummary.setNrOfDistinctContentWords((int) contentWords
                .parallelStream()
                .map(HasWord::word)
                .distinct()
                .count());

        // count distinct functional words
        textSummary.setNrOfDistinctFunctionalWords((int) functionalWords
                .parallelStream()
                .map(HasWord::word)
                .distinct()
                .count());

        // calculate word frequency
        WordFrequencyAnalysis<AnnotatedToken<String>> wordFrequencyAnalysis =
                new WordFrequencyAnalysis<>();
        textSummary.setWordFrequency(
                wordFrequencyAnalysis.execute(tokens)
        );
        textSummary.setContentWordFrequency(
                wordFrequencyAnalysis.execute(contentWords)
        );
        textSummary.setFunctionalWordFrequency(
                wordFrequencyAnalysis.execute(functionalWords)
        );

        // count distinct lemmas
        List<AnnotatedToken<String>> lemmaWords =
                new BatchStep<>(new Lemmatizer(locale))
                        .execute(tokens);
        textSummary.setNrOfDistinctLemmas((int) lemmaWords
                .parallelStream()
                .map(HasWord::word)
                .distinct()
                .count());

        // calculate lemma frequency
        textSummary.setLemmaFrequency(
                wordFrequencyAnalysis.execute(lemmaWords)
        );

        // set lemmas
        textSummary.setLemmas(
                lemmaWords.parallelStream()
                        .map(HasWord::word)
                        .collect(Collectors.toSet())
        );

        // group words by category
        Map<String, String> grammaticalConversions =
                Config.getInstance(locale).getGrammaticalConversions();
        textSummary.setWordsByCategory(
                new TagGroupingAnalysis()
                        .execute(tokens)
                        .entrySet()
                        .stream()
                        .collect(Collectors.toMap(
                                e -> grammaticalConversions.get(e.getKey()),
                                Map.Entry::getValue
                        )));

        return textSummary;
    }

    /**
     * Analysis to annotate emotions in words.
     *
     * @param locale    {@link Locale} locale/language of text.
     * @param text      {@link String} text to analyze.
     * @param lemmatize {@code boolean} lemmatize tokens before analysis?
     * @return list of emotions' annotations.
     * @throws LinguiniException if an error occurs during analysis.
     */
    public static List<AnnotatedToken<Emotion>> analyzeEmotions(
            Locale locale, String text, boolean lemmatize
    ) throws LinguiniException {

        Step<String, List<Token>> preprocessPipeline = preprocessingPipeline(locale, lemmatize);

        return preprocessPipeline
                .pipe(new EmotaixAnalysis(locale))
                .execute(text);
    }

    /**
     * Analysis to calculate lexical diversity. Lexical diversity is a measure
     * of how many different words are used in a text.
     *
     * @param locale    {@link Locale} locale/language of text.
     * @param text      {@link String} text to analyze.
     * @param algorithm {@link LDAlgorithm} algorithm to use to calculate
     *                  lexical diversity.
     * @param lemmatize {@code boolean} lemmatize tokens before analysis?
     * @return {@link Double} lexical diversity of the text.
     * @throws LinguiniException if an error occurs during analysis.
     * @see "MTLD, vocd-D, and HD-D: A validation study of sophisticated
     * approaches to lexical diversity assessment. Behavior research methods,
     * 42(2), 381-392."
     */
    public static Double analyzeLexicalDiversity(
            Locale locale, String text, LDAlgorithm algorithm,
            boolean lemmatize
    ) throws LinguiniException {

        Step<String, List<Token>> preprocessPipeline = preprocessingPipeline(locale, lemmatize);

        Step<String, Double> pipeline;
        switch (algorithm) {
            case BASE_TTR:
                pipeline = preprocessPipeline.pipe(new BaseTtrAnalysis<>());
                break;
            case MTLD:
                pipeline = preprocessPipeline.pipe(new MtldAnalysis<>());
                break;
            case HDD:
                pipeline = preprocessPipeline.pipe(new HddAnalysis<>());
                break;
            case VOCD:
                pipeline = preprocessPipeline.pipe(new VocdAnalysis<>());
                break;
            default:
                throw new AnalysisException("Unknown lexical diversity algorithm.");
        }

        return pipeline.execute(text);
    }

    /**
     * Analyze word-word co-occurrences in text.
     *
     * @param locale     {@link Locale} locale/language of text.
     * @param text       {@link String} text to analyze.
     * @param windowSize {@link Integer} size of the sliding window.
     *                   Defaults to 5.
     * @param threshold  {@link Double} Threshold to consider as co-occurrence.
     *                   Defaults to 0.
     * @return List of word-word co-occurrences.
     * @throws LinguiniException if an error occurs during analysis.
     */
    public static List<CoOccurrence> analyzeCoOccurrence(
            Locale locale, String text, Integer windowSize, Double threshold)
            throws LinguiniException {

        CoOccurrenceAnalysis<AnnotatedToken<String>> coOccurrenceAnalysis;
        if (threshold != null) {
            if (windowSize != null) {
                coOccurrenceAnalysis = new CoOccurrenceAnalysis<>(
                        threshold, windowSize);
            } else {
                coOccurrenceAnalysis = new CoOccurrenceAnalysis<>(threshold);
            }
        } else {
            coOccurrenceAnalysis = new CoOccurrenceAnalysis<>();
        }

        return new SentenceSplitter(locale)
                .pipe(new BatchStep<>(new Tokenizer(locale, true)))
                .pipe(new BatchStep<>(new WhitespaceTokenFilter<>()))
                .pipe(new BatchStep<>(new PoSTagger(locale)))
                .pipe(new BatchStep<>(new BatchStep<>(new Lemmatizer(locale))))
                .pipe(new BatchStep<>(new StopTokenFilter<>(locale)))
                .pipe(new BatchStep<>(new PunctuationTokenFilter<>()))
                .pipe(coOccurrenceAnalysis)
                .execute(text);
    }

    /**
     * Analyze idea density of a text.
     *
     * @param locale {@link Locale} locale/language of text.
     * @param text   {@link String} text to analyze.
     * @return density value
     * @throws LinguiniException if an exception occurs while analyzing the idea density.
     */
    public static double analyzeIdeaDensity(
            Locale locale, String text) throws LinguiniException {

        List<List<AnnotatedToken<String>>> taggedTokens =
                new SentenceSplitter(locale)
                        .pipe(new BatchStep<>(new Tokenizer(locale, true)))
                        .pipe(new BatchStep<>(new PoSTagger(locale)))
                        .pipe(new BatchStep<>(new BatchStep<>(new Lemmatizer(locale))))
                        .execute(text);

        int wc = taggedTokens.parallelStream()
                .mapToInt(tt -> new PunctuationTokenFilter<AnnotatedToken<String>>().execute(tt).size())
                .sum();

        if (wc == 0) {
            throw new LinguiniException("No words in text.");
        }

        List<List<Proposition>> sentencePropositions =
                new BatchStep<>(NNDepParser.getInstance(locale))
                        .pipe(new BatchStep<>(new IdeaDensityAnalysis(locale)))
                        .execute(taggedTokens);

        int pc = sentencePropositions.parallelStream()
                .mapToInt(List::size)
                .sum();

        return (double) pc / wc;
    }

    private static Step<String, List<Token>> preprocessingPipeline(
            Locale locale, boolean lemmatize
    ) throws LinguiniException {

        Step<String, List<Token>> preprocessPipeline =
                // 1. tokenize text
                new Tokenizer(locale, true)
                // 2. remove whitespaces
                .pipe(new WhitespaceTokenFilter<>());

        if (lemmatize) {
            preprocessPipeline = preprocessPipeline
                    // 3. PoS tagging
                    .pipe(new PoSTagger(locale))
                    // 4. lemmatize text
                    .pipe(new BatchStep<>(new Lemmatizer(locale)))
                    .pipe(ats -> ats.parallelStream()
                            .map(AnnotatedToken::getToken)
                            .collect(Collectors.toList()));
        }

        preprocessPipeline = preprocessPipeline
                // 5. remove punctuation
                .pipe(new PunctuationTokenFilter<>())
                // 6. normalize
                .pipe(new BatchStep<>(new LowercaseTokenTransformer<>()))
                // 7. remove stopwords
                .pipe(new StopTokenFilter<>(locale));

        return preprocessPipeline;
    }
}
