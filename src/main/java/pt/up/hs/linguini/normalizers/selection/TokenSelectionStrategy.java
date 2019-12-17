package pt.up.hs.linguini.normalizers.selection;

import pt.up.hs.linguini.models.Token;
import pt.up.hs.linguini.normalizers.selection.exceptions.SelectionException;

import java.util.List;

/**
 * Interface for strategies to select one token from a list.
 *
 * @author José Carlos Paiva <code>josepaiva94@gmail.com</code>
 */
public interface TokenSelectionStrategy {

    Token select(List<Token> items) throws SelectionException;
}
