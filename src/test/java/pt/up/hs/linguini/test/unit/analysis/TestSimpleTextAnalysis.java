package pt.up.hs.linguini.test.unit.analysis;

import org.junit.Assert;
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

    private static final String TEXT_WITH_ERRORS = "Ao acordar tenho sempre" +
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
        Assertions.assertEquals(23, textSummary.getNrOfWords());
        Assertions.assertEquals(13, textSummary.getNrOfContentWords());
        Assertions.assertEquals(10, textSummary.getNrOfFunctionalWords());

        Assertions.assertEquals(1, textSummary.getNrOfSentences());
        Assertions.assertEquals(0, textSummary.getNrOfErrors());
        /*Assertions.assertEquals(9, textSummary.getNrOfStopWords());*/
        Assertions.assertEquals(18, textSummary.getNrOfDistinctLemmas());
        Assertions.assertEquals("4.57",
                String.format(Locale.US, "%.2f", textSummary.getAvgWordLength()));
        Assertions.assertEquals("6.62",
                String.format(Locale.US, "%.2f", textSummary.getAvgContentWordLength()));
        Assertions.assertEquals("1.90",
                String.format(Locale.US, "%.2f", textSummary.getAvgFunctionalWordLength()));

        Assertions.assertEquals(2, textSummary.getWordFrequency().get("partiu"));
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

    @Test
    public final void testTextWithErrors() {

        TextSummary textSummary;
        try {
            textSummary = TextAnalyzer.summarize(
                    new Locale("pt", "PT"), TEXT_WITH_ERRORS);
        } catch (Exception e) {
            Assertions.fail("Error thrown during test", e);
            return;
        }
        Assert.assertFalse(textSummary.getWordsByCategory().containsKey(null));
    }
}
