package pt.up.hs.linguini.analysis;

import pt.up.hs.linguini.analysis.exceptions.AnalysisException;
import pt.up.hs.linguini.analysis.summary.*;
import pt.up.hs.linguini.exceptions.LinguiniException;
import pt.up.hs.linguini.filtering.PunctuationTokenFilter;
import pt.up.hs.linguini.filtering.StopTokenFilter;
import pt.up.hs.linguini.filtering.WhitespaceTokenFilter;
import pt.up.hs.linguini.lemmatization.Lemmatizer;
import pt.up.hs.linguini.models.AnnotatedToken;
import pt.up.hs.linguini.models.HasWord;
import pt.up.hs.linguini.models.TextSummary;
import pt.up.hs.linguini.pipeline.BatchStep;
import pt.up.hs.linguini.pos.PoSTagger;
import pt.up.hs.linguini.tokenization.SentenceSplitter;
import pt.up.hs.linguini.tokenization.Tokenizer;
import pt.up.hs.linguini.transformation.LowercaseTokenTransformer;

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
public class SimpleTextAnalysis/* implements Analysis<String, TextSummary>*/ {

    /*private Locale locale;

    public SimpleTextAnalysis() throws AnalysisException {
        this(Locale.ENGLISH);
    }

    public SimpleTextAnalysis(Locale locale) throws AnalysisException {
        this.locale = locale;
    }

    @Override
    public TextSummary execute(String text) throws LinguiniException {
    }

    @Override
    public SimpleTextAnalysis preprocess(List<Token> tokens)
            throws AnalysisException {
        this.tokens = tokens;
        prepareHelperLists();

        return this;
    }

    @Override
    public SimpleTextAnalysis skipPreprocessing(
            List<Token> tokens,
            Void v
    ) throws AnalysisException {
        this.tokens = tokens;
        prepareHelperLists();

        return this;
    }

    public void analyze() throws FilteringException, TokenizationException {

        Tokenizer tokenizer = new Tokenizer(locale);

        Step<List<Token>, List<Token>> whitespaceRemoval = new WhitespaceTokenFilter();

        Step<List<Token>, List<AnnotatedToken<String>>> posTagging =
                new PoSTagger(locale);

        Step<List<Token>, List<Token>> punctuationRemoval =
                new PunctuationTokenFilter();
        Step<List<Token>, List<Token>> stopwordRemoval =
                new StopTokenFilter(locale);

        whitespaceRemoval.pipe(posTagging).execute();

                .pipe(punctuationRemoval).pipe(stopwordRemoval)

        whitespaceRemoval.pipe(new )
    }

    @Override
    public SimpleTextAnalysis execute() throws AnalysisException {

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
        textSummary.setNrOfStopWords(stopWords.size());

        // count functional words
        textSummary.setNrOfFunctionalWords(functionalWords.size());

        // count content words
        textSummary.setNrOfContentWords(contentWords.size());

        // count distinct lemmas
        textSummary.setNrOfDistinctLemmas(
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

        // calculate average non stop word length
        textSummary.setAvgNonStopWordLength(
                nonStopWords.parallelStream()
                        .mapToInt(t -> t.getOriginal().length())
                        .average()
                        .orElse(-1)
        );

        // calculate functional word length
        textSummary.setAvgFunctionalWordLength(
                functionalWords.parallelStream()
                        .mapToInt(t -> t.getOriginal().length())
                        .average()
                        .orElse(-1)
        );

        // calculate content word length
        textSummary.setAvgContentWordLength(
                contentWords.parallelStream()
                        .mapToInt(t -> t.getOriginal().length())
                        .average()
                        .orElse(-1)
        );

        // set stop words
        textSummary.setStopWords(
                stopWords.parallelStream()
                        .map(Token::getWord)
                        .collect(Collectors.toSet())
        );

        // set functional words
        textSummary.setFunctionalWords(
                functionalWords.parallelStream()
                        .map(Token::getWord)
                        .collect(Collectors.toSet())
        );

        // set content words
        textSummary.setContentWords(
                contentWords.parallelStream()
                        .map(Token::getWord)
                        .collect(Collectors.toSet())
        );

        // set lemmas
        textSummary.setLemmas(
                lemmatizedWords.parallelStream()
                        .map(Token::getWord)
                        .collect(Collectors.toSet())
        );

        // calculate token frequency
        textSummary.setTokenFrequency(
                wordsOnly.parallelStream()
                        .collect(
                                Collectors
                                        .toConcurrentMap(
                                                Token::getOriginal,
                                                t -> 1,
                                                Integer::sum)
                        )
        );

        // calculate word frequency
        textSummary.setWordFrequency(
                wordsOnly.parallelStream()
                        .collect(
                                Collectors
                                        .toConcurrentMap(
                                                t -> t.getWord().toLowerCase(),
                                                t -> 1,
                                                Integer::sum
                                        )
                        )
        );

        // calculate lemma frequency
        textSummary.setLemmaFrequency(
                lemmatizedWords.parallelStream()
                        .collect(
                                Collectors
                                        .toConcurrentMap(
                                                t -> t.getWord().toLowerCase(),
                                                t -> 1,
                                                Integer::sum
                                        )
                        )
        );

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

    private void prepareHelperLists() throws AnalysisException {

        // build sentences
        sentences = SentenceStream
                .sentences(locale, tokens.stream().map(Token::getOriginal))
                .collect(Collectors.toList());

        // build non-whitespace token list
        WhitespaceTokenFilter whitespaceFilter = new WhitespaceTokenFilter();
        List<Token> nonWhitespace = tokens.parallelStream()
                .filter(whitespaceFilter::accept)
                .collect(Collectors.toList());

        // annotate tokens with PoS tags
        PoSTagger tagger = new PoSTagger(locale);
        taggedTokens = tagger.tag(nonWhitespace);

        // build words list
        PunctuationTokenFilter punctuationFilter = new PunctuationTokenFilter();
        wordsOnly = nonWhitespace.parallelStream()
                .filter(punctuationFilter::accept)
                .collect(Collectors.toList());

        // build stop, non-stop, functional, and content word lists
        StopTokenFilter stopFilter = new StopTokenFilter(locale);
        String nonFunctionalTags = config.getNonFunctionalTags().toUpperCase();

        nonStopWords = new ArrayList<>();
        stopWords = new ArrayList<>();
        contentWords = new ArrayList<>();
        functionalWords = new ArrayList<>();

        for (AnnotatedToken<String> taggedToken: taggedTokens) {
            Token token = taggedToken.getToken();
            String tag = taggedToken.getInfo().toUpperCase();
            if (tag.equals("PUNCT")) {
                continue;
            }
            if (stopFilter.accept(token)) {
                nonStopWords.add(token);
                if (tag.matches(nonFunctionalTags)) {
                    contentWords.add(token);
                } else {
                    functionalWords.add(token);
                }
            } else {
                stopWords.add(token);
            }
        }

        // build lemmatized word list
        try {
            final Lemmatizer lemmatizer = new Lemmatizer(locale);
            lemmatizedWords = taggedTokens.parallelStream()
                    .peek(at -> lemmatizer.lemmatize(at.getToken(), at.getInfo()))
                    .map(AnnotatedToken::getToken)
                    .collect(Collectors.toList());
        } catch (LemmatizationException e) {
            throw new AnalysisException("Could not build a lemmatized word list", e);
        }
    }*/
}
