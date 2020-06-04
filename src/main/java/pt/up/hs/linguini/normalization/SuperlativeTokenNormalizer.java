package pt.up.hs.linguini.normalization;

import pt.up.hs.linguini.models.Replacement;
import pt.up.hs.linguini.normalization.exceptions.NormalizationException;

import java.util.Locale;

/**
 * Normalizer of superlative declensions.
 *
 * @author José Carlos Paiva
 */
public class SuperlativeTokenNormalizer extends DeclensionTokenNormalizer {
    private static final String FILE_PATH_FORMAT =
            "/%s/replacements/superlativedeclensions.json";

    public SuperlativeTokenNormalizer() throws NormalizationException {
        this(Locale.getDefault());
    }

    public SuperlativeTokenNormalizer(Locale locale) throws NormalizationException {
        super(String.format(FILE_PATH_FORMAT, locale.toString()));
    }

    public SuperlativeTokenNormalizer(Replacement[] declensions) {
        super(declensions);
    }
}
