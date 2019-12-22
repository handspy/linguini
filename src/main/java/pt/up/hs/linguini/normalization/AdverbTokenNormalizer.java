package pt.up.hs.linguini.normalization;

import pt.up.hs.linguini.models.Replacement;
import pt.up.hs.linguini.normalization.exceptions.NormalizationException;

import java.util.Locale;

/**
 * Normalizer of adverb declensions.
 *
 * @author José Carlos Paiva <code>josepaiva94@gmail.com</code>
 */
public class AdverbTokenNormalizer extends DeclensionTokenNormalizer {
    private static final String FILE_PATH_FORMAT =
            "/%s/replacements/adverbdeclensions.xml";

    public AdverbTokenNormalizer() throws NormalizationException {
        this(Locale.getDefault());
    }

    public AdverbTokenNormalizer(Locale locale) throws NormalizationException {
        super(String.format(FILE_PATH_FORMAT, locale.toString()));
    }

    public AdverbTokenNormalizer(Replacement[] replacements) {
        super(replacements);
    }
}
