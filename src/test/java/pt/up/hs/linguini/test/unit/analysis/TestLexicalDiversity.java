package pt.up.hs.linguini.test.unit.analysis;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.platform.runner.JUnitPlatform;
import org.junit.runner.RunWith;
import pt.up.hs.linguini.TextAnalyzer;
import pt.up.hs.linguini.analysis.lexicaldiversity.LDAlgorithm;
import pt.up.hs.linguini.exceptions.LinguiniException;

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

        Double result;
        try {
            result = TextAnalyzer.analyzeLexicalDiversity(
                    new Locale("pt", "PT"),
                    PARAGRAPH,
                    LDAlgorithm.MTLD,
                    false,
                    null, null
            );
        } catch (LinguiniException e) {
            Assertions.fail("Failed to analyze text", e);
            return;
        }

        Assertions.assertEquals("43.482",
                String.format(Locale.US, "%.3f", result));
    }

    @Test
    public final void testMtldLemmatized() {

        Double result;
        try {
            result = TextAnalyzer.analyzeLexicalDiversity(
                    new Locale("pt", "PT"),
                    PARAGRAPH,
                    LDAlgorithm.MTLD,
                    true,
                    null, null
            );
        } catch (LinguiniException e) {
            Assertions.fail("Failed to analyze text", e);
            return;
        }

        Assertions.assertEquals("117.623",
                String.format(Locale.US, "%.3f", result));
    }

    @Test
    public final void testHdd() {

        Double result;
        try {
            result = TextAnalyzer.analyzeLexicalDiversity(
                    new Locale("pt", "PT"),
                    PARAGRAPH,
                    LDAlgorithm.HDD,
                    false,
                    null, null
            );
        } catch (LinguiniException e) {
            Assertions.fail("Failed to analyze text", e);
            return;
        }

        Assertions.assertEquals("0.757",
                String.format(Locale.US, "%.3f", result));
    }

    @Test
    public final void testHddLemmatized() {

        Double result;
        try {
            result = TextAnalyzer.analyzeLexicalDiversity(
                    new Locale("pt", "PT"),
                    PARAGRAPH,
                    LDAlgorithm.HDD,
                    true,
                    null, null
            );
        } catch (LinguiniException e) {
            Assertions.fail("Failed to analyze text", e);
            return;
        }

        Assertions.assertEquals("0.880",
                String.format(Locale.US, "%.3f", result));
    }
}
