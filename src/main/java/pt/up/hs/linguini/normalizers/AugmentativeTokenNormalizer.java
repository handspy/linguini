package pt.up.hs.linguini.normalizers;

import pt.up.hs.linguini.exceptions.ReplacementException;
import pt.up.hs.linguini.models.Replacement;
import pt.up.hs.linguini.utils.ResourceUtils;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * Normalizer of augmentative declensions.
 *
 * @author Ricardo Rodrigues
 * @author (modified by) José Carlos Paiva
 */
public class AugmentativeTokenNormalizer extends DeclensionTokenNormalizer {
    private static final String FILE_PATH_FORMAT =
            "/%s/replacements/augmentativedeclensions.xml";

    private static Map<Locale, AugmentativeTokenNormalizer> augmentativeNormalizers =
            new HashMap<>();

    private AugmentativeTokenNormalizer(Replacement[] declensions) {
        super(declensions);
    }

    public static AugmentativeTokenNormalizer getInstance()
            throws ReplacementException {
        return getInstance(Locale.ENGLISH);
    }

    public static AugmentativeTokenNormalizer getInstance(Locale locale)
            throws ReplacementException {
        if (!augmentativeNormalizers.containsKey(locale)) {
            String path = String.format(FILE_PATH_FORMAT, locale.toString());
            InputStream is = AugmentativeTokenNormalizer.class
                    .getResourceAsStream(path);
            AugmentativeTokenNormalizer normalizer = new AugmentativeTokenNormalizer(
                    ResourceUtils.readReplacements(is));
            augmentativeNormalizers.put(locale, normalizer);
        }
        return augmentativeNormalizers.get(locale);
    }
}
