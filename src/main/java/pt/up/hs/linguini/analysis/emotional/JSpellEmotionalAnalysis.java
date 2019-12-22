package pt.up.hs.linguini.analysis.emotional;

import pt.up.hs.linguini.analysis.Analysis;
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
        implements Analysis<List<AnnotatedToken<JSpellInfo>>, List<AnnotatedToken<Emotion>>> {

    private Locale locale;

    public JSpellEmotionalAnalysis() {
        this(Locale.getDefault());
    }

    public JSpellEmotionalAnalysis(Locale locale) {
        this.locale = locale;
    }

    @Override
    public List<AnnotatedToken<Emotion>> execute(
            List<AnnotatedToken<JSpellInfo>> jSpellAnnotatedTokens) {

        List<AnnotatedToken<Emotion>> emotions = new ArrayList<>();

        for (AnnotatedToken<JSpellInfo> jSpellAnnotatedToken: jSpellAnnotatedTokens) {
            Token token = jSpellAnnotatedToken.getToken();
            JSpellInfo info = jSpellAnnotatedToken.getInfo();
            if (info != null && info.getRelated() != null &&
                    !info.getRelated().isEmpty()) {
                JSpellLex selectedLex = info.getRelated().get(0);
                Emotion emotion = selectedLex.getEmotion();
                if (emotion != null) {
                    emotions.add(new AnnotatedToken<>(token, emotion));
                }
            }
        }

        return emotions;
    }
}
