package pt.up.hs.linguini.utils;

/**
 * Utilities to deal with strings.
 *
 * @author José Carlos Paiva <code>josepaiva94@gmail.com</code>
 */
public class StringUtils {
    private static final String PUNCTUATION = "^[,.:;?!_\\[\\]()\"`/*+%={}#$<>'«»\\\\|]+$";

    public static boolean isEmpty(String string) {
        return string == null || string.isEmpty();
    }

    public static boolean isBlankString(String string) {
        return string == null || string.trim().isEmpty();
    }

    public static boolean isPunctuation(String string) {
        return string != null && string.matches(PUNCTUATION);
    }

    public static boolean startsWithLetter(String string) {
        return string != null && string.matches("^[A-Za-z\\u00C0-\\u00D6\\u00D8-\\u00f6\\u00f8-\\u00ff].*");
    }
}
