package pt.up.hs.linguini.test.unit.analysis;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.platform.runner.JUnitPlatform;
import org.junit.runner.RunWith;
import pt.up.hs.linguini.TextAnalyzer;
import pt.up.hs.linguini.exceptions.LinguiniException;
import pt.up.hs.linguini.models.TextSummary;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Locale;

/**
 * Unit tests for simple text analysis.
 *
 * @author José Carlos Paiva <code>josepaiva94@gmail.com</code>
 */
@RunWith(JUnitPlatform.class)
public class TestSimpleTextAnalysis {
    private static final String SENTENCE = "Ao tomar conhecimento, Maria " +
            "Eduarda, agora rica, partiu para o estrangeiro; e Carlos, para " +
            "se distrair, vai correr o mundo e partiu.";

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

    public TestSimpleTextAnalysis() {
        super();
    }

    @Test
    public final void testSentence() {

        TextSummary textSummary;
        try {
            textSummary = TextAnalyzer.summarize(
                    new Locale("pt", "PT"), SENTENCE);
        } catch (LinguiniException e) {
            Assertions.fail("Error thrown during test", e);
            return;
        }

        Assertions.assertEquals(133, textSummary.getNrOfCharacters());
        Assertions.assertEquals(112, textSummary.getNrOfNonBlankCharacters());
        Assertions.assertEquals(22, textSummary.getNrOfWords());
        Assertions.assertEquals(9, textSummary.getNrOfContentWords());
        Assertions.assertEquals(4, textSummary.getNrOfFunctionalWords());

        System.out.println(String.join(", ", textSummary.getContentWords()));
        System.out.println(String.join(", ", textSummary.getFunctionalWords()));

        Assertions.assertEquals(1, textSummary.getNrOfSentences());
        Assertions.assertEquals(0, textSummary.getNrOfErrors());
        Assertions.assertEquals(9, textSummary.getNrOfStopWords());
        Assertions.assertEquals(13, textSummary.getNrOfDistinctLemmas());
        Assertions.assertEquals("6.38",
                String.format(Locale.US, "%.2f", textSummary.getAvgWordLength()));

        Assertions.assertEquals(2, textSummary.getWordFrequency().get("partir"));
        Assertions.assertEquals(1, textSummary.getWordFrequency().get("conhecimento"));

        Assertions.assertIterableEquals(
                Arrays.asList("rico"),
                textSummary.getWordsByCategory().get("ADJECTIVE")
        );
        Assertions.assertEquals(
                new HashSet<>(Arrays.asList("tomar", "partir", "distrair", "correr")),
                textSummary.getWordsByCategory().get("VERB")
        );
        Assertions.assertEquals(
                new HashSet<>(Arrays.asList("ir")),
                textSummary.getWordsByCategory().get("AUXILIAR_VERB")
        );
        Assertions.assertEquals(
                new HashSet<>(Arrays.asList("maria", "eduarda", "carlos")),
                textSummary.getWordsByCategory().get("PROPER_NOUN")
        );
    }

    @Test
    public final void testMultipleSentences() {

        TextSummary textSummary;
        try {
            textSummary = TextAnalyzer.summarize(
                    new Locale("pt", "PT"), PARAGRAPH);
        } catch (LinguiniException e) {
            Assertions.fail("Error thrown during test", e);
            return;
        }

        Assertions.assertEquals(7, textSummary.getNrOfSentences());
    }
}
