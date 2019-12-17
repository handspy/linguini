package pt.up.hs.linguini.normalizers;

import pt.up.hs.linguini.models.Replacement;

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
