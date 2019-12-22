package pt.up.hs.linguini.normalization;

import pt.up.hs.linguini.models.Replacement;
import pt.up.hs.linguini.normalization.exceptions.NormalizationException;

import java.util.Locale;

/**
 * Normalizer of augmentative declensions.
 *
 * @author José Carlos Paiva
 */
public class AugmentativeTokenNormalizer extends DeclensionTokenNormalizer {
    private static final String FILE_PATH_FORMAT =
            "/%s/replacements/augmentativedeclensions.xml";

    public AugmentativeTokenNormalizer() throws NormalizationException {
        this(Locale.getDefault());
    }

    public AugmentativeTokenNormalizer(Locale locale) throws NormalizationException {
        super(String.format(FILE_PATH_FORMAT, locale.toString()));
    }

    private AugmentativeTokenNormalizer(Replacement[] declensions) {
        super(declensions);
    }
}
