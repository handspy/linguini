package pt.up.hs.linguini.utils;

import java.util.function.Predicate;

/**
 * Utility predicates that are not part of Java 8 core.
 *
 * @author José Carlos Paiva <code>josepaiva94@gmail.com</code>
 */
public class Predicates {

    public static <T> Predicate<T> not(Predicate<T> t) {
        return t.negate();
    }
}
