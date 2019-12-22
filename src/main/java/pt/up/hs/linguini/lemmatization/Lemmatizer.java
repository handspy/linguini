package pt.up.hs.linguini.lemmatization;

import pt.up.hs.linguini.Config;
import pt.up.hs.linguini.caching.InMemoryCache;
import pt.up.hs.linguini.dictionaries.Dictionary;
import pt.up.hs.linguini.dictionaries.Lexicon;
import pt.up.hs.linguini.dictionaries.exceptions.DictionaryException;
import pt.up.hs.linguini.exceptions.ConfigException;
import pt.up.hs.linguini.lemmatization.exceptions.LemmatizationException;
import pt.up.hs.linguini.models.AnnotatedToken;
import pt.up.hs.linguini.models.Token;
import pt.up.hs.linguini.normalization.*;
import pt.up.hs.linguini.normalization.exceptions.NormalizationException;
import pt.up.hs.linguini.pipeline.Step;
import pt.up.hs.linguini.ranking.WordRanking;
import pt.up.hs.linguini.ranking.exceptions.WordRankingException;
import pt.up.hs.linguini.transformation.LowercaseTokenTransformer;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * Convert a word to its lemma form.
 *
 * @author José Carlos Paiva <code>josepaiva94@gmail.com</code>
 */
public class Lemmatizer implements Step<AnnotatedToken<String>, AnnotatedToken<String>> {
    private static final int AUGMENTATIVE = 1;         // Binary 00000001
    private static final int SUPERLATIVE = 2;          // Binary 00000010
    private static final int DIMINUTIVE = 4;           // Binary 00000100
    private static final int GENDER = 8;               // Binary 00001000
    private static final int GENDER_NAME = 16;         // Binary 00010000
    private static final int NUMBER = 32;              // Binary 00100000
    private static final int ADVERB = 64;              // Binary 01000000
    private static final int VERB = 128;               // Binary 10000000
    private static final int ALL = 255;                // Binary 11111111

    private static InMemoryCache<LemmaCacheKey, String> cache =
            new InMemoryCache<>(86400, 3600, 20);

    private Locale locale;

    private int flags = 0;                            // Binary 000000000

    private boolean breakOnHyphen;
    private boolean breakOnUnderscore;

    private LowercaseTokenTransformer lowercaseTokenNormalizer;
    private AdverbTokenNormalizer adverbNormalizer;
    private AugmentativeTokenNormalizer augmentativeNormalizer;
    private DiminutiveTokenNormalizer diminutiveNormalizer;
    private GenderTokenNormalizer genderNormalizer;
    private GenderNameTokenNormalizer genderNameNormalizer;
    private NumberTokenNormalizer numberNormalizer;
    private SuperlativeTokenNormalizer superlativeNormalizer;
    private VerbTokenNormalizer verbNormalizer;

    private String augmentativeTags;
    private String superlativeTags;
    private String diminutiveTags;
    private String genderTags;
    private String genderNameTags;
    private String numberTags;
    private String adverbTags;
    private String verbTags;

    private Lexicon lexicon;
    private Dictionary dictionary;

    private String dictionaryExclusions;

    private WordRanking wordRanking;

    private Map<String, String> lexiconConversions = new HashMap<>();

    public Lemmatizer() throws LemmatizationException {
        this(Locale.ENGLISH);
    }

    public Lemmatizer(Locale locale) throws LemmatizationException {
        this(locale, ALL);
    }

    public Lemmatizer(Locale locale, int flags)
            throws LemmatizationException {
        this(locale, flags, false, true);
    }

    public Lemmatizer(Locale locale, int flags, boolean breakOnHyphen, boolean breakOnUnderscore)
            throws LemmatizationException {
        this.locale = locale;
        this.flags |= flags;
        this.breakOnHyphen = breakOnHyphen;
        this.breakOnUnderscore = breakOnUnderscore;

        initialize();
    }

    private void initialize() throws LemmatizationException {

        this.lowercaseTokenNormalizer = new LowercaseTokenTransformer();

        // read normalizers' replacements
        try {
            this.adverbNormalizer = new AdverbTokenNormalizer(locale);
            this.augmentativeNormalizer = new AugmentativeTokenNormalizer(locale);
            this.diminutiveNormalizer = new DiminutiveTokenNormalizer(locale);
            this.genderNormalizer = new GenderTokenNormalizer(locale);
            this.genderNameNormalizer = new GenderNameTokenNormalizer(locale);
            this.numberNormalizer = new NumberTokenNormalizer(locale);
            this.superlativeNormalizer = new  SuperlativeTokenNormalizer(locale);
            this.verbNormalizer = new VerbTokenNormalizer(locale);
        } catch (NormalizationException e) {
            throw new LemmatizationException(
                    "Could not read normalizers' replacements", e);
        }

        // get replacements' tags
        this.augmentativeTags =
                augmentativeNormalizer.getReplacementsTagsString();
        this.superlativeTags =
                superlativeNormalizer.getReplacementsTagsString();
        this.diminutiveTags =
                diminutiveNormalizer.getReplacementsTagsString();
        this.genderTags = genderNormalizer.getReplacementsTagsString();
        this.genderNameTags = genderNameNormalizer.getReplacementsTagsString();
        this.numberTags = numberNormalizer.getReplacementsTagsString();
        this.adverbTags = adverbNormalizer.getReplacementsTagsString();
        this.verbTags = verbNormalizer.getReplacementsTagsString();

        // read dictionary
        try {
            this.dictionary = new Dictionary(locale);
        } catch (DictionaryException e) {
            throw new LemmatizationException(
                    "Could not read dictionary", e);
        }

        // get lexicon
        this.lexicon = dictionary.retrieveLexicon();

        // read word ranking
        try {
            this.wordRanking = new WordRanking(locale);
        } catch (WordRankingException e) {
            throw new LemmatizationException(
                    "Could not read word ranking", e);
        }

        // read configuration
        try {
            Config config = Config.getInstance(locale);
            this.dictionaryExclusions = config.getDictionaryExclusions();
            this.lexiconConversions = config.getLexicalConversions();
        } catch (ConfigException e) {
            throw new LemmatizationException(
                    "Could not read configuration", e);
        }
    }

    /**
     * This method retrieves the lemma of a given token, when classified with
     * a given <em>PoS tag</em>.
     *
     * @param  taggedToken the <em>PoS tag</em> of the token
     * @return {@link AnnotatedToken} the lemma of the token (when classified
     *                                with the given tag)
     */
    @Override
    public AnnotatedToken<String> execute(AnnotatedToken<String> taggedToken) {

        Token token = taggedToken.getToken();
        String tag = taggedToken.getInfo();

        // check for token|tag in cache
        LemmaCacheKey key = new LemmaCacheKey(
                token.getWord().toLowerCase(), tag.toLowerCase());
        if (cache.get(key) != null) {
            token.setWord(cache.get(key));
            return taggedToken;
        }

        // normalize token/lemma
        lowercaseTokenNormalizer.execute(token);

        // simplify pos tag to address label-lex-sw
        String lexTag = tag.toUpperCase();
        if (lexTag.contains("-")) {
            lexTag = lexTag.substring(0, lexTag.indexOf("-"));
        }

        // address pos tag notation differences between open-nlp and label-lex-sw
        for (String conversionKey : lexiconConversions.keySet()) {
            if (lexTag.equals(conversionKey)) {
                lexTag = lexiconConversions.get(conversionKey);
                break;
            }
        }

        // check for composed tokens
        if (breakOnHyphen && token.getWord().contains("-")) {
            String[] subwords = token.getWord().split("-");
            Token first = new Token(token.getStart(), subwords[0]);
            Token last = new Token(
                    token.getStart() + subwords[0].length() + 1,
                    subwords[1]);
            token.setWord(first.getWord() + "-" + last.getWord());
            return taggedToken;
        }
        if (breakOnUnderscore && token.getWord().contains("_")) {
            String[] subwords = token.getWord().split("_");
            Token first = new Token(token.getStart(), subwords[0]);
            Token last = new Token(
                    token.getStart() + subwords[0].length() + 1,
                    subwords[1]);
            token.setWord(first.getWord() + "-" + last.getWord());
            return taggedToken;
        }

        // check flags for determining which normalizations to perform
        if (checkFlag(ADVERB) && tag.toLowerCase().matches(adverbTags)) {

            // check dictionary
            if (dictionary.contains(token.getWord(), lexTag)
                    && !lexTag.matches(dictionaryExclusions)) {
                String[] lemmas = dictionary.retrieveLemmas(token.getWord(), lexTag);
                token.setWord(wordRanking.retrieveTopWord(lemmas));
                cache.put(key, token.getWord());
                return taggedToken;
            }

            // and then check rules
            adverbNormalizer.execute(taggedToken);
            if (lexicon.contains(token.getWord(), lexTag)) {
                cache.put(key, token.getWord());
                return taggedToken;
            }
        }

        if (checkFlag(NUMBER) &&
                tag.toLowerCase().matches(numberTags)) {

            // check dictionary
            if (dictionary.contains(token.getWord(), lexTag)
                    && !lexTag.matches(dictionaryExclusions)) {
                String[] lemmas = dictionary.retrieveLemmas(token.getWord(), lexTag);
                token.setWord(wordRanking.retrieveTopWord(lemmas));
                cache.put(key, token.getWord());
                return taggedToken;
            }

            // and then check rules
            numberNormalizer.execute(taggedToken);
            if (lexicon.contains(token.getWord(), lexTag)) {
                cache.put(key, token.getWord());
                return taggedToken;
            }
        }

        if (checkFlag(SUPERLATIVE) &&
                tag.toLowerCase().matches(superlativeTags)) {

            // check dictionary
            if (dictionary.contains(token.getWord(), lexTag)
                    && !lexTag.matches(dictionaryExclusions)) {
                String[] lemmas = dictionary.retrieveLemmas(token.getWord(), lexTag);
                token.setWord(wordRanking.retrieveTopWord(lemmas));
                cache.put(key, token.getWord());
                return taggedToken;
            }

            // and then check rules
            superlativeNormalizer.execute(taggedToken);
            if (lexicon.contains(token.getWord(), lexTag)) {
                cache.put(key, token.getWord());
                return taggedToken;
            }
        }

        if (checkFlag(AUGMENTATIVE) &&
                tag.toLowerCase().matches(augmentativeTags)) {

            // check dictionary
            if (dictionary.contains(token.getWord(), lexTag)
                    && !lexTag.matches(dictionaryExclusions)) {
                String[] lemmas = dictionary.retrieveLemmas(token.getWord(), lexTag);
                token.setWord(wordRanking.retrieveTopWord(lemmas));
                cache.put(key, token.getWord());
                return taggedToken;
            }

            // and then check rules
            augmentativeNormalizer.execute(taggedToken);
            if (lexicon.contains(token.getWord(), lexTag)) {
                cache.put(key, token.getWord());
                return taggedToken;
            }
        }

        if (checkFlag(DIMINUTIVE) &&
                tag.toLowerCase().matches(diminutiveTags)) {

            // check dictionary
            if (dictionary.contains(token.getWord(), lexTag)
                    && !lexTag.matches(dictionaryExclusions)) {
                String[] lemmas = dictionary.retrieveLemmas(token.getWord(), lexTag);
                token.setWord(wordRanking.retrieveTopWord(lemmas));
                cache.put(key, token.getWord());
                return taggedToken;
            }

            // and then check rules
            diminutiveNormalizer.execute(taggedToken);
            if (lexicon.contains(token.getWord(), lexTag)) {
                cache.put(key, token.getWord());
                return taggedToken;
            }
        }

        if (checkFlag(GENDER) &&
                tag.toLowerCase().matches(genderTags)) {

            // check dictionary
            if (dictionary.contains(token.getWord(), lexTag)
                    && !lexTag.matches(dictionaryExclusions)) {
                String[] lemmas = dictionary.retrieveLemmas(token.getWord(), lexTag);
                token.setWord(wordRanking.retrieveTopWord(lemmas));
                cache.put(key, token.getWord());
                return taggedToken;
            }

            // and then check rules
            genderNormalizer.execute(taggedToken);
            if (lexicon.contains(token.getWord(), lexTag)) {
                cache.put(key, token.getWord());
                return taggedToken;
            }
        }

        if (checkFlag(GENDER_NAME) &&
                tag.toLowerCase().matches(genderNameTags)) {

            // check dictionary
            if (dictionary.contains(token.getWord(), lexTag)
                    && !lexTag.matches(dictionaryExclusions)) {
                String[] lemmas = dictionary.retrieveLemmas(token.getWord(), lexTag);
                token.setWord(wordRanking.retrieveTopWord(lemmas));
                cache.put(key, token.getWord());
                return taggedToken;
            }

            // and then check rules
            genderNameNormalizer.execute(taggedToken);
            if (lexicon.contains(token.getWord(), lexTag)) {
                cache.put(key, token.getWord());
                return taggedToken;
            }
        }

        if (checkFlag(VERB) &&
                tag.toLowerCase().matches(verbTags)) {

            // check dictionary
            if (dictionary.contains(token.getWord(), lexTag)
                    && !lexTag.matches(dictionaryExclusions)) {
                String[] lemmas = dictionary.retrieveLemmas(token.getWord(), lexTag);
                token.setWord(wordRanking.retrieveTopWord(lemmas));
                cache.put(key, token.getWord());
                return taggedToken;
            }

            // and then check rules
            verbNormalizer.execute(taggedToken);
            if (lexicon.contains(token.getWord(), lexTag)) {
                cache.put(key, token.getWord());
            }
        }

        return taggedToken;
    }

    /**
     * Check if a flag is in the flags.
     *
     * @param flag {@code int} the flag to check
     * @return {@code boolean} flag is in flags?
     */
    private boolean checkFlag(int flag) {
        return (flags & flag) == flag;
    }
}
