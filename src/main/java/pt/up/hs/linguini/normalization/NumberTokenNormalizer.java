package pt.up.hs.linguini.normalization;

import pt.up.hs.linguini.models.Replacement;
import pt.up.hs.linguini.normalization.exceptions.NormalizationException;

import java.util.Locale;

/**
 * Normalizer of number declensions.
 *
 * @author José Carlos Paiva
 */
public class NumberTokenNormalizer extends DeclensionTokenNormalizer {
    private static final String FILE_PATH_FORMAT =
            "/%s/replacements/numberdeclensions.xml";

    public NumberTokenNormalizer() throws NormalizationException {
        this(Locale.getDefault());
    }

    public NumberTokenNormalizer(Locale locale) throws NormalizationException {
        super(String.format(FILE_PATH_FORMAT, locale.toString()));
    }

    public NumberTokenNormalizer(Replacement[] declensions) {
        super(declensions);
    }
}
