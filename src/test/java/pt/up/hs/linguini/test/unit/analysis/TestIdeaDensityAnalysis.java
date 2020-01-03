package pt.up.hs.linguini.test.unit.analysis;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.platform.runner.JUnitPlatform;
import org.junit.runner.RunWith;
import pt.up.hs.linguini.TextAnalyzer;
import pt.up.hs.linguini.exceptions.LinguiniException;

import java.util.Locale;

/**
 * Unit tests for Idea Density analysis based on IDD3 (Propositional Idea
 * Density from Dependency Trees).
 *
 * @author José Carlos Paiva <code>josepaiva94@gmail.com</code>
 */
@RunWith(JUnitPlatform.class)
public class TestIdeaDensityAnalysis {
    private static final String SENTENCE_1 = "Era uma vez um gato, que dormia todo o dia.";
    private static final String SENTENCE_2 = "O José vai desistir de fazer desporto.";

    private static final Locale LOCALE = new Locale("pt", "PT");

    @Test
    public final void testExecuteSentence1() throws LinguiniException {

        double idd;
        try {
            idd = TextAnalyzer.analyzeIdeaDensity(LOCALE, SENTENCE_1);
        } catch (LinguiniException e) {
            Assertions.fail("Error thrown during test", e);
            return;
        }

        System.out.println(idd);

        Assertions.assertEquals("0.300",
                String.format(Locale.US, "%.3f", idd));
    }

    @Test
    public final void testExecuteSentence2() throws LinguiniException {

        double idd;
        try {
            idd = TextAnalyzer.analyzeIdeaDensity(LOCALE, SENTENCE_2);
        } catch (LinguiniException e) {
            Assertions.fail("Error thrown during test", e);
            return;
        }

        System.out.println(idd);

        Assertions.assertEquals("0.286",
                String.format(Locale.US, "%.3f", idd));
    }

}
