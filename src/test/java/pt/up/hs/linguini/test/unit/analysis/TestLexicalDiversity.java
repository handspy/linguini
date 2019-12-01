package pt.up.hs.linguini.test.unit.analysis;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.platform.runner.JUnitPlatform;
import org.junit.runner.RunWith;
import pt.up.hs.linguini.analysis.LexicalDiversityAnalysis;
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
 * Unit tests for lexical diversity.
 *
 * @author José Carlos Paiva <code>josepaiva94@gmail.com</code>
 */
@RunWith(JUnitPlatform.class)
public class TestLexicalDiversity {
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

    public TestLexicalDiversity() {
        super();
    }

    @Test
    public final void testMtld() {

        List<Token> tokens = new TextTokenizer(PARAGRAPH)
                .collectAll(true, true);

        LexicalDiversityAnalysis analysis = new LexicalDiversityAnalysis(
                new Locale("pt", "PT"),
                LexicalDiversityAnalysis.Algorithm.MTLD,
                true);

        Double result;
        try {
            result = analysis
                    .preprocess(tokens)
                    .execute()
                    .getResult();
        } catch (AnalyzerException e) {
            Assertions.fail("Failed to analyze text", e);
            return;
        }

        Assertions.assertEquals("111.655",
                String.format(Locale.US, "%.3f", result));
    }

    @Test
    public final void testHdd() {

        List<Token> tokens = new TextTokenizer(PARAGRAPH)
                .collectAll(true, true);

        LexicalDiversityAnalysis analysis = new LexicalDiversityAnalysis(
                new Locale("pt", "PT"),
                LexicalDiversityAnalysis.Algorithm.HDD,
                true);

        Double result;
        try {
            result = analysis
                    .preprocess(tokens)
                    .execute()
                    .getResult();
        } catch (AnalyzerException e) {
            Assertions.fail("Failed to analyze text", e);
            return;
        }

        Assertions.assertEquals("0.875",
                String.format(Locale.US, "%.3f", result));
    }
}
