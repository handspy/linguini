package pt.up.hs.linguini.normalization;

import pt.up.hs.linguini.models.Replacement;
import pt.up.hs.linguini.normalization.exceptions.NormalizationException;
import pt.up.hs.linguini.resources.ResourceLoader;
import pt.up.hs.linguini.resources.exceptions.ResourceLoadingException;

import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * Normalizer based on replacements.
 *
 * @author José Carlos Paiva <code>josepaiva94@gmail.com</code>
 */
public abstract class ReplacementTokenNormalizer
        implements TokenNormalizer {

    protected final Replacement[] replacements;

    public ReplacementTokenNormalizer(String path)
            throws NormalizationException {
        try {
            replacements = ResourceLoader.readReplacements(path);
        } catch (ResourceLoadingException e) {
            throw new NormalizationException("Could not load replacements", e);
        }
        Arrays.sort(replacements);
    }

    public ReplacementTokenNormalizer(Replacement[] replacements) {
        this.replacements = replacements;
        Arrays.sort(this.replacements);
    }

    public String getReplacementsTagsString() {
        return Arrays
                .stream(replacements)
                .map(Replacement::getTag)
                .distinct()
                .collect(Collectors.joining("|"));
    }
}
