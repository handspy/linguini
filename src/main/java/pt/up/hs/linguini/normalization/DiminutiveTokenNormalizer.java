package pt.up.hs.linguini.normalization;

import pt.up.hs.linguini.models.Replacement;
import pt.up.hs.linguini.normalization.exceptions.NormalizationException;
import pt.up.hs.linguini.resources.ResourceLoader;
import pt.up.hs.linguini.resources.exceptions.ResourceLoadingException;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * Normalizer of diminutive declensions.
 *
 * @author José Carlos Paiva
 */
public class DiminutiveTokenNormalizer extends DeclensionTokenNormalizer {
    private static final String FILE_PATH_FORMAT =
            "/%s/replacements/diminutivedeclensions.xml";

    public DiminutiveTokenNormalizer() throws NormalizationException {
        this(Locale.getDefault());
    }

    public DiminutiveTokenNormalizer(Locale locale) throws NormalizationException {
        super(String.format(FILE_PATH_FORMAT, locale.toString()));
    }

    public DiminutiveTokenNormalizer(Replacement[] declensions) {
        super(declensions);
    }
}
