package pt.up.hs.linguini.analysis;

import pt.up.hs.linguini.exceptions.AnalyzerException;
import pt.up.hs.linguini.jspell.JSpellInfo;
import pt.up.hs.linguini.jspell.JSpellWordAnnotator;
import pt.up.hs.linguini.models.AnnotatedToken;
import pt.up.hs.linguini.models.Token;
import pt.up.hs.linguini.transformers.LowercaseTokenTransformer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

/**
 * Analysis based on JSpell.
 *
 * @author José Carlos Paiva <code>josepaiva94@gmail.com</code>
 */
public abstract class JSpellPreprocessingAnalysis<V>
        implements Analysis<List<AnnotatedToken<JSpellInfo>>, V> {

    protected Locale locale;

    protected List<Token> tokens = null;
    protected List<AnnotatedToken<JSpellInfo>> jSpellAnnotatedTokens = null;

    public JSpellPreprocessingAnalysis() {
        this(Locale.ENGLISH);
    }

    public JSpellPreprocessingAnalysis(Locale locale) {
        this.locale = locale;
    }

    @Override
    public JSpellPreprocessingAnalysis<V> preprocess(
            List<Token> tokens) throws AnalyzerException {

        // transform to lowercase
        LowercaseTokenTransformer lcTransformer = new LowercaseTokenTransformer();
        tokens = tokens.parallelStream()
                .map(lcTransformer::transform)
                .collect(Collectors.toList());

        // annotate words
        JSpellWordAnnotator wordAnnotator;
        try {
            wordAnnotator = new JSpellWordAnnotator(locale);
        } catch (IOException e) {
            throw new AnalyzerException("Failed to build word annotator.", e);
        }

        jSpellAnnotatedTokens = new ArrayList<>();
        for (Token token: tokens) {
            try {
                AnnotatedToken<JSpellInfo> annotatedToken =
                        wordAnnotator.annotate(token);
                jSpellAnnotatedTokens.add(annotatedToken);
            } catch (IOException e) {
                throw new AnalyzerException(
                        String.format("Failed to analyze word '%s'.", token.getWord()), e);
            }
        }

        try {
            wordAnnotator.close();
        } catch (IOException e) {
            throw new AnalyzerException("Failed to close word annotator.", e);
        }

        this.tokens = tokens;

        return this;
    }

    @Override
    public Analysis<List<AnnotatedToken<JSpellInfo>>, V> skipPreprocessing(
            List<Token> tokens,
            List<AnnotatedToken<JSpellInfo>> jSpellAnnotatedTokens
    ) throws AnalyzerException {
        this.tokens = tokens;
        this.jSpellAnnotatedTokens = jSpellAnnotatedTokens;
        return this;
    }
}
