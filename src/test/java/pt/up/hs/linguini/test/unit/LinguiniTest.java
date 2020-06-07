package pt.up.hs.linguini.test.unit;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import pt.up.hs.linguini.Linguini;
import pt.up.hs.linguini.exceptions.LinguiniException;
import pt.up.hs.linguini.models.LinguisticsReport;
import pt.up.hs.linguini.utils.UnorderedPair;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Locale;
import java.util.stream.Collectors;

/**
 * Tests on main class.
 *
 * @author José Carlos Paiva <code>josepaiva94@gmail.com</code>
 */
public class LinguiniTest {

    private static final String TEXT = "Ao acordar tenho sempre" +
            " muito sono pelo que XXXX sempre o primeiro alarme do telemóvel" +
            " programando-o para daqui a 5 m ou 6. Ao ouvir o segundo alarme" +
            " abro a preciana e caso já saiba o que vestir, tento levantar-me" +
            " logo e vestir-me; caso não saiba, irei ficar na cama com as por" +
            "tas do armário abertas a olhar para a roupa a pensar na previsão" +
            " do tempo e se quero ir mais arranjada ou se não estou muito bem" +
            "-disposta e vou mais básica. Como me atraso sempre nesta parte a" +
            "penas corro para a casa de banho e lavo a cara, ponho o creme na" +
            " cara já no quarto, desodorizante e perfume. Desço a correr para" +
            " a cozinha, pego na garrafa de 1,5 l de água, no pão sem glúten " +
            "e no leite achocolatado, por vezes se tenho tempo levo 1 peça de" +
            " fruta, a minha mãe já a chegar ao portão espera uns segundos po" +
            "r mim; entro no carro e vamos à pressa para o apeadeiro. Costumo" +
            " apanhar o comboio das 08:12, espero pelo comboio entro nele e v" +
            "enho esmagada e neste momento já cheia de calor até General Torr" +
            "es. Apanho de seguida o metro, pelo menos aquele que dá para se " +
            "entrar nele que também acaba por ficar cheio e aí já estou a ten" +
            "tar controlar a minha ansiedade e vá como costumo chamar “claust" +
            "rofobia” porque começo a sentir-me mais nervosa se estiver muito" +
            " apertada e num espaço pequeno com pouco ar. Tento ser rápida a " +
            "entrar no auditório, após sair do metro. Contudo por vezes tenho" +
            " de ir à casa de banho. A comida ou como no auditório ou à esper" +
            "a do comboio. Tenho as aulas respetivas ao dia da semana em que " +
            "me encontro nas quais tento estar concentrada e tirar apontament" +
            "os, contudo por vezes acabo a conversar com a minha melhor amiga" +
            " que também está cá. Se não tiver aulas de tarde vou para casa, " +
            "apanho o metro e de seguida o autocarro. Demoro quase 1 h a cheg" +
            "ar a casa devido à lentidão do espírito santo. Chego a casa e fa" +
            "ço um almoço improvisado e saudável, para mim e para o meu avô e" +
            " por vezes pai. De seguida tenho que limpar as coisas da cozinha" +
            ". Costumo sentar-me/deitar-me a descansar um pouco. Por vezes dá" +
            "-me vontade de cozinhar um doce a acabo fazendo 2 tarte ou bolo " +
            "ou biscoitos, o que me vier à mente. Volto a limpar o que sujei";


    @Test
    public final void testLinguini() {
        Linguini linguini =  new Linguini(
                new Locale("pt", "PT")
        );
        try {
            LinguisticsReport report = linguini.analyze(TEXT);
            Assertions.assertEquals(2132, report.getCharacterCount());
            Assertions.assertEquals(1727, report.getNonBlankCharacterCount());
            Assertions.assertEquals(458, report.getWordCount());
            Assertions.assertEquals(17, report.getSentenceCount());

            Assertions.assertEquals(
                    new HashSet<>(Arrays.asList(
                            new UnorderedPair<>("ter", "aula"),
                            new UnorderedPair<>("ir", "casa"),
                            new UnorderedPair<>("casa", "banho")
                    )),
                    report.getCoOccurrences().parallelStream()
                            .map(coOccurrence -> new UnorderedPair<>(
                                    coOccurrence.getFirstWord(),
                                    coOccurrence.getSecondWord()))
                            .collect(Collectors.toSet())
            );

            Assertions.assertEquals("0.337",
                    String.format(Locale.US, "%.3f", report.getIdeaDensity()));
        } catch (LinguiniException e) {
            Assertions.fail(e);
        }
    }
}
