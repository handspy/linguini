package pt.up.hs.linguini.test.unit.analysis;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.platform.runner.JUnitPlatform;
import org.junit.runner.RunWith;
import pt.up.hs.linguini.TextAnalyzer;
import pt.up.hs.linguini.exceptions.LinguiniException;
import pt.up.hs.linguini.models.AnnotatedToken;
import pt.up.hs.linguini.models.Emotion;

import java.util.List;
import java.util.Locale;

/**
 * Unit tests for emotional analysis.
 *
 * @author José Carlos Paiva <code>josepaiva94@gmail.com</code>
 */
@RunWith(JUnitPlatform.class)
public class TestEmotionalAnalysis {
    private static final String SENTENCE = "Ao tomar conhecimento, Maria " +
            "Eduarda, agora rica, partiu para o estrangeiro; e Carlos, para " +
            "se distrair, vai correr o mundo.";

    private static final String PARAGRAPH = "Carlos passa a infância com o " +
            "avô, formando-se depois, em Medicina em Coimbra. Carlos " +
            "regressa a Lisboa, ao Ramalhete, após a formatura, onde se vai" +
            " rodear de alguns amigos, como o João da Ega, Alencar, Damaso " +
            "Salcede, Palma de Cavalão, Euzébiozinho, o maestro Cruges, " +
            "entre outros. Seguindo os hábitos dos que o rodeavam, Carlos " +
            "envolve-se com a Condessa de Gouvarinho, que depois irá " +
            "abandonar. Um dia fica deslumbrado ao conhecer Maria Eduarda, " +
            "que julgava ser mulher do brasileiro Castro Gomes. Carlos " +
            "seguiu-a algum tempos sem êxito, mas acaba por conseguir uma " +
            "aproximação quando é chamado Maria Eduarda para visitar, como " +
            "médico a governanta. Começam então os seus encontros com Maria " +
            "Eduarda, visto que Castro Gomes estava ausente. Carlos chega " +
            "mesmo a comprar uma casa onde instala a amante.";

    @Test
    public void testSentenceEmotions() {

        List<AnnotatedToken<Emotion>> emotions;
        try {
            emotions = TextAnalyzer.analyzeEmotions(
                    new Locale("pt", "PT"),
                    SENTENCE,
                    true
            );
        } catch (LinguiniException e) {
            Assertions.fail("Error thrown during test", e);
            return;
        }

        Assertions.assertEquals(
                new Emotion(
                        Emotion.Global.DISCOMFORT,
                        Emotion.Intermediate.DISTURBANCE,
                        Emotion.Specific.AGITATION
                ),
                emotions.get(0).getInfo()
        );
        Assertions.assertEquals(1, emotions.size());
    }

    @Test
    public void testParagraphEmotions() {

        List<AnnotatedToken<Emotion>> emotions;
        try {
            emotions = TextAnalyzer.analyzeEmotions(
                    new Locale("pt", "PT"),
                    PARAGRAPH,
                    true
            );
        } catch (LinguiniException e) {
            Assertions.fail("Error thrown during test", e);
            return;
        }

        Assertions.assertEquals(
                new Emotion(Emotion.Global.BENEVOLENCE, Emotion.Intermediate.AFFECTION, Emotion.Specific.LOVE),
                emotions.get(0).getInfo()
        );
        Assertions.assertEquals(
                166,
                emotions.get(0).getToken().getStart()
        );
        Assertions.assertEquals(
                new Emotion(Emotion.Global.NON_SPECIFIC, null, null),
                emotions.get(1).getInfo()
        );
        Assertions.assertEquals(
                330,
                emotions.get(1).getToken().getStart()
        );
        Assertions.assertEquals(
                new Emotion(Emotion.Global.DISCOMFORT, Emotion.Intermediate.DEPRESSION, Emotion.Specific.SADNESS),
                emotions.get(2).getInfo()
        );
        Assertions.assertEquals(
                386,
                emotions.get(2).getToken().getStart()
        );
        Assertions.assertEquals(
                new Emotion(Emotion.Global.BENEVOLENCE, Emotion.Intermediate.AFFECTION, Emotion.Specific.LOVE),
                emotions.get(6).getInfo()
        );
        Assertions.assertEquals(
                793,
                emotions.get(6).getToken().getStart()
        );
        Assertions.assertEquals(7, emotions.size());
    }
}
