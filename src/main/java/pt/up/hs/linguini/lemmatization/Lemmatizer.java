package pt.up.hs.linguini.lemmatization;

import pt.up.hs.linguini.Config;
import pt.up.hs.linguini.caching.InMemoryCache;
import pt.up.hs.linguini.dictionaries.Dictionary;
import pt.up.hs.linguini.dictionaries.Lexicon;
import pt.up.hs.linguini.dictionaries.exceptions.DictionaryReadException;
import pt.up.hs.linguini.exceptions.ConfigException;
import pt.up.hs.linguini.exceptions.ReplacementException;
import pt.up.hs.linguini.lemmatization.exceptions.LemmatizationException;
import pt.up.hs.linguini.models.Token;
import pt.up.hs.linguini.normalizers.*;
import pt.up.hs.linguini.ranking.WordRanking;
import pt.up.hs.linguini.ranking.exceptions.WordRankingReadException;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * Convert a word to its lemma form.
 *
 * @author Ricardo Rodrigues
 * @author (modified by) José Carlos Paiva <code>josepaiva94@gmail.com</code>
 */
public class Lemmatizer {
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

    private LowercaseTokenNormalizer lowercaseTokenNormalizer;
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

        this.lowercaseTokenNormalizer = new LowercaseTokenNormalizer();

        // read normalizers' replacements
        try {
            this.adverbNormalizer = AdverbTokenNormalizer.getInstance(locale);
            this.augmentativeNormalizer = AugmentativeTokenNormalizer
                    .getInstance(locale);
            this.diminutiveNormalizer = DiminutiveTokenNormalizer.getInstance(locale);
            this.genderNormalizer = GenderTokenNormalizer.getInstance(locale);
            this.genderNameNormalizer = GenderNameTokenNormalizer.getInstance(locale);
            this.numberNormalizer = NumberTokenNormalizer.getInstance(locale);
            this.superlativeNormalizer = SuperlativeTokenNormalizer.getInstance(locale);
            this.verbNormalizer = VerbTokenNormalizer.getInstance(locale);
        } catch (ReplacementException e) {
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
            this.dictionary = Dictionary.getInstance(locale);
        } catch (DictionaryReadException e) {
            throw new LemmatizationException(
                    "Could not read dictionary", e);
        }

        // get lexicon
        this.lexicon = dictionary.retrieveLexicon();

        // read word ranking
        try {
            this.wordRanking = WordRanking.getInstance(locale);
        } catch (WordRankingReadException e) {
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

    public void lemmatize(Token[] tokens, String[] tags) {

        for (int i = 0; i < tokens.length; i++) {
            lemmatize(tokens[i], tags[i]);
        }
    }

    /**
     * This method retrieves the lemma of a given token, when classified with
     * a given <em>PoS tag</em>.
     *
     * @param  token the token whose lemma is wanted
     * @param  tag the <em>PoS tag</em> of the token
     * @return the lemma of the token (when classified with the given tag)
     */
    public void lemmatize(Token token, String tag) {

        // check for token|tag in cache
        LemmaCacheKey key = new LemmaCacheKey(
                token.getWord().toLowerCase(), tag.toLowerCase());
        if (cache.get(key) != null) {
            token.setWord(cache.get(key));
            return;
        }

        // normalize token/lemma
        lowercaseTokenNormalizer.normalize(token);

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
            return;
        }
        if (breakOnUnderscore && token.getWord().contains("_")) {
            String[] subwords = token.getWord().split("_");
            Token first = new Token(token.getStart(), subwords[0]);
            Token last = new Token(
                    token.getStart() + subwords[0].length() + 1,
                    subwords[1]);
            token.setWord(first.getWord() + "-" + last.getWord());
            return;
        }

        // check flags for determining which normalizations to perform
        if (checkFlag(ADVERB) && tag.toLowerCase().matches(adverbTags)) {

            // check dictionary
            if (dictionary.contains(token.getWord(), lexTag)
                    && !lexTag.matches(dictionaryExclusions)) {
                String[] lemmas = dictionary.retrieveLemmas(token.getWord(), lexTag);
                token.setWord(wordRanking.retrieveTopWord(lemmas));
                cache.put(key, token.getWord());
                return;
            }

            // and then check rules
            adverbNormalizer.normalize(token, tag);
            if (lexicon.contains(token.getWord(), lexTag)) {
                cache.put(key, token.getWord());
                return;
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
                return;
            }

            // and then check rules
            numberNormalizer.normalize(token, tag);
            if (lexicon.contains(token.getWord(), lexTag)) {
                cache.put(key, token.getWord());
                return;
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
                return;
            }

            // and then check rules
            superlativeNormalizer.normalize(token, tag);
            if (lexicon.contains(token.getWord(), lexTag)) {
                cache.put(key, token.getWord());
                return;
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
                return;
            }

            // and then check rules
            augmentativeNormalizer.normalize(token, tag);
            if (lexicon.contains(token.getWord(), lexTag)) {
                cache.put(key, token.getWord());
                return;
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
                return;
            }

            // and then check rules
            diminutiveNormalizer.normalize(token, tag);
            if (lexicon.contains(token.getWord(), lexTag)) {
                cache.put(key, token.getWord());
                return;
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
                return;
            }

            // and then check rules
            genderNormalizer.normalize(token, tag);
            if (lexicon.contains(token.getWord(), lexTag)) {
                cache.put(key, token.getWord());
                return;
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
                return;
            }

            // and then check rules
            genderNameNormalizer.normalize(token, tag);
            if (lexicon.contains(token.getWord(), lexTag)) {
                cache.put(key, token.getWord());
                return;
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
                return;
            }

            // and then check rules
            verbNormalizer.normalize(token, tag);
            if (lexicon.contains(token.getWord(), lexTag)) {
                cache.put(key, token.getWord());
            }
        }
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
