package pt.up.hs.linguini.normalizers;

import pt.up.hs.linguini.exceptions.ReplacementException;
import pt.up.hs.linguini.models.Replacement;
import pt.up.hs.linguini.models.Token;
import pt.up.hs.linguini.utils.ResourceUtils;

import java.io.InputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Normalizer of verbs.
 *
 * @author Ricardo Rodrigues
 * @author (modified by) José Carlos Paiva
 */
public class VerbTokenNormalizer extends DeclensionTokenNormalizer {
    private static final String VERB_DECLENSIONS_FILE_PATH_FORMAT =
            "/%s/replacements/verbdeclensions.xml";
    private static final String VERB_IRREGULAR_CONJ_FILE_PATH_FORMAT =
            "/%s/replacements/verbirregularconj.xml";
    private static final String VERB_LEXEMES_FILE_PATH_FORMAT =
            "/%s/replacements/verblexemes.xml";

    private static Map<Locale, VerbTokenNormalizer> verbNormalizers =
            new HashMap<>();

    private Pattern[] conjugationTargetsWithoutPrefixes;
    private Pattern[] conjugationTargets;
    private Pattern[] conjugationTags;

    private Pattern[] lexemeTargetsWithoutPrefixes;
    private Pattern[] lexemeTargets;
    private Pattern[] lexemeTags;

    private Replacement[] conjugations;
    private Replacement[] lexemes;

    private VerbTokenNormalizer(Replacement[] conjugations, Replacement[] lexemes,
                                Replacement[] replacements) {
        super(replacements);
        this.conjugations = conjugations;
        this.lexemes = lexemes;
        Arrays.sort(this.conjugations);
        Arrays.sort(this.lexemes);
        conjugationTargetsWithoutPrefixes = new Pattern[this.conjugations.length];
        conjugationTargets = new Pattern[this.conjugations.length];
        conjugationTags = new Pattern[this.conjugations.length];
        for (int i = 0; i < conjugations.length; i++) {
            conjugationTargetsWithoutPrefixes[i] = Pattern.compile(
                    conjugations[i].getTarget() + conjugations[i].getSuffix());
            conjugationTargets[i] = Pattern.compile(conjugations[i].getPrefix()
                    + conjugations[i].getTarget() + conjugations[i].getSuffix());
            conjugationTags[i] = Pattern.compile(conjugations[i].getTag());
        }
        lexemeTargetsWithoutPrefixes = new Pattern[this.lexemes.length];
        lexemeTargets = new Pattern[this.lexemes.length];
        lexemeTags = new Pattern[this.lexemes.length];
        for (int i = 0; i < lexemes.length; i++) {
            lexemeTargetsWithoutPrefixes[i] = Pattern.compile(
                    lexemes[i].getTarget() + lexemes[i].getSuffix());
            lexemeTargets[i] = Pattern.compile(lexemes[i].getPrefix()
                    + lexemes[i].getTarget() + lexemes[i].getSuffix());
            lexemeTags[i] = Pattern.compile(lexemes[i].getTag());
        }
    }

    public static VerbTokenNormalizer getInstance()
            throws ReplacementException {
        return getInstance(Locale.ENGLISH);
    }

    public static VerbTokenNormalizer getInstance(Locale locale)
            throws ReplacementException {
        if (!verbNormalizers.containsKey(locale)) {
            String declensionsPath = String.format(
                    VERB_DECLENSIONS_FILE_PATH_FORMAT, locale.toString());
            InputStream declensionsIs = VerbTokenNormalizer.class
                    .getResourceAsStream(declensionsPath);
            String irregularConjPath = String.format(
                    VERB_IRREGULAR_CONJ_FILE_PATH_FORMAT, locale.toString());
            InputStream irregularConjIs = VerbTokenNormalizer.class
                    .getResourceAsStream(irregularConjPath);
            String lexemesPath = String.format(
                    VERB_LEXEMES_FILE_PATH_FORMAT, locale.toString());
            InputStream lexemesIs = VerbTokenNormalizer.class
                    .getResourceAsStream(lexemesPath);
            VerbTokenNormalizer normalizer = new VerbTokenNormalizer(
                    ResourceUtils.readReplacements(declensionsIs),
                    ResourceUtils.readReplacements(irregularConjIs),
                    ResourceUtils.readReplacements(lexemesIs));
            verbNormalizers.put(locale, normalizer);
        }
        return verbNormalizers.get(locale);
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
     * @param token the token whose lemma is wanted
     * @param tag   the <em>PoS tag</em> of the token
     */
    @Override
    public void normalize(Token token, String tag) {
        String currentNormalization = token.getWord().toLowerCase();
        String normalization = currentNormalization;
        boolean matchFound = false;

        // from inflections to lemmas (without prefixes)
        for (int i = 0; i < conjugations.length; i++) {
            if (conjugationTargetsWithoutPrefixes[i].matcher(
                    currentNormalization).matches()
                    && conjugationTags[i].matcher(tag.toLowerCase()).matches()) {
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
                        && conjugationTags[i].matcher(tag.toLowerCase()).matches()) {
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
                        && lexemeTags[i].matcher(tag.toLowerCase()).matches()) {
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
                        && lexemeTags[i].matcher(tag.toLowerCase()).matches()) {
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
            super.normalize(token, tag);

        token.setWord(normalization);
    }
}
