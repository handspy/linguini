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

    private static final String TEXT = "Todos os dias quando acordo, por volt" +
            "a das 06:30 da manhã, costumo dirigir-me à casa de banho e aí ex" +
            "ecuto todas as minhas rotinas de higiene pessoal. De seguida, di" +
            "rijo-me à cozinha para tomar o pequeno-almoço e interagir com os" +
            " meus pais e irmã. Posteriormente, volto para o meu quarto e vis" +
            "to-me e arranjo-me para sair de casa.\n\n" +
            "Após o anteriormente referido, dirijo-me à estação ferroviária d" +
            "a minha localidade, à boleia de um dos meus pais, por volta das " +
            "07:30 para apanhar o comboio das 07:41 que me levará até ao Port" +
            "o onde terei as minhas aulas na faculdade onde estou inscrita. A" +
            "pós chegar ao Porto, mais propriamente Gaia (General Torres) apa" +
            "nho o metro que me traz até à paragem do Polo Universitário. É n" +
            "esta paragem que saio e após subir umas escadas encontro a minha" +
            " faculdade. Com isto, dirijo-me à faculdade e a primeira coisa q" +
            "ue faço é descer até ao piso 0 para tomar um café. De seguida, d" +
            "irijo-me às salas/auditórios onde terei aulas naquele dia e enco" +
            "ntro-me nesta altura com os meus colegas de curso.\n" +
            "\n" +
            "Na hora de almoço e se não tiver aulas de tarde, vou até à canti" +
            "na ou ao buffet para almoçar e depois volto para as aulas.\n" +
            "\n" +
            "Normalmente costumo aproveitar o meu tempo livre na faculdade pa" +
            "ra ir até à biblioteca estudar e dependendo dos dias e da minha " +
            "disponibilidade, costumo ficar até ao fecho da mesma.\n" +
            "\n" +
            "Por fim, dirijo-me normalmente ao metro que me levará de volta a" +
            "té General Torres, onde apanharei o comboio para regressar à cid" +
            "ade onde moro. Lá estará o meu pai para me receber e levar até c" +
            "asa. De seguida, tomo banho e vou jantar com a minha família. De" +
            "pendendo das horas, ainda tento acabar algum trabalho e desenvol" +
            "ver alguma tarefa para a faculdade. Por último, trato normalment" +
            "e da mina higiene pessoal para me preparar para um novo dia. ";

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
                        Emotion.Polarity.NEGATIVE,
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
                new Emotion(
                        Emotion.Polarity.POSITIVE,
                        Emotion.Global.BENEVOLENCE,
                        Emotion.Intermediate.AFFECTION,
                        Emotion.Specific.LOVE
                ),
                emotions.get(0).getInfo()
        );
        Assertions.assertEquals(
                166,
                emotions.get(0).getToken().getStart()
        );
        Assertions.assertEquals(
                new Emotion(
                        Emotion.Polarity.NEGATIVE,
                        Emotion.Global.DISCOMFORT,
                        Emotion.Intermediate.DEPRESSION,
                        Emotion.Specific.SADNESS
                ),
                emotions.get(1).getInfo()
        );
        Assertions.assertEquals(
                386,
                emotions.get(1).getToken().getStart()
        );
        Assertions.assertEquals(
                new Emotion(
                        Emotion.Polarity.POSITIVE,
                        Emotion.Global.BENEVOLENCE,
                        Emotion.Intermediate.AFFECTION,
                        Emotion.Specific.LOVE
                ),
                emotions.get(4).getInfo()
        );
        Assertions.assertEquals(
                793,
                emotions.get(4).getToken().getStart()
        );
        Assertions.assertEquals(5, emotions.size());
    }

    @Test
    public void testTextEmotions() {

        List<AnnotatedToken<Emotion>> emotions;
        try {
            emotions = TextAnalyzer.analyzeEmotions(
                    new Locale("pt", "PT"),
                    TEXT,
                    true
            );
        } catch (LinguiniException e) {
            Assertions.fail("Error thrown during test", e);
            return;
        }

        for (AnnotatedToken<Emotion> emotion: emotions) {
            System.out.println(emotion.getInfo());
        }
    }
}
