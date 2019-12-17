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

/**
 * Normalizer of gender name replacements.
 *
 * @author Ricardo Rodrigues
 * @author (modified by) José Carlos Paiva
 */
public class GenderNameTokenNormalizer extends ReplacementTokenNormalizer {
    private static final String FILE_PATH_FORMAT =
            "/%s/replacements/gendernames.xml";

    private static Map<Locale, GenderNameTokenNormalizer> genderNamesNormalizers =
            new HashMap<>();

    private Pattern[] targets;
    private Pattern[] tags;

    private GenderNameTokenNormalizer(Replacement[] replacements) {
        super(replacements);
        Arrays.sort(this.replacements);
        targets = new Pattern[this.replacements.length];
        tags = new Pattern[this.replacements.length];
        for (int i = 0; i < replacements.length; i++) {
            targets[i] = Pattern.compile(replacements[i].getPrefix() +
                    replacements[i].getTarget() + replacements[i].getSuffix());
            tags[i] = Pattern.compile(replacements[i].getTag());
        }
    }

    public static GenderNameTokenNormalizer getInstance()
            throws ReplacementException {
        return getInstance(Locale.ENGLISH);
    }

    public static GenderNameTokenNormalizer getInstance(Locale locale)
            throws ReplacementException {
        if (!genderNamesNormalizers.containsKey(locale)) {
            String path = String.format(FILE_PATH_FORMAT, locale.toString());
            InputStream is = GenderNameTokenNormalizer.class
                    .getResourceAsStream(path);
            GenderNameTokenNormalizer normalizer = new GenderNameTokenNormalizer(
                    ResourceUtils.readReplacements(is));
            genderNamesNormalizers.put(locale, normalizer);
        }
        return genderNamesNormalizers.get(locale);
    }

    @Override
    public void normalize(Token token) {
        normalize(token, null);
    }

    @Override
    public void normalize(Token token, String tag) {
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
    }
}
