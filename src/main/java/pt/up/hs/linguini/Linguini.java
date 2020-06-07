package pt.up.hs.linguini;

import pt.up.hs.linguini.analysis.cooccurrence.CoOccurrence;
import pt.up.hs.linguini.analysis.cooccurrence.CoOccurrenceAnalysis;
import pt.up.hs.linguini.analysis.emotional.EmotaixAnalysis;
import pt.up.hs.linguini.analysis.ideadensity.IdeaDensityAnalysis;
import pt.up.hs.linguini.analysis.ideadensity.Proposition;
import pt.up.hs.linguini.analysis.lexicaldiversity.BaseTtrAnalysis;
import pt.up.hs.linguini.analysis.lexicaldiversity.HddAnalysis;
import pt.up.hs.linguini.analysis.lexicaldiversity.MtldAnalysis;
import pt.up.hs.linguini.analysis.lexicaldiversity.VocdAnalysis;
import pt.up.hs.linguini.analysis.summary.ContentWordAnalysis;
import pt.up.hs.linguini.analysis.summary.FunctionalWordAnalysis;
import pt.up.hs.linguini.analysis.summary.TagGroupingAnalysis;
import pt.up.hs.linguini.analysis.summary.WordFrequencyAnalysis;
import pt.up.hs.linguini.dictionaries.exceptions.DictionaryException;
import pt.up.hs.linguini.exceptions.LinguiniException;
import pt.up.hs.linguini.filtering.PunctuationTokenFilter;
import pt.up.hs.linguini.filtering.StopTokenFilter;
import pt.up.hs.linguini.filtering.WhitespaceTokenFilter;
import pt.up.hs.linguini.lemmatization.Lemmatizer;
import pt.up.hs.linguini.models.AnnotatedToken;
import pt.up.hs.linguini.models.Emotion;
import pt.up.hs.linguini.models.LinguisticsReport;
import pt.up.hs.linguini.models.Token;
import pt.up.hs.linguini.nndep.NNDepParser;
import pt.up.hs.linguini.pipeline.BatchStep;
import pt.up.hs.linguini.pos.PoSTagger;
import pt.up.hs.linguini.tokenization.AnnotatedSentenceSplitter;
import pt.up.hs.linguini.tokenization.Tokenizer;
import pt.up.hs.linguini.transformation.LowercaseTokenTransformer;

import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

public class Linguini {

    private final Locale locale;

    private final Integer coOccurrenceWindowSize;
    private final Double coOccurrenceThreshold;

    public Linguini() {
        this(Locale.getDefault());
    }

    public Linguini(Locale locale) {
        this(locale, null, null);
    }

    public Linguini(Locale locale, Integer coOccurrenceWindowSize, Double coOccurrenceThreshold) {
        this.locale = locale;
        this.coOccurrenceWindowSize = coOccurrenceWindowSize;
        this.coOccurrenceThreshold = coOccurrenceThreshold;
    }

    /*public List<Token> tokenize(String text) throws LinguiniException {
        return new Tokenizer(locale, true).execute(text);
    }

    public <T extends HasWord> List<T> removeWhitespaces(List<T> ts) {
        return new WhitespaceTokenFilter<T>().execute(ts);
    }

    public  List<T> annotatePoS(List<T> ts) {
        return new PoSTagger(locale).execute(ts);
    }

    public void splitSentences() {

    }

    public void lemmatize() {

    }

    public void removePunctuation() {

    }

    public void removeStopwords() {

    }*/

    public LinguisticsReport analyze(String text) throws LinguiniException {

        LinguisticsReport report = new LinguisticsReport();

        // 1. tokenize text
        List<Token> tokens = new Tokenizer(locale, true)
                // 2. remove whitespaces
                .pipe(new WhitespaceTokenFilter<>())
                .execute(text);

        // 3. PoS Tagging
        List<AnnotatedToken<String>> posTaggedTokens = new PoSTagger(locale)
                .execute(tokens);

        // 4. sentence splitting
        List<List<AnnotatedToken<String>>> sentences = new AnnotatedSentenceSplitter(locale)
                .execute(posTaggedTokens);

        // 5. lemmatize
        List<List<AnnotatedToken<String>>> lemmatizedSentences =
                new BatchStep<>(new BatchStep<>(new Lemmatizer(locale)))
                        .execute(sentences);

        // 6. drop punctuation
        List<List<AnnotatedToken<String>>> lemmatizedSentencesNoPunctuation =
                new BatchStep<>(new PunctuationTokenFilter<AnnotatedToken<String>>())
                        .execute(lemmatizedSentences);

        // 7. normalize
        List<List<AnnotatedToken<String>>> lemmatizedSentencesLowercaseNoPunctuation =
                new BatchStep<>(new BatchStep<>(new LowercaseTokenTransformer<AnnotatedToken<String>>()))
                        .execute(lemmatizedSentencesNoPunctuation);

        // 8. drop stop words
        List<List<AnnotatedToken<String>>> cleanedAnnotatedTokenSentences =
                new BatchStep<>(new StopTokenFilter<AnnotatedToken<String>>(locale))
                        .execute(lemmatizedSentencesLowercaseNoPunctuation);

        // 9. cleaned up text tokens
        List<AnnotatedToken<String>> cleanedAnnotatedTokenText = cleanedAnnotatedTokenSentences.parallelStream()
                .flatMap(Collection::parallelStream)
                .collect(Collectors.toList());

        // 10. analyze text structural and morphological composition
        analyzeComposition(
                report,
                text,
                posTaggedTokens,
                lemmatizedSentencesLowercaseNoPunctuation
        );

        // 11. analyze lexical diversity
        analyzeLexicalDiversity(report, cleanedAnnotatedTokenText);

        // 12. analyze co-occurrences
        analyzeCoOccurrences(report, cleanedAnnotatedTokenSentences);

        // 13. analyze emotions
        analyzeEmotions(report, cleanedAnnotatedTokenText);

        // 14. analyze idea density
        analyzeIdeaDensity(report, lemmatizedSentences);

        return report;
    }

    private void analyzeComposition(
            LinguisticsReport report,
            String text,
            List<AnnotatedToken<String>> posTaggedTokens,
            List<List<AnnotatedToken<String>>> lemmatizedSentencesLowercaseNoPunctuation
    ) throws LinguiniException {

        List<AnnotatedToken<String>> lemmatizedText = lemmatizedSentencesLowercaseNoPunctuation.stream()
                .flatMap(Collection::stream)
                .collect(Collectors.toList());

        List<AnnotatedToken<String>> nonLemmatizedText = new PunctuationTokenFilter<AnnotatedToken<String>>()
                .pipe(new BatchStep<>(new LowercaseTokenTransformer<>()))
                .execute(posTaggedTokens);

        // structure
        report.setCharacterCount(text.length());
        report.setNonBlankCharacterCount(text.replaceAll("\\s+", "").length());
        report.setWordCount(nonLemmatizedText.size());
        report.setSentenceCount(lemmatizedSentencesLowercaseNoPunctuation.size());

        // grammatical class
        report.setMorphologicalAnnotations(lemmatizedText);

        // group words by category
        Map<String, String> grammaticalConversions = Config.getInstance(locale)
                .getGrammaticalConversions();
        report.setWordsByCategory(
                new TagGroupingAnalysis()
                        .execute(nonLemmatizedText)
                        .entrySet()
                        .stream()
                        .collect(Collectors.toMap(
                                e -> grammaticalConversions.get(e.getKey()),
                                Map.Entry::getValue
                        )));

        // frequencies
        report.setContentWordFrequency(
                new ContentWordAnalysis(locale)
                        .pipe(new WordFrequencyAnalysis<>())
                        .execute(nonLemmatizedText)
        );
        report.setFunctionalWordFrequency(
                new FunctionalWordAnalysis(locale)
                        .pipe(new WordFrequencyAnalysis<>())
                        .execute(nonLemmatizedText)
        );
        report.setLemmaFrequency(
                new WordFrequencyAnalysis<AnnotatedToken<String>>()
                        .execute(lemmatizedText)
        );
    }

    private void analyzeCoOccurrences(
            LinguisticsReport report,
            List<List<AnnotatedToken<String>>> cleanedAnnotatedTokenSentences
    ) throws LinguiniException {

        CoOccurrenceAnalysis<AnnotatedToken<String>> coOccurrenceAnalysis;
        if (coOccurrenceThreshold != null) {
            if (coOccurrenceWindowSize != null) {
                coOccurrenceAnalysis = new CoOccurrenceAnalysis<>(
                        coOccurrenceThreshold, coOccurrenceWindowSize);
            } else {
                coOccurrenceAnalysis = new CoOccurrenceAnalysis<>(coOccurrenceThreshold);
            }
        } else {
            coOccurrenceAnalysis = new CoOccurrenceAnalysis<>();
        }

        List<CoOccurrence> coOccurrences = coOccurrenceAnalysis
                .execute(cleanedAnnotatedTokenSentences);

        report.setCoOccurrences(coOccurrences);
    }

    private void analyzeLexicalDiversity(
            LinguisticsReport report,
            List<AnnotatedToken<String>> cleanedAndAnnotatedTokens
    ) throws LinguiniException {

        report.setBaseTTR(new BaseTtrAnalysis<AnnotatedToken<String>>()
                .execute(cleanedAndAnnotatedTokens));
        report.setMtld(new MtldAnalysis<AnnotatedToken<String>>()
                .execute(cleanedAndAnnotatedTokens));
        report.setHdd(new HddAnalysis<AnnotatedToken<String>>()
                .execute(cleanedAndAnnotatedTokens));
        report.setVocd(new VocdAnalysis<AnnotatedToken<String>>()
                .execute(cleanedAndAnnotatedTokens));
    }

    private void analyzeEmotions(
            LinguisticsReport report,
            List<AnnotatedToken<String>> cleanedAndAnnotatedTokens
    ) throws LinguiniException {

        try {
            List<AnnotatedToken<Emotion>> emotionalAnnotations = new EmotaixAnalysis(locale)
                    .execute(
                        cleanedAndAnnotatedTokens.parallelStream()
                                .map(AnnotatedToken::getToken)
                                .collect(Collectors.toList())
                    );
            report.setEmotionalAnnotations(emotionalAnnotations);
        } catch (DictionaryException e) {
            throw new LinguiniException("Could not execute emotional analysis.", e);
        }
    }

    private void analyzeIdeaDensity(
            LinguisticsReport report,
            List<List<AnnotatedToken<String>>> lemmatizedSentences
    ) throws LinguiniException {

        int wordCount = lemmatizedSentences.parallelStream()
                .mapToInt(List::size)
                .sum();

        if (wordCount == 0) {
            throw new LinguiniException("No words in text.");
        }

        List<List<Proposition>> sentencePropositions =
                new BatchStep<>(NNDepParser.getInstance(locale))
                        .pipe(new BatchStep<>(new IdeaDensityAnalysis(locale)))
                        .execute(lemmatizedSentences);

        int propositionCount = sentencePropositions.parallelStream()
                .mapToInt(List::size)
                .sum();

        report.setIdeaDensity((double) propositionCount / wordCount);
    }
}
