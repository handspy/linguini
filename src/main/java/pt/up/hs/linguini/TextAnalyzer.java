package pt.up.hs.linguini;

import pt.up.hs.linguini.analysis.cooccurrence.CooccurrenceAnalysis;
import pt.up.hs.linguini.analysis.cooccurrence.Cooccurrence;
import pt.up.hs.linguini.analysis.emotional.JSpellEmotionalAnalysis;
import pt.up.hs.linguini.analysis.exceptions.AnalysisException;
import pt.up.hs.linguini.analysis.ideadensity.IdeaDensityAnalysis;
import pt.up.hs.linguini.analysis.ideadensity.Proposition;
import pt.up.hs.linguini.analysis.lexicaldiversity.HddAnalysis;
import pt.up.hs.linguini.analysis.lexicaldiversity.LDAlgorithm;
import pt.up.hs.linguini.analysis.lexicaldiversity.MtldAnalysis;
import pt.up.hs.linguini.analysis.summary.*;
import pt.up.hs.linguini.exceptions.LinguiniException;
import pt.up.hs.linguini.filtering.PunctuationTokenFilter;
import pt.up.hs.linguini.filtering.StopTokenFilter;
import pt.up.hs.linguini.filtering.WhitespaceTokenFilter;
import pt.up.hs.linguini.jspell.JSpellWordAnnotator;
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

        // count words
        List<Token> nonExpandedTokens = new Tokenizer(locale, false)
                .pipe(new WhitespaceTokenFilter<>())
                .pipe(new PunctuationTokenFilter<>())
                .execute(text);
        textSummary.setNrOfWords(nonExpandedTokens.size());

        // count stop words
        textSummary.setNrOfStopWords(
                nonExpandedTokens.size() -
                        new StopTokenFilter<Token>(locale)
                                .execute(nonExpandedTokens)
                                .size()
        );

        List<AnnotatedToken<String>> tokens =
                new Tokenizer(locale, true)
                        .pipe(new PoSTagger(locale))
                        .pipe(new WhitespaceTokenFilter<>())
                        .pipe(new PunctuationTokenFilter<>())
                        .pipe(new BatchStep<>(
                                new LowercaseTokenTransformer<>(locale)))
                        .pipe(new StopTokenFilter<>(locale))
                        .execute(text);

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

        // count distinct lemmas
        List<AnnotatedToken<String>> lemmaWords =
                new BatchStep<>(new Lemmatizer(locale))
                        .execute(tokens);
        textSummary.setNrOfDistinctLemmas((int) lemmaWords
                .parallelStream()
                .distinct()
                .count());

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

        // set lemmas
        textSummary.setLemmas(
                lemmaWords.parallelStream()
                        .map(HasWord::word)
                        .collect(Collectors.toSet())
        );

        // calculate word frequency and lemma frequency
        WordFrequencyAnalysis<AnnotatedToken<String>> wordFrequencyAnalysis =
                new WordFrequencyAnalysis<>();
        textSummary.setWordFrequency(
                wordFrequencyAnalysis.execute(tokens)
        );
        textSummary.setLemmaFrequency(
                wordFrequencyAnalysis.execute(lemmaWords)
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

        Step<String, List<Token>> preprocessPipeline =
                // 1. tokenize text
                new Tokenizer(locale, true)
                        // 2. remove whitespaces
                        .pipe(new WhitespaceTokenFilter<>());

        if (lemmatize) {
            preprocessPipeline = preprocessPipeline
                    // 3. PoS Tagging
                    .pipe(new PoSTagger(locale))
                    // 4. remove punctuation
                    .pipe(new PunctuationTokenFilter<>())
                    // 5. remove stopwords
                    .pipe(new StopTokenFilter<>(locale))
                    // 6. lemmatize text
                    .pipe(new BatchStep<>(new Lemmatizer(locale)))
                    .pipe(ats -> ats.parallelStream()
                            .map(AnnotatedToken::getToken)
                            .collect(Collectors.toList()));
        } else {
            preprocessPipeline
                    // 3. remove punctuation
                    .pipe(new PunctuationTokenFilter<>())
                    // 4. remove stopwords
                    .pipe(new StopTokenFilter<>(locale));
        }

        return preprocessPipeline
                .pipe(new JSpellWordAnnotator(locale))
                .pipe(new JSpellEmotionalAnalysis(locale))
                .execute(text);
    }

    /**
     * Analysis to calculate lexical diversity. Lexical diversity is a measure
     * of how many different words are used in a text.
     *
     * @param locale        {@link Locale} locale/language of text.
     * @param text          {@link String} text to analyze.
     * @param algorithm     {@link LDAlgorithm} algorithm to use to calculate
     *                      lexical diversity.
     * @param lemmatize     {@code boolean} lemmatize tokens before analysis?
     * @param mtldThreshold {@link Double} threshold to increment counter
     *                      in MTLD algorithm
     * @param hddSampleSize {@link Integer} size of sample in HDD algorithm
     * @return {@link Double} lexical diversity of the text.
     * @throws LinguiniException if an error occurs during analysis.
     * @see "MTLD, vocd-D, and HD-D: A validation study of sophisticated
     * approaches to lexical diversity assessment. Behavior research methods,
     * 42(2), 381-392."
     */
    public static Double analyzeLexicalDiversity(
            Locale locale, String text, LDAlgorithm algorithm,
            boolean lemmatize, Double mtldThreshold, Integer hddSampleSize
    ) throws LinguiniException {

        Step<String, List<Token>> preprocessPipeline =
                // 1. tokenize text
                new Tokenizer(locale, true)
                        // 2. remove whitespaces
                        .pipe(new WhitespaceTokenFilter<>());

        if (lemmatize) {
            preprocessPipeline = preprocessPipeline
                    // 3. PoS Tagging
                    .pipe(new PoSTagger(locale))
                    // 4. remove punctuation
                    .pipe(new PunctuationTokenFilter<>())
                    // 5. remove stopwords
                    .pipe(new StopTokenFilter<>(locale))
                    // 6. lemmatize text
                    .pipe(new BatchStep<>(new Lemmatizer(locale)))
                    .pipe(ats -> ats.parallelStream()
                            .map(AnnotatedToken::getToken)
                            .collect(Collectors.toList()));
        } else {
            preprocessPipeline
                    // 3. remove punctuation
                    .pipe(new PunctuationTokenFilter<>())
                    // 4. remove stopwords
                    .pipe(new StopTokenFilter<>(locale));
        }

        Step<String, Double> pipeline;
        switch (algorithm) {
            case MTLD:
                if (mtldThreshold == null)
                    pipeline = preprocessPipeline.pipe(new MtldAnalysis<>());
                else
                    pipeline = preprocessPipeline
                            .pipe(new MtldAnalysis<>(mtldThreshold));
                break;
            case HDD:
                if (hddSampleSize == null)
                    pipeline = preprocessPipeline.pipe(new HddAnalysis<>());
                else
                    pipeline = preprocessPipeline.pipe(
                            new HddAnalysis<>(hddSampleSize));
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
    public static List<Cooccurrence> analyzeCoOccurrence(
            Locale locale, String text, Integer windowSize, Double threshold)
            throws LinguiniException {

        CooccurrenceAnalysis<AnnotatedToken<String>> coOccurrenceAnalysis;
        if (threshold != null) {
            if (windowSize != null) {
                coOccurrenceAnalysis = new CooccurrenceAnalysis<>(
                        threshold, windowSize);
            } else {
                coOccurrenceAnalysis = new CooccurrenceAnalysis<>(threshold);
            }
        } else {
            coOccurrenceAnalysis = new CooccurrenceAnalysis<>();
        }

        return new SentenceSplitter(locale)
                .pipe(new BatchStep<>(new Tokenizer(locale, true)))
                .pipe(new BatchStep<>(new WhitespaceTokenFilter<>()))
                .pipe(new BatchStep<>(new PunctuationTokenFilter<>()))
                .pipe(new BatchStep<>(new StopTokenFilter<>(locale)))
                .pipe(new BatchStep<>(new PoSTagger(locale)))
                .pipe(new BatchStep<>(new BatchStep<>(new Lemmatizer(locale))))
                .pipe(coOccurrenceAnalysis)
                .execute(text);
    }

    /**
     * Analyze idea density of a text.
     *
     * @param locale {@link Locale} locale/language of text.
     * @param text   {@link String} text to analyze.
     * @throws LinguiniException if an exception occurs while analyzing the idea density.
     */
    public static double analyzeIdeaDensity(
            Locale locale, String text) throws LinguiniException {

        List<List<AnnotatedToken<String>>> taggedTokens =
                new SentenceSplitter(locale)
                        .pipe(new BatchStep<>(new Tokenizer(locale, true)))
                        .pipe(new BatchStep<>(new PoSTagger(locale)))
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
}
