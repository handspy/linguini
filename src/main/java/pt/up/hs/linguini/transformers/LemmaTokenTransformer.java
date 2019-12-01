package pt.up.hs.linguini.transformers;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import pt.up.hs.linguini.jspell.JSpellInfo;
import pt.up.hs.linguini.models.AnnotatedToken;
import pt.up.hs.linguini.models.Token;
import pt.up.hs.linguini.jspell.JSpellLex;
import pt.up.hs.linguini.jspell.JSpellWordAnnotator;
import pt.up.hs.linguini.transformers.selection.ChooseFirstStrategy;
import pt.up.hs.linguini.transformers.selection.SelectionStrategy;
import pt.up.hs.linguini.transformers.selection.exceptions.SelectionException;

import java.io.IOException;

/**
 * Token transformer that converts a token to its lemma.
 *
 * @author José Carlos Paiva <code>josepaiva94@gmail.com</code>
 */
public class LemmaTokenTransformer implements TokenTransformer {
    private static final Logger LOGGER = LogManager.getLogger(LemmaTokenTransformer.class.getName());

    private JSpellWordAnnotator wordAnnotator;
    private SelectionStrategy<JSpellLex> strategy;

    public LemmaTokenTransformer(JSpellWordAnnotator wordAnnotator) {
        this(wordAnnotator, new ChooseFirstStrategy<>());
    }

    public LemmaTokenTransformer(
            JSpellWordAnnotator wordAnnotator,
            SelectionStrategy<JSpellLex> strategy) {
        this.wordAnnotator = wordAnnotator;
        this.strategy = strategy;
    }

    @Override
    public Token transform(Token token) {
        try {
            AnnotatedToken<JSpellInfo> annotatedToken = wordAnnotator.annotate(token);
            JSpellInfo info = annotatedToken.getInfo();
            if (info != null && info.getRelated() != null && !info.getRelated().isEmpty()) {
                JSpellLex selectedLex = strategy.select(info.getRelated());
                token.setWord(selectedLex.getLemma());
            }
        } catch (IOException | SelectionException e) {
            LOGGER.warn("Could not lemmatize word '" + token.getWord() + "'.");
        }
        return token;
    }
}
