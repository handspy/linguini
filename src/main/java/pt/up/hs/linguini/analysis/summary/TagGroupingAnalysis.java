package pt.up.hs.linguini.analysis.summary;

import pt.up.hs.linguini.analysis.Analysis;
import pt.up.hs.linguini.models.AnnotatedToken;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Group tagged tokens by tag.
 *
 * @author José Carlos Paiva <code>josepaiva94@gmail.com</code>
 */
public class TagGroupingAnalysis
        implements Analysis<List<AnnotatedToken<String>>, Map<String, Set<String>>> {
    private static final String DEFAULT_TAG = "OTHER";

    @Override
    public Map<String, Set<String>> execute(
            List<AnnotatedToken<String>> taggedTokens) {

        return taggedTokens.parallelStream()
                .collect(Collectors.groupingBy(
                        at -> {
                            String tag = at.getInfo();
                            if (tag != null) {
                                return tag.toUpperCase();
                            }
                            return DEFAULT_TAG;
                        },
                        Collectors.mapping(
                                at -> at.getToken().word(),
                                Collectors.toSet()
                        )
                ));
    }
}
