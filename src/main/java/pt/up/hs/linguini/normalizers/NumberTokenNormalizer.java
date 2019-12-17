package pt.up.hs.linguini.normalizers;

import pt.up.hs.linguini.exceptions.ReplacementException;
import pt.up.hs.linguini.models.Replacement;
import pt.up.hs.linguini.utils.ResourceUtils;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * Normalizer of number declensions.
 *
 * @author Ricardo Rodrigues
 * @author (modified by) José Carlos Paiva
 */
public class NumberTokenNormalizer extends DeclensionTokenNormalizer {
    private static final String FILE_PATH_FORMAT =
            "/%s/replacements/numberdeclensions.xml";

    private static Map<Locale, NumberTokenNormalizer> numberNormalizers =
            new HashMap<>();

    private NumberTokenNormalizer(Replacement[] declensions) {
        super(declensions);
    }

    public static NumberTokenNormalizer getInstance()
            throws ReplacementException {
        return getInstance(Locale.ENGLISH);
    }

    public static NumberTokenNormalizer getInstance(Locale locale)
            throws ReplacementException {
        if (!numberNormalizers.containsKey(locale)) {
            String path = String.format(FILE_PATH_FORMAT, locale.toString());
            InputStream is = NumberTokenNormalizer.class
                    .getResourceAsStream(path);
            NumberTokenNormalizer normalizer = new NumberTokenNormalizer(
                    ResourceUtils.readReplacements(is));
            numberNormalizers.put(locale, normalizer);
        }
        return numberNormalizers.get(locale);
    }
}
