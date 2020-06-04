package pt.up.hs.linguini.models;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Replacement for a token.
 *
 * @author José Carlos Paiva <code>josepaiva94@gmail.com</code>
 */
public class Replacement implements Comparable<Replacement> {

    private String target = "";
    private String tag = "";
    private String prefix = "";
    private String suffix = "";
    private String exceptions = "";
    private String replacement = "";

    public Replacement() {
    }

    /**
     * Creates a new <code>Replacement</code> object.
     *
     * @param prefix      {@link String} Regex string for matching target words by
     *                    prefix.
     * @param target      {@link String} Target word.
     * @param suffix      {@link String} Regex string for matching target words by
     *                    suffix.
     * @param tag         {@link String} Tag of the replacement.
     * @param exceptions  {@link String} Exceptions of the replacement.
     * @param replacement {@link String} The replacement.
     */
    public Replacement(String prefix, String target, String suffix, String tag,
                       String exceptions, String replacement) {
        this.prefix = prefix;
        this.target = target;
        this.suffix = suffix;
        this.tag = tag;
        this.exceptions = exceptions;
        this.replacement = replacement;
    }

    public String getTarget() {
        return target;
    }

    public void setTarget(String target) {
        this.target = target;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
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

    public String getExceptions() {
        return exceptions;
    }

    public void setExceptions(String exceptions) {
        this.exceptions = exceptions;
    }

    public String getReplacement() {
        return replacement;
    }

    public void setReplacement(String replacement) {
        this.replacement = replacement;
    }

    /**
     * Returns a hash code for this <em>replacement</em>.
     *
     * @return a hash code value for this object
     */
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result
                + ((exceptions == null) ? 0 : exceptions.hashCode());
        result = prime * result + ((prefix == null) ? 0 : prefix.hashCode());
        result = prime * result
                + ((replacement == null) ? 0 : replacement.hashCode());
        result = prime * result + ((suffix == null) ? 0 : suffix.hashCode());
        result = prime * result + ((tag == null) ? 0 : tag.hashCode());
        result = prime * result + ((target == null) ? 0 : target.hashCode());
        return result;
    }

    /**
     * Checks if this <code>Replacement</code> is equal to another one.
     *
     * @param obj the other <code>Object</code> (<code>Replacement</code>)
     *            to be compared with this one
     * @return the value of the equality (<code>true</code> or
     * <code>false</code>)
     */
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        Replacement other = (Replacement) obj;
        if (exceptions == null) {
            if (other.exceptions != null) {
                return false;
            }
        } else if (!exceptions.equals(other.exceptions)) {
            return false;
        }
        if (prefix == null) {
            if (other.prefix != null) {
                return false;
            }
        } else if (!prefix.equals(other.prefix))
            return false;
        if (replacement == null) {
            if (other.replacement != null) {
                return false;
            }
        } else if (!replacement.equals(other.replacement)) {
            return false;
        }
        if (suffix == null) {
            if (other.suffix != null) {
                return false;
            }
        } else if (!suffix.equals(other.suffix)) {
            return false;
        }
        if (tag == null) {
            if (other.tag != null) {
                return false;
            }
        } else if (!tag.equals(other.tag)) {
            return false;
        }
        if (target == null) {
            return other.target == null;
        } else {
            return target.equals(other.target);
        }
    }

    /**
     * Compares two <em>replacements</em>, using the <em>length</em> value of
     * each of the <em>strings</em> that represent the attributes of the
     * <em>replacements</em>. The result is zero if the <em>replacements</em>
     * have the same length. The length of the target is one thousand times more
     * important than the length of the replacement, the length of the tag
     * (along with its prefix and suffix) and the length of the exceptions. This
     * method is needed for sorting the order by which the targets are matched
     * against the tokens, insuring that the larger (and more specific) are
     * tested first.
     *
     * @param other the other <code>Replacement</code> to be
     *              compared to this one
     * @return the value the corresponding to the lexicographic difference
     * between the attributes of both <em>replacements</em>
     */
    @Override
    public int compareTo(Replacement other) {
        // it must be used a "normalized" length of the targets to circumvent
        // situations where the use of regular expressions on them may alter the
        // intended order of both targets
        // as such, for now, all contents between squared brackets are reduced
        // to one unit in length, affecting thus the overall length of the targets
        int totalLength = this.computeNormalizedTargetLength(target) * 1000
                + exceptions.length() + replacement.length() + prefix.length()
                + tag.length() + suffix.length();
        int otherTotalLength =
                this.computeNormalizedTargetLength(other.getTarget()) * 1000
                        + other.getExceptions().length() + other.getReplacement().length()
                        + other.getPrefix().length() + other.getTag().length()
                        + other.getSuffix().length();
        return Integer.compare(otherTotalLength, totalLength);
    }

    private int computeNormalizedTargetLength(String target) {
        int normalizedLength = target.length();
        Matcher matcher = Pattern.compile("\\[.*?]").matcher(target);
        while (matcher.find()) {
            normalizedLength -= matcher.group().trim().length() - 1;
        }
        return normalizedLength;
    }

    /**
     * Creates and returns a copy of this object.
     *
     * @return a clone of this instance
     */
    @Override
    public Replacement clone() {
        return new Replacement(prefix, target, suffix, tag, exceptions,
                replacement);
    }
}
