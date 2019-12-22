package pt.up.hs.linguini.normalization;

import pt.up.hs.linguini.models.AnnotatedToken;
import pt.up.hs.linguini.models.Replacement;
import pt.up.hs.linguini.models.Token;
import pt.up.hs.linguini.normalization.exceptions.NormalizationException;

import java.util.Locale;
import java.util.regex.Pattern;

/**
 * Normalizer of gender name replacements.
 *
 * @author José Carlos Paiva
 */
public class GenderNameTokenNormalizer extends ReplacementTokenNormalizer {
    private static final String FILE_PATH_FORMAT =
            "/%s/replacements/gendernames.xml";

    private Pattern[] targets;
    private Pattern[] tags;

    public GenderNameTokenNormalizer() throws NormalizationException {
        this(Locale.getDefault());
    }

    public GenderNameTokenNormalizer(Locale locale) throws NormalizationException {
        super(String.format(FILE_PATH_FORMAT, locale.toString()));
        initializePatterns();
    }

    public GenderNameTokenNormalizer(Replacement[] declensions) {
        super(declensions);
        initializePatterns();
    }

    private void initializePatterns() {
        targets = new Pattern[this.replacements.length];
        tags = new Pattern[this.replacements.length];
        for (int i = 0; i < replacements.length; i++) {
            targets[i] = Pattern.compile(replacements[i].getPrefix() +
                    replacements[i].getTarget() + replacements[i].getSuffix());
            tags[i] = Pattern.compile(replacements[i].getTag());
        }
    }

    @Override
    public AnnotatedToken<String> execute(AnnotatedToken<String> taggedToken) {
        Token token = taggedToken.getToken();
        String tag = taggedToken.getInfo();

        if (tag != null && !tag.toLowerCase().matches(getReplacementsTagsString())) {
            return taggedToken;
        }

        String normalized = token.getWord().toLowerCase();

        // using gender specific nouns
        for (int i = 0; i < replacements.length; i++) {
            if (targets[i].matcher(normalized).matches() &&
                    (tag == null || tags[i].matcher(tag.toLowerCase()).matches())) {
                normalized = replacements[i].getReplacement();
                break;
            }
        }

        token.setWord(normalized);
        return taggedToken;
    }
}
