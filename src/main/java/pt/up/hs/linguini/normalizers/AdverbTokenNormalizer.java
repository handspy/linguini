package pt.up.hs.linguini.normalizers;

import pt.up.hs.linguini.exceptions.ReplacementException;
import pt.up.hs.linguini.models.Replacement;
import pt.up.hs.linguini.utils.ResourceUtils;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * [Description here]
 *
 * @author José Carlos Paiva <code>josepaiva94@gmail.com</code>
 */
public class AdverbTokenNormalizer extends DeclensionTokenNormalizer {
    private static final String FILE_PATH_FORMAT =
            "/%s/replacements/adverbdeclensions.xml";

    private static Map<Locale, AdverbTokenNormalizer> adverbNormalizers =
            new HashMap<>();

    private AdverbTokenNormalizer(Replacement[] declensions) {
        super(declensions);
    }

    public static AdverbTokenNormalizer getInstance()
            throws ReplacementException {
        return getInstance(Locale.ENGLISH);
    }

    public static AdverbTokenNormalizer getInstance(Locale locale)
            throws ReplacementException {
        if (!adverbNormalizers.containsKey(locale)) {
            String path = String.format(FILE_PATH_FORMAT, locale.toString());
            InputStream is = AdverbTokenNormalizer.class
                    .getResourceAsStream(path);
            AdverbTokenNormalizer normalizer =
                    new AdverbTokenNormalizer(
                            ResourceUtils.readReplacements(is)
                    );
            adverbNormalizers.put(locale, normalizer);
        }
        return adverbNormalizers.get(locale);
    }
}
