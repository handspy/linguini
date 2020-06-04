package pt.up.hs.linguini.models;

import java.util.Arrays;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Set of replacements for tokens.
 *
 * @author José Carlos Paiva <code>josepaiva94@gmail.com</code>
 */
public class Replacements {

    private String prefix = "";
    private String suffix = "";
    private Replacement[] replacements;

    public Replacements() {
    }

    /**
     * Creates a new <code>Replacement</code> object.
     *
     * @param prefix      {@link String} Regex string for matching target words by
     *                    prefix.
     * @param suffix      {@link String} Regex string for matching target words by
     *                    suffix.
     * @param replacements{@link Replacement[]} The replacements.
     */
    public Replacements(String prefix, String suffix, Replacement[] replacements) {
        this.prefix = prefix;
        this.suffix = suffix;
        this.replacements = replacements;
        if (replacements != null) {
            for (Replacement replacement: replacements) {
                replacement.setPrefix(prefix);
                replacement.setSuffix(suffix);
            }
        }
    }

    public String getPrefix() {
        return prefix;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    public String getSuffix() {
        return suffix;
    }

    public void setSuffix(String suffix) {
        this.suffix = suffix;
    }

    public Replacement[] getReplacements() {
        if (replacements != null) {
            for (Replacement replacement: replacements) {
                replacement.setPrefix(prefix);
                replacement.setSuffix(suffix);
            }
        }
        return replacements;
    }

    public void setReplacement(Replacement[] replacements) {
        this.replacements = replacements;
        if (replacements != null) {
            for (Replacement replacement: replacements) {
                replacement.setPrefix(prefix);
                replacement.setSuffix(suffix);
            }
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Replacements that = (Replacements) o;
        return Objects.equals(getPrefix(), that.getPrefix()) &&
                Objects.equals(getSuffix(), that.getSuffix()) &&
                Arrays.equals(getReplacements(), that.getReplacements());
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(getPrefix(), getSuffix());
        result = 31 * result + Arrays.hashCode(getReplacements());
        return result;
    }

    /**
     * Creates and returns a copy of this object.
     *
     * @return a clone of this instance
     */
    @Override
    public Replacements clone() {
        return new Replacements(prefix, suffix, replacements);
    }
}
