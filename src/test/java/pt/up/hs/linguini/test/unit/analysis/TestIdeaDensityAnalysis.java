package pt.up.hs.linguini.test.unit.analysis;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import pt.up.hs.linguini.TextAnalyzer;
import pt.up.hs.linguini.exceptions.LinguiniException;

import java.util.Locale;

import static pt.up.hs.linguini.test.unit.TestConstants.TEXT_1;
import static pt.up.hs.linguini.test.unit.TestConstants.TEXT_2;
import static pt.up.hs.linguini.test.unit.TestConstants.TEXT_3;
import static pt.up.hs.linguini.test.unit.TestConstants.TEXT_4;
import static pt.up.hs.linguini.test.unit.TestConstants.TEXT_5;
import static pt.up.hs.linguini.test.unit.TestConstants.TEXT_6;
import static pt.up.hs.linguini.test.unit.TestConstants.TEXT_7;
import static pt.up.hs.linguini.test.unit.TestConstants.TEXT_8;
import static pt.up.hs.linguini.test.unit.TestConstants.TEXT_9;
import static pt.up.hs.linguini.test.unit.TestConstants.TEXT_10;
import static pt.up.hs.linguini.test.unit.TestConstants.TEXT_11;

/**
 * Unit tests for Idea Density analysis based on IDD3 (Propositional Idea
 * Density from Dependency Trees).
 *
 * @author José Carlos Paiva <code>josepaiva94@gmail.com</code>
 */
public class TestIdeaDensityAnalysis {
    private static final String SENTENCE_1 = "Era uma vez um gato, que dormi" +
            "a todo o dia.";
    private static final String SENTENCE_2 = "O José vai desistir de fazer d" +
            "esporto.";
    private static final String PARAGRAPH_1 =
            "Um porta-voz de passageiros que chegaram à capital da Guiné-Bis" +
            "sau nos últimos dias, providentes de Lisboa, anunciou que vão b" +
            "loquear, com corrente e cadeado, as portas da agência da TAP em" +
            " Bissau em protesto pelo \"péssimo serviço\" da companhia. \n" +
            "Olívio Barreto indicou que a \"paciência já está quase no lim" +
            "ite\" por parte de cerca de 200 passageiros da TAP que chegaram" +
            " à Bissau nas últimas semanas, cujas malas ficaram retidas em L" +
            "isboa.\nCerca de duas dezenas de passageiros manifestaram hoje " +
            "a sua indignação na sede agência da companhia portuguesa na cap" +
            "ital guineense e, de acordo com Olívio Barreto, receberam a pro" +
            "messa dos funcionários em como a situação estava a ser resolvid" +
            "a.\nO serviço da TAP é péssimo. O Governo da Guiné-Bissau devia" +
            " ter chamado já o delegado da TAP para lhe pedir satisfação\", " +
            "declarou o porta-voz dos passageiros indignados.\nUm passageiro" +
            ", que não se quis identificar, disse à Lusa que \"é ridícula, a" +
            " resposta da TAP\", que mandou uma mensagem eletrónica aos afet" +
            "ados pedindo-lhes que indiquem os valores contidos nas malas re" +
            "tidas em Lisboa.\n\"Nem sequer respondi a essa mensagem, pois p" +
            "refiro os meus pertences naquela mala a dez mil euros em dinhei" +
            "ro\", observou o passageiro.\nVários passageiros, que protestav" +
            "am esta manhã de forma ruidosa na sede da agência da TAP, ao po" +
            "nto de interromperem o serviço, disseram à Lusa que estavam sem" +
            " os medicamentos que ficaram nas malas retidas em Lisboa.";
    private static final String PARAGRAPH_2 =
            "Carlos passa a infância com o " +
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

        Assertions.assertEquals("0.286",
                String.format(Locale.US, "%.3f", idd));
    }

    @Test
    public final void testExecuteParagraph1() throws LinguiniException {

        double idd;
        try {
            idd = TextAnalyzer.analyzeIdeaDensity(LOCALE, PARAGRAPH_1);
        } catch (LinguiniException e) {
            Assertions.fail("Error thrown during test", e);
            return;
        }

        Assertions.assertEquals("0.419",
                String.format(Locale.US, "%.3f", idd));
    }

    @Test
    public final void testExecuteParagraph2() throws LinguiniException {

        double idd;
        try {
            idd = TextAnalyzer.analyzeIdeaDensity(LOCALE, PARAGRAPH_2);
        } catch (LinguiniException e) {
            Assertions.fail("Error thrown during test", e);
            return;
        }

        Assertions.assertEquals("0.279",
                String.format(Locale.US, "%.3f", idd));
    }

    @Test
    public final void testExecuteTexts() throws LinguiniException {

        double idd;
        try {
            idd = TextAnalyzer.analyzeIdeaDensity(LOCALE, TEXT_1);
            Assertions.assertEquals("0.347",
                    String.format(Locale.US, "%.3f", idd));

            idd = TextAnalyzer.analyzeIdeaDensity(LOCALE, TEXT_2);
            Assertions.assertEquals("0.565",
                    String.format(Locale.US, "%.3f", idd));

            idd = TextAnalyzer.analyzeIdeaDensity(LOCALE, TEXT_3);
            Assertions.assertEquals("0.482",
                    String.format(Locale.US, "%.3f", idd));

            idd = TextAnalyzer.analyzeIdeaDensity(LOCALE, TEXT_4);
            Assertions.assertEquals("0.580",
                    String.format(Locale.US, "%.3f", idd));

            idd = TextAnalyzer.analyzeIdeaDensity(LOCALE, TEXT_5);
            Assertions.assertEquals("0.470",
                    String.format(Locale.US, "%.3f", idd));

            idd = TextAnalyzer.analyzeIdeaDensity(LOCALE, TEXT_6);
            Assertions.assertEquals("0.837",
                    String.format(Locale.US, "%.3f", idd));

            idd = TextAnalyzer.analyzeIdeaDensity(LOCALE, TEXT_7);
            Assertions.assertEquals("0.689",
                    String.format(Locale.US, "%.3f", idd));

            idd = TextAnalyzer.analyzeIdeaDensity(LOCALE, TEXT_8);
            Assertions.assertEquals("0.446",
                    String.format(Locale.US, "%.3f", idd));

            idd = TextAnalyzer.analyzeIdeaDensity(LOCALE, TEXT_9);
            Assertions.assertEquals("0.256",
                    String.format(Locale.US, "%.3f", idd));

            idd = TextAnalyzer.analyzeIdeaDensity(LOCALE, TEXT_10);
            Assertions.assertEquals("0.450",
                    String.format(Locale.US, "%.3f", idd));

            idd = TextAnalyzer.analyzeIdeaDensity(LOCALE, TEXT_11);
            Assertions.assertEquals("0.619",
                    String.format(Locale.US, "%.3f", idd));
        } catch (LinguiniException e) {
            e.printStackTrace();
            Assertions.fail("Error thrown during test", e);
        }
    }
}
