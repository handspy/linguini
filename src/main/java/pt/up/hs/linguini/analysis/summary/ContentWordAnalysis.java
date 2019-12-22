package pt.up.hs.linguini.analysis.summary;

import pt.up.hs.linguini.Config;
import pt.up.hs.linguini.analysis.Analysis;
import pt.up.hs.linguini.analysis.exceptions.AnalysisException;
import pt.up.hs.linguini.exceptions.ConfigException;
import pt.up.hs.linguini.models.AnnotatedToken;

import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

/**
 * Filter out the non-content words.
 *
 * @author José Carlos Paiva <code>josepaiva94@gmail.com</code>
 */
public class ContentWordAnalysis
        implements Analysis<List<AnnotatedToken<String>>, List<AnnotatedToken<String>>> {

    private Locale locale;
    private String contentWordTags;

    public ContentWordAnalysis() throws AnalysisException {
        this(Locale.getDefault());
    }

    public ContentWordAnalysis(Locale locale) throws AnalysisException {
        this.locale = locale;
        try {
            contentWordTags = Config.getInstance(locale)
                    .getContentWordTags();
        } catch (ConfigException e) {
            throw new AnalysisException(
                    "Could not load required configuration property for content word analysis", e);
        }
    }

    @Override
    public List<AnnotatedToken<String>> execute(
            List<AnnotatedToken<String>> taggedTokens) {

        return taggedTokens.parallelStream()
                .filter(tt -> tt.getInfo().toUpperCase().matches(contentWordTags))
                .collect(Collectors.toList());
    }
}
