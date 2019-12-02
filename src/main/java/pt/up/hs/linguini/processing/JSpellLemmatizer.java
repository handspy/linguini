package pt.up.hs.linguini.processing;

import pt.up.hs.linguini.jspell.JSpellWordAnnotator;
import pt.up.hs.linguini.models.AnnotatedToken;
import pt.up.hs.linguini.models.Token;
import pt.up.hs.linguini.processing.exceptions.ProcessorException;
import pt.up.hs.linguini.transformers.LemmaTokenTransformer;

import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

/**
 * Lemmatizer
 *
 * @author José Carlos Paiva <code>josepaiva94@gmail.com</code>
 */
public class JSpellLemmatizer {

    private Locale locale;

    private List<Token> tokens;
    private List<AnnotatedToken<Token>> jSpellAnnotatedTokens;

    public JSpellLemmatizer(Locale locale) {
        this.locale = locale;
    }

    public JSpellLemmatizer(List<AnnotatedToken<Token>> jSpellAnnotatedTokens) {
        this.jSpellAnnotatedTokens = jSpellAnnotatedTokens;
    }

    public void process(List<Token> tokens) throws ProcessorException {

        if (jSpellAnnotatedTokens == null) {
            processWithTransformer(tokens);
        } else {
        }
    }

    private void processWithTransformer(List<Token> tokens) throws ProcessorException {
        JSpellWordAnnotator wordAnnotator;
        try {
            wordAnnotator = new JSpellWordAnnotator(locale);
        } catch (IOException e) {
            throw new ProcessorException("Could not create JSpell word annotator.", e);
        }

        LemmaTokenTransformer transformer = new LemmaTokenTransformer(wordAnnotator);

        this.tokens = tokens.parallelStream()
                .map(transformer::transform)
                .collect(Collectors.toList());
    }

    private void processFromAnnotatedInfo() throws ProcessorException {


    }
}
