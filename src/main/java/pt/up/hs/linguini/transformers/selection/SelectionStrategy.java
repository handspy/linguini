package pt.up.hs.linguini.transformers.selection;

import pt.up.hs.linguini.transformers.selection.exceptions.SelectionException;

import java.util.List;

/**
 * Interface for strategies to select one element from a list.
 *
 * @author José Carlos Paiva <code>josepaiva94@gmail.com</code>
 */
public interface SelectionStrategy<T> {

    T select(List<T> items) throws SelectionException;
}
