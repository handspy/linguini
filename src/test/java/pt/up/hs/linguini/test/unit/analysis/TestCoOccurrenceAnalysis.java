package pt.up.hs.linguini.test.unit.analysis;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import pt.up.hs.linguini.TextAnalyzer;
import pt.up.hs.linguini.analysis.cooccurrence.CoOccurrence;
import pt.up.hs.linguini.exceptions.LinguiniException;
import pt.up.hs.linguini.utils.UnorderedPair;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

/**
 * Unit tests for co-occurrence analysis.
 *
 * @author José Carlos Paiva <code>josepaiva94@gmail.com</code>
 */
public class TestCoOccurrenceAnalysis {
    private static final String PARAGRAPHS =
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

    @Test
    public void testSentenceEmotions() {

        List<CoOccurrence> coOccurrences;
        try {
            coOccurrences = TextAnalyzer.analyzeCoOccurrence(
                    new Locale("pt", "PT"),
                    PARAGRAPHS,
                    5,
                    2.0
            );
        } catch (LinguiniException e) {
            Assertions.fail("Error thrown during test", e);
            return;
        }

        Assertions.assertEquals(
                new HashSet<>(Arrays.asList(
                        new UnorderedPair<>("sede", "agência"),
                        new UnorderedPair<>("dizer", "lusa"),
                        new UnorderedPair<>("reter", "lisboa"),
                        new UnorderedPair<>("agência", "tap"),
                        new UnorderedPair<>("mala", "reter"),
                        new UnorderedPair<>("mala", "ficar"),
                        new UnorderedPair<>("olívio", "barreto"),
                        new UnorderedPair<>("porta-voz", "passageiro"))),
                coOccurrences.parallelStream()
                        .map(cooccurrence -> new UnorderedPair<>(cooccurrence.getFirstWord(), cooccurrence.getSecondWord()))
                        .collect(Collectors.toSet()));
    }
}
