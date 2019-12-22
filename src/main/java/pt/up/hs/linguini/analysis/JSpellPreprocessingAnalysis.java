package pt.up.hs.linguini.analysis;

/**
 * Analysis based on JSpell.
 *
 * @author José Carlos Paiva <code>josepaiva94@gmail.com</code>
 */
public abstract class JSpellPreprocessingAnalysis/*<T> implements Analysis<T>*/ {

    /*protected Locale locale;

    protected List<Token> tokens = null;
    protected List<AnnotatedToken<JSpellInfo>> jSpellAnnotatedTokens = null;

    public JSpellPreprocessingAnalysis() {
        this(Locale.ENGLISH);
    }

    public JSpellPreprocessingAnalysis(Locale locale) {
        this.locale = locale;
    }

    @Override
    public JSpellPreprocessingAnalysis<T> preprocess(String text) throws AnalysisException {

        // normalize to lowercase
        LowercaseTokenNormalizer lcTransformer = new LowercaseTokenNormalizer();
        tokens = tokens
                .parallelStream()
                .peek(lcTransformer::normalize)
                .collect(Collectors.toList());

        // annotate words
        JSpellWordAnnotator wordAnnotator;
        try {
            wordAnnotator = new JSpellWordAnnotator(locale);
        } catch (IOException e) {
            throw new AnalysisException("Failed to build word annotator.", e);
        }

        jSpellAnnotatedTokens = new ArrayList<>();
        for (Token token: tokens) {
            try {
                AnnotatedToken<JSpellInfo> annotatedToken =
                        wordAnnotator.annotate(token);
                jSpellAnnotatedTokens.add(annotatedToken);
            } catch (IOException e) {
                throw new AnalysisException(
                        String.format("Failed to analyze word '%s'.", token.getWord()), e);
            }
        }

        try {
            wordAnnotator.close();
        } catch (IOException e) {
            throw new AnalysisException("Failed to close word annotator.", e);
        }

        this.tokens = tokens;

        return this;
    }

    @Override
    public Analysis<List<AnnotatedToken<JSpellInfo>>, V> skipPreprocessing(
            List<Token> tokens,
            List<AnnotatedToken<JSpellInfo>> jSpellAnnotatedTokens
    ) throws AnalysisException {
        this.tokens = tokens;
        this.jSpellAnnotatedTokens = jSpellAnnotatedTokens;
        return this;
    }*/
}
