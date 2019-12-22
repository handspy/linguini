package pt.up.hs.linguini.normalization;

import pt.up.hs.linguini.models.Replacement;
import pt.up.hs.linguini.normalization.exceptions.NormalizationException;

import java.util.Locale;

/**
 * Normalizer of gender declensions.
 *
 * @author José Carlos Paiva
 */
public class GenderTokenNormalizer extends DeclensionTokenNormalizer {
    private static final String FILE_PATH_FORMAT =
            "/%s/replacements/genderdeclensions.xml";

    public GenderTokenNormalizer() throws NormalizationException {
        this(Locale.getDefault());
    }

    public GenderTokenNormalizer(Locale locale) throws NormalizationException {
        super(String.format(FILE_PATH_FORMAT, locale.toString()));
    }

    public GenderTokenNormalizer(Replacement[] declensions) {
        super(declensions);
    }
}
