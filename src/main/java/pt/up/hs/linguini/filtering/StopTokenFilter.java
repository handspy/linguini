package pt.up.hs.linguini.filtering;

import pt.up.hs.linguini.filtering.exceptions.FilteringException;
import pt.up.hs.linguini.models.HasWord;
import pt.up.hs.linguini.models.Token;
import pt.up.hs.linguini.resources.ResourceLoader;
import pt.up.hs.linguini.resources.exceptions.ResourceLoadingException;

import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

/**
 * Token filter that accepts only stop words.
 *
 * @author José Carlos Paiva <code>josepaiva94@gmail.com</code>
 */
public class StopTokenFilter<T extends HasWord> implements TokenFilter<T> {
    private static final String STOPWORDS_PATH = "/%s/stopwords/stopwords.txt";

    private final Locale locale;

    private final Collection<String> stopwords;

    public StopTokenFilter(Locale locale) throws FilteringException {
        this.locale = locale;
        try {
            this.stopwords = ResourceLoader.readStopwords(
                    String.format(STOPWORDS_PATH, locale.toString())
            );
        } catch (ResourceLoadingException e) {
            throw new FilteringException("Could not read stopwords.", e);
        }
    }

    @Override
    public List<T> execute(List<T> tokens) {
        return tokens
                .parallelStream()
                .filter(token -> !stopwords.contains(token.word().toLowerCase(locale)))
                .collect(Collectors.toList());
    }
}
