package pt.up.hs.linguini.analysis;

import pt.up.hs.linguini.exceptions.AnalyzerException;
import pt.up.hs.linguini.filters.PunctuationTokenFilter;
import pt.up.hs.linguini.filters.StopTokenFilter;
import pt.up.hs.linguini.filters.WhitespaceTokenFilter;
import pt.up.hs.linguini.jspell.JSpellInfo;
import pt.up.hs.linguini.jspell.JSpellLex;
import pt.up.hs.linguini.models.AnnotatedToken;
import pt.up.hs.linguini.models.Category;
import pt.up.hs.linguini.models.TextSummary;
import pt.up.hs.linguini.models.Token;
import pt.up.hs.linguini.transformers.selection.ChooseFirstStrategy;
import pt.up.hs.linguini.transformers.selection.SelectionStrategy;
import pt.up.hs.linguini.transformers.selection.exceptions.SelectionException;
import pt.up.hs.linguini.utils.SentenceStream;

import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

/**
 * Simple text analysis that provides a summary of stats of the text,
 * including: frequency of each word, words by grammatical class, number of
 * sentences, average word length, ...
 *
 * @author José Carlos Paiva <code>josepaiva94@gmail.com</code>
 */
public class SimpleTextAnalysis extends JSpellPreprocessingAnalysis<TextSummary> {

    // prepared info for analysis
    private List<String> sentences = null;
    private List<Token> wordsOnly = null;
    private List<Token> contentWordsOnly = null;
    private List<Token> lemmatizedWords = null;

    // result
    private TextSummary textSummary;

    public SimpleTextAnalysis() {
        this(Locale.ENGLISH);
    }

    public SimpleTextAnalysis(Locale locale) {
        super(locale);
    }

    @Override
    public SimpleTextAnalysis preprocess(List<Token> tokens)
            throws AnalyzerException {
        super.preprocess(tokens);

        prepareHelperLists();

        return this;
    }

    @Override
    public Analysis<List<AnnotatedToken<JSpellInfo>>, TextSummary> skipPreprocessing(
            List<Token> tokens, List<AnnotatedToken<JSpellInfo>> annotatedTokens)
            throws AnalyzerException {
        super.skipPreprocessing(tokens, annotatedTokens);

        prepareHelperLists();

        return this;
    }

    @Override
    public Analysis<List<AnnotatedToken<JSpellInfo>>, TextSummary> execute()
            throws AnalyzerException {

        textSummary = new TextSummary();

        // count sentences
        textSummary.setNrOfSentences(sentences.size());

        // count words
        textSummary.setNrOfWords(wordsOnly.size());

        // count stop words
        textSummary.setNrOfNonStopWords(wordsOnly.size() - contentWordsOnly.size());

        // count errors
        textSummary.setNrOfErrors(
                (int) jSpellAnnotatedTokens.parallelStream()
                        .filter(t -> t.getInfo().isError())
                        .count());

        // count distinct lemmas
        textSummary.setNrOfLemmas(
                (int) lemmatizedWords.parallelStream()
                        .map(Token::getWord)
                        .distinct()
                        .count());

        // calculate average word length
        textSummary.setAvgWordLength(
                wordsOnly.parallelStream()
                        .mapToInt(t -> t.getOriginal().length())
                        .average()
                        .orElse(-1)
        );

        // calculate word frequency
        textSummary.setWordFrequency(
                wordsOnly.parallelStream()
                        .collect(
                                Collectors
                                        .toConcurrentMap(
                                                Token::getOriginal,
                                                t -> 1,
                                                Integer::sum)
                        )
        );

        // group words by category
        SelectionStrategy<JSpellLex> categoryStrategy = new ChooseFirstStrategy<>();
        textSummary.setWordsByCategory(
                jSpellAnnotatedTokens.parallelStream()
                        .collect(
                                Collectors
                                        .groupingByConcurrent(
                                                at -> {
                                                    JSpellInfo info = at.getInfo();
                                                    if (info.getRelated() != null && !info.getRelated().isEmpty()) {
                                                        JSpellLex selectedLex;
                                                        try {
                                                            selectedLex = categoryStrategy.select(info.getRelated());
                                                        } catch (SelectionException ignore) {
                                                            return Category.UNKNOWN;
                                                        }

                                                        return selectedLex.getCategory();
                                                    }
                                                    return Category.UNKNOWN;
                                                },
                                                Collectors.mapping(at -> at.getToken().getOriginal(), Collectors.toSet())
                                        )
                        )
        );

        return this;
    }

    @Override
    public TextSummary getResult() {
        return textSummary;
    }

    private void prepareHelperLists() throws AnalyzerException {

        // build sentences
        sentences = SentenceStream
                .sentences(locale, tokens.stream().map(Token::getOriginal))
                .collect(Collectors.toList());

        // build words list
        WhitespaceTokenFilter whitespaceFilter = new WhitespaceTokenFilter();
        PunctuationTokenFilter punctuationFilter = new PunctuationTokenFilter();
        wordsOnly = tokens.parallelStream()
                .filter(punctuationFilter::accept)
                .filter(whitespaceFilter::accept)
                .collect(Collectors.toList());

        // build content word list
        StopTokenFilter stopFilter = new StopTokenFilter(locale);
        contentWordsOnly = wordsOnly.parallelStream()
                .filter(stopFilter::accept)
                .collect(Collectors.toList());

        // build lemmatized word list
        SelectionStrategy<JSpellLex> strategy = new ChooseFirstStrategy<>();
        lemmatizedWords = jSpellAnnotatedTokens.parallelStream()
                .filter(at -> punctuationFilter.accept(at.getToken()))
                .filter(at -> whitespaceFilter.accept(at.getToken()))
                .map(at -> {
                    Token token = at.getToken();
                    JSpellInfo info = at.getInfo();
                    if (info.getRelated() != null && !info.getRelated().isEmpty()) {
                        JSpellLex selectedLex;
                        try {
                            selectedLex = strategy.select(info.getRelated());
                        } catch (SelectionException ignore) {
                            return token;
                        }
                        token.setWord(selectedLex.getLemma());
                    }
                    return token;
                })
                .collect(Collectors.toList());
    }
}
