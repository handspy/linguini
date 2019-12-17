package pt.up.hs.linguini.normalizers;

import pt.up.hs.linguini.exceptions.ReplacementException;
import pt.up.hs.linguini.models.Replacement;
import pt.up.hs.linguini.utils.ResourceUtils;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * Normalizer of diminutive declensions.
 *
 * @author Ricardo Rodrigues
 * @author (modified by) José Carlos Paiva
 */
public class DiminutiveTokenNormalizer extends DeclensionTokenNormalizer {
    private static final String FILE_PATH_FORMAT =
            "/%s/replacements/diminutivedeclensions.xml";

    private static Map<Locale, DiminutiveTokenNormalizer> diminutiveNormalizers =
            new HashMap<>();

    private DiminutiveTokenNormalizer(Replacement[] declensions) {
        super(declensions);
    }

    public static DiminutiveTokenNormalizer getInstance()
            throws ReplacementException {
        return getInstance(Locale.ENGLISH);
    }

    public static DiminutiveTokenNormalizer getInstance(Locale locale)
            throws ReplacementException {
        if (!diminutiveNormalizers.containsKey(locale)) {
            String path = String.format(FILE_PATH_FORMAT, locale.toString());
            InputStream is = DiminutiveTokenNormalizer.class
                    .getResourceAsStream(path);
            DiminutiveTokenNormalizer normalizer = new DiminutiveTokenNormalizer(
                    ResourceUtils.readReplacements(is));
            diminutiveNormalizers.put(locale, normalizer);
        }
        return diminutiveNormalizers.get(locale);
    }
}
