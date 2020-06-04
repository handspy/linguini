package pt.up.hs.linguini.analysis.emotional;

import pt.up.hs.linguini.analysis.Analysis;
import pt.up.hs.linguini.dictionaries.exceptions.DictionaryException;
import pt.up.hs.linguini.emotaix.Emotaix;
import pt.up.hs.linguini.jspell.JSpellInfo;
import pt.up.hs.linguini.jspell.JSpellLex;
import pt.up.hs.linguini.models.AnnotatedToken;
import pt.up.hs.linguini.models.Emotion;
import pt.up.hs.linguini.models.Token;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * JSpell emotional analysis.
 *
 * @author José Carlos Paiva <code>josepaiva94@gmail.com</code>
 */
public class JSpellEmotionalAnalysis
        implements Analysis<List<Token>, List<AnnotatedToken<Emotion>>> {

    private Locale locale;

    public JSpellEmotionalAnalysis() {
        this(Locale.getDefault());
    }

    public JSpellEmotionalAnalysis(Locale locale) {
        this.locale = locale;
    }

    @Override
    public List<AnnotatedToken<Emotion>> execute(
            List<Token> tokens) throws DictionaryException {

        Emotaix emotaix = new Emotaix(locale);

        List<AnnotatedToken<Emotion>> annotatedTokens = new ArrayList<>();
        for (Token token: tokens) {
            List<Emotion> emotions = emotaix.retrieveEmotions(token.word());

            if (emotions != null) {
                Emotion emotion = emotions.get(0);
                if (emotion != null) {
                    annotatedTokens.add(new AnnotatedToken<>(token, emotion));
                }
            }
        }

        return annotatedTokens;
    }
}
