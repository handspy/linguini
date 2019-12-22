package pt.up.hs.linguini.normalization.selection;

import pt.up.hs.linguini.models.Token;
import pt.up.hs.linguini.normalization.selection.exceptions.SelectionException;

import java.util.List;

/**
 * Selection strategy that selects the first element from a list.
 *
 * @author José Carlos Paiva <code>josepaiva94@gmail.com</code>
 */
public class ChooseFirstStrategy implements TokenSelectionStrategy {

    @Override
    public Token select(List<Token> items) throws SelectionException {
        if (items == null) {
            throw new SelectionException("Cannot select from null collection");
        }
        if (items.isEmpty()) {
            throw new SelectionException("No elements in collection");
        }
        return items.get(0);
    }
}
