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
    private static final String PARAGRAPH =
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

    @Test
    public final void testExecuteParagraph() throws LinguiniException {

        double idd;
        try {
            idd = TextAnalyzer.analyzeIdeaDensity(LOCALE, PARAGRAPH);
        } catch (LinguiniException e) {
            Assertions.fail("Error thrown during test", e);
            return;
        }

        System.out.println(idd);

        Assertions.assertEquals("0.316",
                String.format(Locale.US, "%.3f", idd));
    }

}
