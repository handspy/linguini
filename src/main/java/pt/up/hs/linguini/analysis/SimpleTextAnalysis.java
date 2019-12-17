package pt.up.hs.linguini.analysis;

import pt.up.hs.linguini.Config;
import pt.up.hs.linguini.exceptions.AnalyzerException;
import pt.up.hs.linguini.exceptions.ConfigException;
import pt.up.hs.linguini.filters.PunctuationTokenFilter;
import pt.up.hs.linguini.filters.StopTokenFilter;
import pt.up.hs.linguini.filters.WhitespaceTokenFilter;
import pt.up.hs.linguini.lemmatization.Lemmatizer;
import pt.up.hs.linguini.lemmatization.exceptions.LemmatizationException;
import pt.up.hs.linguini.models.AnnotatedToken;
import pt.up.hs.linguini.models.Category;
import pt.up.hs.linguini.models.TextSummary;
import pt.up.hs.linguini.models.Token;
import pt.up.hs.linguini.pos.PoSTagger;
import pt.up.hs.linguini.utils.SentenceStream;

import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Simple text analysis that provides a summary of stats of the text,
 * including: frequency of each word, words by grammatical class, number of
 * sentences, average word length, ...
 *
 * @author José Carlos Paiva <code>josepaiva94@gmail.com</code>
 */
public class SimpleTextAnalysis implements Analysis<Void, TextSummary> {

    // properties
    private Locale locale;
    private Config config;
    private List<Token> tokens;

    // prepared info for analysis
    private List<String> sentences = null;
    private List<Token> nonWhitespace = null;
    private List<Token> wordsOnly = null;
    private List<Token> stopWords = null;
    private List<Token> nonStopWords = null;
    private List<Token> functionalWords = null;
    private List<Token> contentWords = null;
    private List<AnnotatedToken<String>> taggedTokens = null;
    private List<Token> lemmatizedWords = null;

    // result
    private TextSummary textSummary;

    public SimpleTextAnalysis() throws AnalyzerException {
        this(Locale.ENGLISH);
    }

    public SimpleTextAnalysis(Locale locale) throws AnalyzerException {
        this.locale = locale;
        try {
            this.config = Config.getInstance(locale);
        } catch (ConfigException e) {
            throw new AnalyzerException("Could not load configuration properties.");
        }
    }

    @Override
    public SimpleTextAnalysis preprocess(List<Token> tokens)
            throws AnalyzerException {
        this.tokens = tokens;
        prepareHelperLists();

        return this;
    }

    @Override
    public SimpleTextAnalysis skipPreprocessing(
            List<Token> tokens,
            Void v
    ) throws AnalyzerException {
        this.tokens = tokens;
        prepareHelperLists();

        return this;
    }

    @Override
    public SimpleTextAnalysis execute() throws AnalyzerException {

        textSummary = new TextSummary();

        // count characters
        textSummary.setNrOfCharacters(
                tokens.parallelStream()
                        .mapToInt(t -> t.getOriginal().length())
                        .sum());

        // count non-blank characters
        WhitespaceTokenFilter whitespaceFilter = new WhitespaceTokenFilter();
        textSummary.setNrOfNonBlankCharacters(
                tokens.parallelStream()
                        .filter(whitespaceFilter::accept)
                        .mapToInt(t -> t.getOriginal().length())
                        .sum());

        // count sentences
        textSummary.setNrOfSentences(sentences.size());

        // count words
        textSummary.setNrOfWords(wordsOnly.size());

        // count stop words
        textSummary.setNrOfStopWords();
        textSummary.setNrOfNonStopWords(wordsOnly.size() - contentWordsOnly.size());

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

        // calculate average content word length
        textSummary.setAvgContentWordLength(
                contentWordsOnly.parallelStream()
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

        // calculate content word frequency
        textSummary.setContentWordFrequency(
                contentWordsOnly.parallelStream()
                        .collect(
                                Collectors
                                        .toConcurrentMap(
                                                Token::getOriginal,
                                                t -> 1,
                                                Integer::sum)
                        )
        );

        // calculate functional words
        textSummary.

        // group words by category
        Map<String, String> grammaticalCategories = config.getGrammaticalConversions();
        textSummary.setWordsByCategory(
                taggedTokens.parallelStream()
                    .collect(
                            Collectors
                                    .groupingByConcurrent(
                                            at -> {
                                                String tag = grammaticalCategories.get(at.getInfo());
                                                if (tag != null) {
                                                    return Category.valueOf(tag.toUpperCase());
                                                }
                                                return Category.OTHER;
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

        // build non-whitespace token list
        WhitespaceTokenFilter whitespaceFilter = new WhitespaceTokenFilter();
        nonWhitespace = tokens.parallelStream()
                .filter(whitespaceFilter::accept)
                .collect(Collectors.toList());

        // build words list
        PunctuationTokenFilter punctuationFilter = new PunctuationTokenFilter();
        wordsOnly = nonWhitespace.parallelStream()
                .filter(punctuationFilter::accept)
                .collect(Collectors.toList());

        // build stop and word list
        StopTokenFilter stopFilter = new StopTokenFilter(locale);
        for (Token token: wordsOnly) {
            if (stopFilter.accept(token)) {

            } else {

            }
        }
        contentWords = wordsOnly.parallelStream()
                .filter(stopFilter::accept)
                .collect(Collectors.toList());

        // annotate tokens with PoS tags
        PoSTagger tagger = new PoSTagger(locale);
        taggedTokens = tagger.tag(wordsOnly);

        // build functional word list
        String functionalTags = config.getNonFunctionalTags();
        functionalWords = taggedTokens
                .parallelStream()
                .filter(tt -> tt.getInfo().toUpperCase().matches(functionalTags.toUpperCase()))
                .map(AnnotatedToken::getToken)
                .collect(Collectors.toList());

        // build content word list
        StopTokenFilter stopFilter = new StopTokenFilter(locale);
        contentWords = wordsOnly.parallelStream()
                .filter(stopFilter::accept)
                .collect(Collectors.toList());

        // build lemmatized word list
        try {
            final Lemmatizer lemmatizer = new Lemmatizer(locale);
            lemmatizedWords = taggedTokens.parallelStream()
                    .peek(at -> lemmatizer.lemmatize(at.getToken(), at.getInfo()))
                    .map(AnnotatedToken::getToken)
                    .collect(Collectors.toList());
        } catch (LemmatizationException e) {
            throw new AnalyzerException("Could not build a lemmatized word list", e);
        }
    }
}
