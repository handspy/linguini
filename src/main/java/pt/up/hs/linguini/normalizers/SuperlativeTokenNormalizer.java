package pt.up.hs.linguini.normalizers;

import pt.up.hs.linguini.exceptions.ReplacementException;
import pt.up.hs.linguini.models.Replacement;
import pt.up.hs.linguini.utils.ResourceUtils;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * Normalizer of superlative declensions.
 *
 * @author Ricardo Rodrigues
 * @author (modified by) José Carlos Paiva
 */
public class SuperlativeTokenNormalizer extends DeclensionTokenNormalizer {
    private static final String FILE_PATH_FORMAT =
            "/%s/replacements/superlativedeclensions.xml";

    private static Map<Locale, SuperlativeTokenNormalizer> superlativeNormalizers =
            new HashMap<>();

    private SuperlativeTokenNormalizer(Replacement[] declensions) {
        super(declensions);
    }

    public static SuperlativeTokenNormalizer getInstance()
            throws ReplacementException {
        return getInstance(Locale.ENGLISH);
    }

    public static SuperlativeTokenNormalizer getInstance(Locale locale)
            throws ReplacementException {
        if (!superlativeNormalizers.containsKey(locale)) {
            String path = String.format(FILE_PATH_FORMAT, locale.toString());
            InputStream is = SuperlativeTokenNormalizer.class
                    .getResourceAsStream(path);
            SuperlativeTokenNormalizer normalizer = new SuperlativeTokenNormalizer(
                    ResourceUtils.readReplacements(is));
            superlativeNormalizers.put(locale, normalizer);
        }
        return superlativeNormalizers.get(locale);
    }
}
