package pt.up.hs.linguini.analysis.summary;

import pt.up.hs.linguini.Config;
import pt.up.hs.linguini.analysis.Analysis;
import pt.up.hs.linguini.analysis.exceptions.AnalysisException;
import pt.up.hs.linguini.exceptions.ConfigException;
import pt.up.hs.linguini.exceptions.LinguiniException;
import pt.up.hs.linguini.models.AnnotatedToken;

import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

/**
 * Filter out the non-functional words.
 *
 * @author José Carlos Paiva <code>josepaiva94@gmail.com</code>
 */
public class FunctionalWordAnalysis
        implements Analysis<List<AnnotatedToken<String>>, List<AnnotatedToken<String>>> {

    private Locale locale;
    private String functionalWordTags;

    public FunctionalWordAnalysis() throws AnalysisException {
        this(Locale.getDefault());
    }

    public FunctionalWordAnalysis(Locale locale) throws AnalysisException {
        this.locale = locale;
        try {
            functionalWordTags = Config.getInstance(locale)
                    .getFunctionalWordTags();
        } catch (ConfigException e) {
            throw new AnalysisException(
                    "Could not load required configuration property for functional word analysis", e);
        }
    }

    @Override
    public List<AnnotatedToken<String>> execute(
            List<AnnotatedToken<String>> taggedTokens) {

        return taggedTokens.parallelStream()
                .filter(tt -> !tt.getInfo().toUpperCase().matches(functionalWordTags))
                .collect(Collectors.toList());
    }
}
