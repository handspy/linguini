package pt.up.hs.linguini.normalization.selection;

import pt.up.hs.linguini.models.Token;
import pt.up.hs.linguini.normalization.selection.exceptions.SelectionException;
import pt.up.hs.linguini.ranking.WordRanking;
import pt.up.hs.linguini.ranking.exceptions.WordRankingException;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Selection strategy that selects the most frequent token word from
 * a list.
 *
 * @author José Carlos Paiva <code>josepaiva94@gmail.com</code>
 */
public class MostFrequentStrategy implements TokenSelectionStrategy {

    private final Locale locale;
    private final WordRanking wordRanking;

    public MostFrequentStrategy(Locale locale) throws SelectionException {
        this.locale = locale;
        try {
            this.wordRanking = new WordRanking(locale);
        } catch (WordRankingException e) {
            throw new SelectionException("Loading word ranking", e);
        }
    }

    @Override
    public Token select(List<Token> items) throws SelectionException {
        if (items == null) {
            throw new SelectionException("Cannot select from null collection");
        }
        if (items.isEmpty()) {
            throw new SelectionException("No elements in collection");
        }
        Optional<Map.Entry<Token, Integer>> topEntry = items
                .stream()
                .collect(
                        Collectors.toConcurrentMap(
                                token -> token,
                                token -> wordRanking.getRank(token.getWord())
                        )
                )
                .entrySet()
                .stream()
                .min(Map.Entry.comparingByValue());
        if (!topEntry.isPresent()) {
            throw new SelectionException("No elements found after sort");
        }
        return topEntry
                .get()
                .getKey();
    }
}
