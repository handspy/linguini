package pt.up.hs.linguini.normalization;

import pt.up.hs.linguini.models.AnnotatedToken;
import pt.up.hs.linguini.models.Replacement;
import pt.up.hs.linguini.models.Token;
import pt.up.hs.linguini.normalization.exceptions.NormalizationException;
import pt.up.hs.linguini.resources.ResourceLoader;
import pt.up.hs.linguini.resources.exceptions.ResourceLoadingException;

import java.util.Arrays;
import java.util.Locale;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Normalizer of verbs.
 *
 * @author José Carlos Paiva
 */
public class VerbTokenNormalizer extends DeclensionTokenNormalizer {
    private static final String VERB_DECLENSIONS_FILE_PATH_FORMAT =
            "/%s/replacements/verbdeclensions.xml";
    private static final String VERB_IRREGULAR_CONJ_FILE_PATH_FORMAT =
            "/%s/replacements/verbirregularconj.xml";
    private static final String VERB_LEXEMES_FILE_PATH_FORMAT =
            "/%s/replacements/verblexemes.xml";

    private Pattern[] conjugationTargetsWithoutPrefixes;
    private Pattern[] conjugationTargets;
    private Pattern[] conjugationTags;

    private Pattern[] lexemeTargetsWithoutPrefixes;
    private Pattern[] lexemeTargets;
    private Pattern[] lexemeTags;

    private Replacement[] conjugations;
    private Replacement[] lexemes;

    public VerbTokenNormalizer() throws NormalizationException {
        this(Locale.getDefault());
    }

    public VerbTokenNormalizer(Locale locale) throws NormalizationException {
        super(String.format(VERB_DECLENSIONS_FILE_PATH_FORMAT, locale.toString()));
        try {
            this.conjugations = ResourceLoader.readReplacements(
                    String.format(VERB_IRREGULAR_CONJ_FILE_PATH_FORMAT, locale.toString())
            );
            this.lexemes = ResourceLoader.readReplacements(
                    String.format(VERB_LEXEMES_FILE_PATH_FORMAT, locale.toString())
            );
        } catch (ResourceLoadingException e) {
            throw new NormalizationException("Could not load replacements", e);
        }
        initialize();
    }

    public VerbTokenNormalizer(Replacement[] conjugations, Replacement[] lexemes,
                               Replacement[] replacements) {
        super(replacements);
        this.conjugations = conjugations;
        this.lexemes = lexemes;
        initialize();
    }

    private void initialize() {
        Arrays.sort(conjugations);
        Arrays.sort(lexemes);
        conjugationTargetsWithoutPrefixes = new Pattern[conjugations.length];
        conjugationTargets = new Pattern[conjugations.length];
        conjugationTags = new Pattern[conjugations.length];
        for (int i = 0; i < conjugations.length; i++) {
            conjugationTargetsWithoutPrefixes[i] = Pattern.compile(
                    conjugations[i].getTarget() + conjugations[i].getSuffix());
            conjugationTargets[i] = Pattern.compile(conjugations[i].getPrefix()
                    + conjugations[i].getTarget() + conjugations[i].getSuffix());
            conjugationTags[i] = Pattern.compile(conjugations[i].getTag());
        }
        lexemeTargetsWithoutPrefixes = new Pattern[lexemes.length];
        lexemeTargets = new Pattern[lexemes.length];
        lexemeTags = new Pattern[lexemes.length];
        for (int i = 0; i < lexemes.length; i++) {
            lexemeTargetsWithoutPrefixes[i] = Pattern.compile(
                    lexemes[i].getTarget() + lexemes[i].getSuffix());
            lexemeTargets[i] = Pattern.compile(lexemes[i].getPrefix()
                    + lexemes[i].getTarget() + lexemes[i].getSuffix());
            lexemeTags[i] = Pattern.compile(lexemes[i].getTag());
        }
    }

    public String getReplacementsTagsString() {
        return Stream.concat(
                        Stream.concat(
                                Arrays.stream(replacements),
                                Arrays.stream(conjugations)
                        ),
                        Arrays.stream(lexemes)
                )
                .map(Replacement::getTag)
                .distinct()
                .collect(Collectors.joining("|"));
    }

    /**
     * This method retrieves the infinitive form of a given verb, if it
     * exists, when classified with a given <em>PoS tag</em>. Otherwise, it
     * returns the same token (in lower case).
     *
     * @param taggedToken the token whose lemma is wanted annotated with
     *                    the <em>PoS tag</em> of the token
     */
    @Override
    public AnnotatedToken<String> execute(AnnotatedToken<String> taggedToken) {
        Token token = taggedToken.getToken();
        String tag = taggedToken.getInfo();

        if (tag != null && !tag.toLowerCase().matches(getReplacementsTagsString())) {
            return taggedToken;
        }

        String currentNormalization = token.getWord().toLowerCase();
        String normalization = currentNormalization;
        boolean matchFound = false;

        // from inflections to lemmas (without prefixes)
        for (int i = 0; i < conjugations.length; i++) {
            if (conjugationTargetsWithoutPrefixes[i].matcher(
                    currentNormalization).matches()
                    && (tag == null || conjugationTags[i].matcher(tag.toLowerCase()).matches())) {
                if (currentNormalization.split(
                        conjugations[i].getTarget()).length > 0) {
                    String verbPrefix = token.getWord().split(conjugations[i].getTarget())[0];
                    normalization = verbPrefix + conjugations[i].getReplacement();
                } else {
                    normalization = conjugations[i].getReplacement();
                }
                matchFound = true;
                break;
            }
        }

        // from inflections to lemmas (with prefixes)
        if (!matchFound) {
            for (int i = 0; i < conjugations.length; i++) {
                if (conjugationTargets[i].matcher(currentNormalization).matches()
                        && (tag == null || conjugationTags[i].matcher(tag.toLowerCase()).matches())) {
                    // check whether the current inflection is from a verb with a prefix
                    // and appends it (the prefix) to the replacement
                    if (currentNormalization.split(
                            conjugations[i].getTarget()).length > 0) {
                        String verbPrefix = token.getWord().split(conjugations[i].getTarget())[0];
                        normalization = verbPrefix + conjugations[i].getReplacement();
                    } else {
                        normalization = conjugations[i].getReplacement();
                    }
                    matchFound = true;
                    break;
                }
            }
        }

        // from lexemes to lemmas (without prefixes)
        if (!matchFound) {
            for (int i = 0; i < lexemes.length; i++) {
                if (lexemeTargetsWithoutPrefixes[i].matcher(
                        currentNormalization).matches()
                        && (tag == null || lexemeTags[i].matcher(tag.toLowerCase()).matches())) {
                    if (currentNormalization.split(lexemes[i].getTarget()).length > 0) {
                        String verbPrefix = token.getWord().split(lexemes[i].getTarget())[0];
                        normalization = verbPrefix + lexemes[i].getReplacement();
                    } else {
                        normalization = lexemes[i].getReplacement();
                    }
                    matchFound = true;
                    break;
                }
            }
        }

        // from lexemes to lemmas (with prefixes)
        if (!matchFound) {
            for (int i = 0; i < lexemes.length; i++) {
                if (lexemeTargets[i].matcher(currentNormalization).matches()
                        && (tag == null || lexemeTags[i].matcher(tag.toLowerCase()).matches())) {
                    // check whether the current lexeme is from a verb with a prefix
                    // and appends it (the prefix) to the replacement
                    if (currentNormalization.split(lexemes[i].getTarget()).length > 0) {
                        String verbPrefix = token.getWord().split(lexemes[i].getTarget())[0];
                        normalization = verbPrefix + lexemes[i].getReplacement();
                    } else {
                        normalization = lexemes[i].getReplacement();
                    }
                    matchFound = true;
                    break;
                }
            }
        }

        // from declensions to lemmas (when everything else fails)
        if (!matchFound)
            super.execute(taggedToken);

        token.setWord(normalization);

        return taggedToken;
    }
}
