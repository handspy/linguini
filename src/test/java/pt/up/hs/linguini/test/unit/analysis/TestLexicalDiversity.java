package pt.up.hs.linguini.test.unit.analysis;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;
import pt.up.hs.linguini.TextAnalyzer;
import pt.up.hs.linguini.analysis.lexicaldiversity.LDAlgorithm;
import pt.up.hs.linguini.exceptions.LinguiniException;

import java.util.Locale;

/**
 * Unit tests for lexical diversity.
 *
 * @author José Carlos Paiva <code>josepaiva94@gmail.com</code>
 */
public class TestLexicalDiversity {
    private static final double EPSILON = 0.001;

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

    public TestLexicalDiversity() {
        super();
    }

    @Test
    public final void testMtldParagraph() {

        Double result;
        try {
            result = TextAnalyzer.analyzeLexicalDiversity(
                    new Locale("pt", "PT"),
                    PARAGRAPH,
                    LDAlgorithm.MTLD,
                    false
            );
        } catch (LinguiniException e) {
            Assertions.fail("Failed to analyze text", e);
            return;
        }

        // System.out.println("MTLD (paragraph): " + result);

        Assertions.assertEquals("141.148",
                String.format(Locale.US, "%.3f", result));
    }

    @Test
    public final void testMtldParagraphLemmatized() {

        Double result;
        try {
            result = TextAnalyzer.analyzeLexicalDiversity(
                    new Locale("pt", "PT"),
                    PARAGRAPH,
                    LDAlgorithm.MTLD,
                    true
            );
        } catch (LinguiniException e) {
            Assertions.fail("Failed to analyze text", e);
            return;
        }

        // System.out.println("MTLD + lemma (paragraph): " + result);

        Assertions.assertEquals("108.575",
                String.format(Locale.US, "%.3f", result));
    }

    @Test
    public final void testHddParagraph() {

        Double result;
        try {
            result = TextAnalyzer.analyzeLexicalDiversity(
                    new Locale("pt", "PT"),
                    PARAGRAPH,
                    LDAlgorithm.HDD,
                    false
            );
        } catch (LinguiniException e) {
            Assertions.fail("Failed to analyze text", e);
            return;
        }

        // System.out.println("HDD (paragraph): " + result);

        Assertions.assertEquals("0.897",
                String.format(Locale.US, "%.3f", result));
    }

    @Test
    public final void testHddParagraphLemmatized() {

        Double result;
        try {
            result = TextAnalyzer.analyzeLexicalDiversity(
                    new Locale("pt", "PT"),
                    PARAGRAPH,
                    LDAlgorithm.HDD,
                    true
            );
        } catch (LinguiniException e) {
            Assertions.fail("Failed to analyze text", e);
            return;
        }

        // System.out.println("HDD + lemma (paragraph): " + result);

        Assertions.assertEquals("0.872",
                String.format(Locale.US, "%.3f", result));
    }

    @RepeatedTest(10)
    public final void testVocdParagraph() {

        Double result;
        try {
            result = TextAnalyzer.analyzeLexicalDiversity(
                    new Locale("pt", "PT"),
                    PARAGRAPH,
                    LDAlgorithm.VOCD,
                    false
            );
        } catch (LinguiniException e) {
            Assertions.fail("Failed to analyze text", e);
            return;
        }

        // System.out.println("VOCD (paragraph): " + result);

        Assertions.assertTrue(
                Double.compare(163.000D, result) < 0 &&
                        Double.compare(166.000D, result) > 0,
                "Expected 163.000 < D < 166.000, but voc-D was " + result
        );
    }

    @RepeatedTest(10)
    public final void testVocdParagraphLemmatized() {

        Double result;
        try {
            result = TextAnalyzer.analyzeLexicalDiversity(
                    new Locale("pt", "PT"),
                    PARAGRAPH,
                    LDAlgorithm.VOCD,
                    true
            );
        } catch (LinguiniException e) {
            Assertions.fail("Failed to analyze text", e);
            return;
        }

        // System.out.println("VOCD + lemma (paragraph): " + result);

        Assertions.assertTrue(
                Double.compare(124.000D, result) < 0 &&
                        Double.compare(126.500D, result) > 0,
                "Expected 135.000 < D < 137.000, but voc-D was " + result
        );
    }

    @Test
    public final void testHddText() {

        Double result;
        try {
            result = TextAnalyzer.analyzeLexicalDiversity(
                    new Locale("pt", "PT"),
                    TEXT,
                    LDAlgorithm.HDD,
                    false
            );
        } catch (LinguiniException e) {
            Assertions.fail("Failed to analyze text", e);
            return;
        }

        // System.out.println("HDD (text): " + result);

        Assertions.assertTrue(
                Double.compare(0.934D - EPSILON, result) < 0 &&
                        Double.compare(0.934D + EPSILON, result) > 0
        );
    }

    @Test
    public final void testMtldText() {

        Double result;
        try {
            result = TextAnalyzer.analyzeLexicalDiversity(
                    new Locale("pt", "PT"),
                    TEXT,
                    LDAlgorithm.MTLD,
                    false
            );
        } catch (LinguiniException e) {
            Assertions.fail("Failed to analyze text", e);
            return;
        }

        // System.out.println("MTLD (text): " + result);

        Assertions.assertTrue(
                Double.compare(240.045 - EPSILON, result) < 0 &&
                        Double.compare(240.045 + EPSILON, result) > 0
        );
    }

    @RepeatedTest(10)
    public final void testVocdText() {

        Double result;
        try {
            result = TextAnalyzer.analyzeLexicalDiversity(
                    new Locale("pt", "PT"),
                    TEXT,
                    LDAlgorithm.VOCD,
                    false
            );
        } catch (LinguiniException e) {
            Assertions.fail("Failed to analyze text", e);
            return;
        }

        // System.out.println("VOCD (text): " + result);

        Assertions.assertTrue(
                Double.compare(272.000D, result) < 0 &&
                        Double.compare(281.000D, result) > 0,
                "Expected 272.000 < D < 281.000, but voc-D was " + result
        );
    }

    @Test
    public final void testHddTextLemmatized() {

        Double result;
        try {
            result = TextAnalyzer.analyzeLexicalDiversity(
                    new Locale("pt", "PT"),
                    TEXT,
                    LDAlgorithm.HDD,
                    true
            );
        } catch (LinguiniException e) {
            Assertions.fail("Failed to analyze text", e);
            return;
        }

        // System.out.println("HDD + lemma (text): " + result);

        Assertions.assertTrue(
                Double.compare(0.905D - EPSILON, result) < 0 &&
                        Double.compare(0.905D + EPSILON, result) > 0
        );
    }

    @Test
    public final void testMtldTextLemmatized() {

        Double result;
        try {
            result = TextAnalyzer.analyzeLexicalDiversity(
                    new Locale("pt", "PT"),
                    TEXT,
                    LDAlgorithm.MTLD,
                    true
            );
        } catch (LinguiniException e) {
            Assertions.fail("Failed to analyze text", e);
            return;
        }

        /// System.out.println("MTLD + lemma (text): " + result);

        Assertions.assertTrue(
                Double.compare(157.333D - EPSILON, result) < 0 &&
                        Double.compare(157.333D + EPSILON, result) > 0
        );
    }

    @RepeatedTest(10)
    public final void testVocdTextLemmatized() {

        Double result;
        try {
            result = TextAnalyzer.analyzeLexicalDiversity(
                    new Locale("pt", "PT"),
                    TEXT,
                    LDAlgorithm.VOCD,
                    true
            );
        } catch (LinguiniException e) {
            Assertions.fail("Failed to analyze text", e);
            return;
        }

        // System.out.println("VOCD + lemma (text): " + result);

        Assertions.assertTrue(
                Double.compare(178.500D, result) < 0 &&
                        Double.compare(183.500D, result) > 0,
                "Expected 178.500 < D < 183.500, but voc-D was " + result
        );
    }
}
