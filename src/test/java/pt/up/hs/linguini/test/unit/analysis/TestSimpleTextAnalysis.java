package pt.up.hs.linguini.test.unit.analysis;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.platform.runner.JUnitPlatform;
import org.junit.runner.RunWith;
import pt.up.hs.linguini.analysis.SimpleTextAnalysis;
import pt.up.hs.linguini.exceptions.AnalyzerException;
import pt.up.hs.linguini.models.Category;
import pt.up.hs.linguini.models.TextSummary;
import pt.up.hs.linguini.models.Token;
import pt.up.hs.linguini.processing.TextTokenizer;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
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

        List<Token> tokens = new TextTokenizer(SENTENCE)
                .collectAll(true, true);

        SimpleTextAnalysis analysis = new SimpleTextAnalysis(
                new Locale("pt", "PT"));

        TextSummary textSummary;
        try {
            textSummary = analysis
                    .preprocess(tokens)
                    .execute()
                    .getResult();
        } catch (AnalyzerException e) {
            Assertions.fail("Failed to analyze text", e);
            return;
        }

        Assertions.assertEquals(1, textSummary.getNrOfSentences());
        Assertions.assertEquals(0, textSummary.getNrOfErrors());
        Assertions.assertEquals(18, textSummary.getNrOfLemmas());
        Assertions.assertEquals(9, textSummary.getNrOfNonStopWords());
        Assertions.assertEquals("4.77",
                String.format(Locale.US, "%.2f", textSummary.getAvgWordLength()));

        Assertions.assertEquals(2, textSummary.getWordFrequency().get("partiu"));
        Assertions.assertEquals(1, textSummary.getWordFrequency().get("conhecimento"));
        Assertions.assertEquals(2, textSummary.getWordFrequency().get("o"));

        Assertions.assertIterableEquals(
                Arrays.asList("rica"),
                textSummary.getWordsByCategory().get(Category.ADJECTIVE)
        );
        Assertions.assertEquals(
                new HashSet<>(Arrays.asList("tomar", "partiu", "distrair", "correr", "vai")),
                textSummary.getWordsByCategory().get(Category.VERB)
        );
        Assertions.assertEquals(
                new HashSet<>(Arrays.asList("Maria", "Eduarda", "Carlos")),
                textSummary.getWordsByCategory().get(Category.PROPER_NAME)
        );
    }

    @Test
    public final void testMultipleSentences() {

        List<Token> tokens = new TextTokenizer(PARAGRAPH)
                .collectAll(true, true);

        SimpleTextAnalysis analysis = new SimpleTextAnalysis(
                new Locale("pt", "PT"));

        TextSummary textSummary;
        try {
            textSummary = analysis
                    .preprocess(tokens)
                    .execute()
                    .getResult();
        } catch (AnalyzerException e) {
            Assertions.fail("Failed to analyze text", e);
            return;
        }

        Assertions.assertEquals(7, textSummary.getNrOfSentences());
    }
}
