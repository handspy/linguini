package pt.up.hs.linguini.test.unit.analysis;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import pt.up.hs.linguini.TextAnalyzer;
import pt.up.hs.linguini.exceptions.LinguiniException;

import java.util.Locale;

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

    private static final String TEXT_1 = "Ao acordar tenho sempre" +
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

    private static final String TEXT_2 = "A experiência mais traumática da mi" +
            "nha vida foi ficar com os membros paralisados durante um ataque " +
            "de ansiedade. Nesse momento, senti-me completamente impotente, t" +
            "udo o que eu tentasse fazer para me mexer era inútil, como se es" +
            "tivesse acorrentada. “Incrível” como o psicológico afetou o físi" +
            "co de uma maneira que até ao momento eu considerava impossível. " +
            "Foi assustador. Senti que ia ficar assim para sempre e o medo go" +
            "vernou-me. Chorei e a minha mãe também. Muito. Só queria, naquel" +
            "e momento, puder mexer 1 dedo que fosse, mas não conseguia. Diss" +
            "eram que era normal ocorrer esta situação e fiquei surpresa. Nun" +
            "ca tinha tido tal reação durante um ataque de ansiedade, nunca t" +
            "inha ficado imobilizada. Depois, voltei ao normal.\n" +
            "Amo os pais. Amo o meu namorado. Amo os meus amigos. O meu maior" +
            " objetivo é puder retribuir o orgulho que têm em mim. Entrei na " +
            "faculdade, mas não sei se é realmente isto que quero. Por um lad" +
            "o, sinto que, se desistir, vou desiludir todos os que me rodeiam" +
            ", pois têm tantas expectativas em mim. Por outro, sinto que podi" +
            "a fazer o que realmente quero e mostrar que nem toda a gente pre" +
            "cisa de uma licenciatura/mestrado para ser feliz. Não que não go" +
            "ste do curso em que estou, porque adoro, mas sinto que há qualqu" +
            "er coisa que me diz que pode";

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
    public final void testExecuteText1() throws LinguiniException {

        double idd;
        try {
            idd = TextAnalyzer.analyzeIdeaDensity(LOCALE, TEXT_1);
        } catch (LinguiniException e) {
            e.printStackTrace();
            Assertions.fail("Error thrown during test", e);
            return;
        }

        Assertions.assertEquals("0.347",
                String.format(Locale.US, "%.3f", idd));
    }

    @Test
    public final void testExecuteText2() throws LinguiniException {

        double idd;
        try {
            idd = TextAnalyzer.analyzeIdeaDensity(LOCALE, TEXT_2);
        } catch (LinguiniException e) {
            Assertions.fail("Error thrown during test", e);
            return;
        }

        Assertions.assertEquals("0.548",
                String.format(Locale.US, "%.3f", idd));
    }
}
