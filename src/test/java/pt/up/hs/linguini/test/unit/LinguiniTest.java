package pt.up.hs.linguini.test.unit;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import pt.up.hs.linguini.Linguini;
import pt.up.hs.linguini.TextAnalyzer;
import pt.up.hs.linguini.exceptions.LinguiniException;
import pt.up.hs.linguini.models.LinguisticsReport;
import pt.up.hs.linguini.utils.UnorderedPair;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Locale;
import java.util.stream.Collectors;

import static pt.up.hs.linguini.test.unit.TestConstants.*;

/**
 * Tests on main class.
 *
 * @author José Carlos Paiva <code>josepaiva94@gmail.com</code>
 */
public class LinguiniTest {

    private static final Locale LOCALE = new Locale("pt", "PT");

    @Test
    public final void testLinguini() {
        Linguini linguini =  new Linguini(LOCALE);
        try {
            LinguisticsReport report = linguini.analyze(TEXT_1);
            Assertions.assertEquals(2132, report.getCharacterCount());
            Assertions.assertEquals(1727, report.getNonBlankCharacterCount());
            Assertions.assertEquals(458, report.getWordCount());
            Assertions.assertEquals(17, report.getSentenceCount());

            Assertions.assertEquals(
                    new HashSet<>(Arrays.asList(
                            new UnorderedPair<>("casa", "banho"),
                            new UnorderedPair<>("chegar", "casa"),
                            new UnorderedPair<>("estar", "tentar"),
                            new UnorderedPair<>("ter", "aula"),
                            new UnorderedPair<>("vestir", "levantar"),
                            new UnorderedPair<>("cara", "pôr"),
                            new UnorderedPair<>("vestir", "tentar"),
                            new UnorderedPair<>("ter", "ir"),
                            new UnorderedPair<>("seguida", "metro"),
                            new UnorderedPair<>("cara", "creme"),
                            new UnorderedPair<>("comboio", "08:12"),
                            new UnorderedPair<>("saber", "vestir"),
                            new UnorderedPair<>("ir", "casa"),
                            new UnorderedPair<>("comboio", "esperar")
                    )),
                    report.getCoOccurrences().parallelStream()
                            .map(coOccurrence -> new UnorderedPair<>(
                                    coOccurrence.getFirstWord(),
                                    coOccurrence.getSecondWord()))
                            .collect(Collectors.toSet())
            );

            Assertions.assertEquals("0.493",
                    String.format(Locale.US, "%.3f", report.getLexicalDensity()));

            Assertions.assertEquals("0.321",
                    String.format(Locale.US, "%.3f", report.getIdeaDensity()));
        } catch (LinguiniException e) {
            Assertions.fail(e);
        }
    }

    @Test
    public final void testLinguiniNotBroken() {

        Linguini linguini =  new Linguini(LOCALE);

        LinguisticsReport report;
        try {
            report = linguini.analyze(TEXT_1);
            Assertions.assertEquals("0.321",
                    String.format(Locale.US, "%.3f", report.getIdeaDensity()));

            report = linguini.analyze(TEXT_2);
            Assertions.assertEquals("0.483",
                    String.format(Locale.US, "%.3f", report.getIdeaDensity()));

            report = linguini.analyze(TEXT_3);
            Assertions.assertEquals("0.626",
                    String.format(Locale.US, "%.3f", report.getIdeaDensity()));

            report = linguini.analyze(TEXT_4);
            Assertions.assertEquals("0.515",
                    String.format(Locale.US, "%.3f", report.getIdeaDensity()));

            report = linguini.analyze(TEXT_5);
            Assertions.assertEquals("0.413",
                    String.format(Locale.US, "%.3f", report.getIdeaDensity()));

            report = linguini.analyze(TEXT_6);
            Assertions.assertEquals("0.837",
                    String.format(Locale.US, "%.3f", report.getIdeaDensity()));

            report = linguini.analyze(TEXT_7);
            Assertions.assertEquals("0.616",
                    String.format(Locale.US, "%.3f", report.getIdeaDensity()));

            report = linguini.analyze(TEXT_8);
            Assertions.assertEquals("0.396",
                    String.format(Locale.US, "%.3f", report.getIdeaDensity()));

            report = linguini.analyze(TEXT_9);
            Assertions.assertEquals("0.217",
                    String.format(Locale.US, "%.3f", report.getIdeaDensity()));

            report = linguini.analyze(TEXT_10);
            Assertions.assertEquals("0.386",
                    String.format(Locale.US, "%.3f", report.getIdeaDensity()));

            report = linguini.analyze(TEXT_11);
            Assertions.assertEquals("0.544",
                    String.format(Locale.US, "%.3f", report.getIdeaDensity()));
        } catch (LinguiniException e) {
            e.printStackTrace();
            Assertions.fail("Error thrown during test", e);
        }
    }

}
