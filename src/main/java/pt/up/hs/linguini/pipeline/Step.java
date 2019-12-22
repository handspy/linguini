package pt.up.hs.linguini.pipeline;

import pt.up.hs.linguini.exceptions.LinguiniException;

/**
 * Interface for steps of Natural Language Processing.
 *
 * @author José Carlos Paiva <code>josepaiva94@gmail.com</code>
 */
public interface Step<I, O> {

    O execute(I value) throws LinguiniException;

    default <R> Step<I, R> pipe(Step<O, R> source) {
        return value -> source.execute(execute(value));
    }

    static <I, O> Step<I, O> of(Step<I, O> source) {
        return source;
    }
}
