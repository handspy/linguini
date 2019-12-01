package pt.up.hs.linguini.analysis;

import pt.up.hs.linguini.exceptions.AnalyzerException;
import pt.up.hs.linguini.jspell.JSpellInfo;
import pt.up.hs.linguini.jspell.JSpellLex;
import pt.up.hs.linguini.models.AnnotatedToken;
import pt.up.hs.linguini.models.Emotion;
import pt.up.hs.linguini.models.Token;
import pt.up.hs.linguini.transformers.selection.ChooseFirstStrategy;
import pt.up.hs.linguini.transformers.selection.SelectionStrategy;
import pt.up.hs.linguini.transformers.selection.exceptions.SelectionException;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Analysis to annotate emotions in words.
 *
 * @author José Carlos Paiva <code>josepaiva94@gmail.com</code>
 */
public class EmotionalAnalysis extends JSpellPreprocessingAnalysis<List<AnnotatedToken<Emotion>>> {
    
    private SelectionStrategy<JSpellLex> lemmaStrategy;

    private List<AnnotatedToken<Emotion>> emotions;

    public EmotionalAnalysis(Locale locale) {
        this(locale, new ChooseFirstStrategy<>());
    }

    public EmotionalAnalysis(Locale locale, SelectionStrategy<JSpellLex> lemmaStrategy) {
        super(locale);
        this.lemmaStrategy = lemmaStrategy;
    }

    @Override
    public EmotionalAnalysis execute() throws AnalyzerException {

        emotions = new ArrayList<>();

        for (AnnotatedToken<JSpellInfo> jSpellAnnotatedToken: jSpellAnnotatedTokens) {
            Token token = jSpellAnnotatedToken.getToken();
            JSpellInfo info = jSpellAnnotatedToken.getInfo();
            try {
                if (info != null && info.getRelated() != null &&
                        !info.getRelated().isEmpty()) {
                    JSpellLex selectedLex = lemmaStrategy.select(info.getRelated());
                    Emotion emotion = selectedLex.getEmotion();
                    if (emotion != null) {
                        emotions.add(new AnnotatedToken<>(token, emotion));
                    }
                }
            } catch (SelectionException e) {
                throw new AnalyzerException("Failed to select lemma.", e);
            }
        }

        return this;
    }

    @Override
    public List<AnnotatedToken<Emotion>> getResult() {
        return emotions;
    }
}
