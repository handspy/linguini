package pt.up.hs.linguini.normalizers;

import pt.up.hs.linguini.exceptions.ReplacementException;
import pt.up.hs.linguini.models.Replacement;
import pt.up.hs.linguini.utils.ResourceUtils;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * Normalizer of gender declensions.
 *
 * @author Ricardo Rodrigues
 * @author (modified by) José Carlos Paiva
 */
public class GenderTokenNormalizer extends DeclensionTokenNormalizer {
    private static final String FILE_PATH_FORMAT =
            "/%s/replacements/genderdeclensions.xml";

    private static Map<Locale, GenderTokenNormalizer> genderNormalizers =
            new HashMap<>();

    private GenderTokenNormalizer(Replacement[] declensions) {
        super(declensions);
    }

    public static GenderTokenNormalizer getInstance()
            throws ReplacementException {
        return getInstance(Locale.ENGLISH);
    }

    public static GenderTokenNormalizer getInstance(Locale locale)
            throws ReplacementException {
        if (!genderNormalizers.containsKey(locale)) {
            String path = String.format(FILE_PATH_FORMAT, locale.toString());
            InputStream is = GenderTokenNormalizer.class
                    .getResourceAsStream(path);
            GenderTokenNormalizer normalizer = new GenderTokenNormalizer(
                    ResourceUtils.readReplacements(is));
            genderNormalizers.put(locale, normalizer);
        }
        return genderNormalizers.get(locale);
    }
}
