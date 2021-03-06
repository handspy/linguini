package pt.up.hs.linguini.normalization;

import pt.up.hs.linguini.models.AnnotatedToken;
import pt.up.hs.linguini.models.Replacement;
import pt.up.hs.linguini.models.Token;
import pt.up.hs.linguini.normalization.exceptions.NormalizationException;

import java.util.regex.Pattern;

/**
 * Normalizer of declensions.
 *
 * @author José Carlos Paiva <code>josepaiva94@gmail.com</code>
 */
public class DeclensionTokenNormalizer extends ReplacementTokenNormalizer {

    protected Pattern[] declensionExceptions;
    protected Pattern[] declensionTargets;
    protected Pattern[] declensionTags;

    public DeclensionTokenNormalizer(String path)
            throws NormalizationException {
        super(path);
        initializePatterns();
    }

    public DeclensionTokenNormalizer(Replacement[] replacements) {
        super(replacements);
        initializePatterns();
    }

    private void initializePatterns() {
        declensionExceptions = new Pattern[this.replacements.length];
        declensionTargets = new Pattern[this.replacements.length];
        declensionTags = new Pattern[this.replacements.length];
        for (int i = 0; i < replacements.length; i++) {
            declensionExceptions[i] = Pattern.compile(
                    replacements[i].getExceptions());
            declensionTargets[i] = Pattern.compile(
                    replacements[i].getPrefix() +
                            replacements[i].getTarget() +
                            replacements[i].getSuffix());
            declensionTags[i] = Pattern.compile(replacements[i].getTag());
        }
    }

    @Override
    public AnnotatedToken<String> execute(AnnotatedToken<String> taggedToken) {
        Token token = taggedToken.getToken();
        String tag = taggedToken.getInfo();

        if (tag != null && !tag.toLowerCase().matches(getReplacementsTagsString())) {
            return taggedToken;
        }

        String normalizedWord = token.getWord().toLowerCase();
        for (int i = 0; i < replacements.length; i++) {
            if (
                    declensionTargets[i].matcher(normalizedWord).matches() &&
                            (tag == null || declensionTags[i].matcher(tag.toLowerCase()).matches()) &&
                            !declensionExceptions[i].matcher(normalizedWord).matches()
            ) {
                normalizedWord = normalizedWord
                        .substring(
                                0,
                                normalizedWord.length() -
                                        replacements[i].getTarget().length()
                        ) +
                        replacements[i].getReplacement();
                break;
            }
        }
        token.setWord(normalizedWord);
        return taggedToken;
    }
}
