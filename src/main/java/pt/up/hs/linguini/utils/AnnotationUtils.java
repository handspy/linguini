package pt.up.hs.linguini.utils;

import pt.up.hs.linguini.models.Annotation;
import pt.up.hs.linguini.models.Token;

/**
 * Utilities to deal with annotations.
 *
 * @author José Carlos Paiva <code>josepaiva94@gmail.com</code>
 */
public class AnnotationUtils {

    public static  <T> Annotation<T> fromToken(Token token, T annot) {
        String word = token.getOriginal();
        return new Annotation<>(
                token.getStart(),
                word.length(),
                annot
        );
    }
}
