package pt.up.hs.linguini.utils;

/**
 * Utilities to deal with strings.
 *
 * @author José Carlos Paiva <code>josepaiva94@gmail.com</code>
 */
public class StringUtils {
    private static final String PUNCTUATION = "[,.:;?!_\\[\\]()\"`/*+%={}#$<>'«»\\\\|”“]";
    private static final String ONLY_PUNCTUATION = "^" + PUNCTUATION + "+$";

    public static boolean isEmpty(String string) {
        return string == null || string.isEmpty();
    }

    public static boolean isBlankString(String string) {
        return string == null || string.trim().isEmpty();
    }

    public static boolean isPunctuation(String string) {
        return string != null && string.matches(ONLY_PUNCTUATION);
    }

    public static boolean hasPunctuation(String string) {
        return string != null && string.matches(".*" + PUNCTUATION + ".*");
    }

    public static String separatePunctuation(String string) {
        return string.replaceAll("(" + PUNCTUATION + ")", " $1 ");
    }

    public static boolean startsWithLetter(String string) {
        return string != null && string.matches("^[A-Za-z\\u00C0-\\u00D6\\u00D8-\\u00f6\\u00f8-\\u00ff].*");
    }


}
