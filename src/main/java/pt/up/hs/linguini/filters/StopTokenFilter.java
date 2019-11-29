package pt.up.hs.linguini.filters;

import pt.up.hs.linguini.models.Token;
import pt.up.hs.linguini.exceptions.AnalyzerException;
import pt.up.hs.linguini.utils.InMemoryCache;
import pt.up.hs.linguini.utils.FileUtils;

import java.io.IOException;
import java.util.Collection;
import java.util.Locale;

/**
 * Token filter that accepts only stop words.
 *
 * @author José Carlos Paiva <code>josepaiva94@gmail.com</code>
 */
public class StopTokenFilter extends SimpleTokenFilter {
    private static final String STOPWORDS_PATH = "stopwords/%s/stopwords.txt";

    protected static InMemoryCache<String, Collection<String>> stopwordsCache =
            new InMemoryCache<>(86400, 3600, 20);

    private Locale locale;

    private Collection<String> stopwords;

    public StopTokenFilter(Locale locale) throws AnalyzerException {
        this.locale = locale;
        this.stopwords = loadStopWords();
    }

    @Override
    public boolean accept(Token token) {
        return !stopwords.contains(token.getWord());
    }

    private Collection<String> loadStopWords() throws AnalyzerException {
        String localeStr = locale.toString();
        Collection<String> stopwords = stopwordsCache.get(localeStr);
        if (stopwords == null) {
            try {
                stopwords = FileUtils.readAllLines(
                        String.format(STOPWORDS_PATH, localeStr));
                stopwordsCache.put(localeStr, stopwords);
            } catch (IOException e) {
                throw new AnalyzerException("Could not read stopwords.", e);
            }
        }
        return stopwords;
    }
}
