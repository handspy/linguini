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

    private static final String TEXT_3 = "\uFEFFRe: Preciso conversar com alg" +
            "uém \n\nEu não sei por onde começar, minha vida ta uma bagunça, " +
            "me sinto só sem rumo. Meu pai usa drogas, minha mãe é uma egoíst" +
            "a, disse que vai embora, que vai ser melhor assim, quer q eu mor" +
            "e de favor na casa dos outros e quer seguir a vida dela, e eu? E" +
            "u fico pra trás, eu fico por minha total e própria conta, acabei" +
            " de me formar no ensino médio, ainda não achei emprego, e to com" +
            " medo, tinha tantos planos para o futuro, eu queria ser tanta co" +
            "isa, mas não tenho apoio de ninguém, ninguém pra me ajudar, eu s" +
            "ei que eu já deveria saber me virar, sei que ela não tem obrigaç" +
            "ão de me da um teto e tudo mais...mas me sinto sendo jogada pra " +
            "fora...além disso não só tem eu, tenho mais duas irmãs mais nova" +
            " e ela quer fazer o mesmo com as outras duas, desde pequena  cui" +
            "do da minhas irmãs, e agora me sinto impotente não tenho um teto" +
            " pra dar a elas, me sinto um fracasso.";

    private static final String TEXT_4 = "Não sei bem como começar a explicar" +
            " ou como começar esse email, mas vou resumir um pouco a minha vi" +
            "da ultimamente.. Meu avo morreu e eu tive uma depressão bem prof" +
            "unda, ja me recuperei um pouco mas ainda doi.. Fazem 7 anos já, " +
            "e depois que eu superei entrei num estado que dá pra descrever c" +
            "omo se eu estivesse vivendo minha vida por fora do meu corpo. Re" +
            "centemente comecei a ter problemas com os meus pais, principalme" +
            "nte por dinheiro, e agora os dois desabafam comigo o tempo todo " +
            "e eu não aguento mais isso. Já orei, comecei a passar mais tempo" +
            " fora de casa p me distrair mas parece que nada funciona. Nao te" +
            "m mais sentido..";

    private static final String TEXT_5 = "Boa noite\n" +
            "\n" +
            "Não tenho mais ninguém em quem confie tanto, ou sinta tanta à von" +
            "tade para expressar o que sinto. Nunca gostei de falar do que sen" +
            "tia.\nNão é só por causa dela que sofro, isto já vem bem antes de" +
            " sentir isto por ela. Mas nem eu sei bem porque estou assim, só s" +
            "into um enorme vazio e sem vontade de viver... \n";

    private static final String TEXT_6 = "Ainda fiquei sozinha com a minha mãe" +
            " que embora seja a minha mãe e nao a queira mal mas é manipulador" +
            "a e por nada trata mal as pessoas que mais próximas dela estão ne" +
            "ste caso só sou eu antigamente era o meu pai e o meu irmão ";

    private static final String TEXT_7 = "O problema é que eu nunca saia se ca" +
            "sa para fazer nada, sempre gostei de ficar em casa, minha namorad" +
            "a eu conheci na escola eu estava no 1 ano do segundo grau e ela 3" +
            " ano do segundo grau, isto foi no ano passado, ela se formou e ag" +
            "ora está fazendo direito, eu consegui pular o ensino médio e fui " +
            "fazer Física, e eu volto somente fim de semana e as vezes eu comb" +
            "ino de buscar minha namorada na faculdade dela quando ela tem aul" +
            "a sábado, combino de ir tomar um açai, porquê ela gosta disto, ma" +
            "s isto sempre de manhã, eu não faço nada a noite, não combino de " +
            "sair a noite para eles não reclamarem, nunca tive amigos, nunca g" +
            "ostei de ter, nunca fui em casa de colegas nem nada disto, sabe e" +
            "u trato meus pais muito bem, nunca maltratei eles, trato todos mu" +
            "ito bem, sabe? Eu gosto de ajudar os outros, participo de ONGs, i" +
            "nclusive estou fazendo Física pois eu tenho um projeto de Exo esq" +
            "ueleto, para ajudar pessoas que tem dificuldade de movimentos, eu" +
            " gosto de ajudar, para mim ajudar está até mesmo em pequenas cois" +
            "as, inclusive tratar bem as pessoas, entende?\nSempre trabalhei d" +
            "es de novo para não precisar depender de meus pais, vendia picolé" +
            " na rua, já trabalhei como menor aprendiz depois que fui demitido" +
            " voltei a trabalhar vendendo picolé na rua, agora estou procurand" +
            "o emprego pois estou em outra cidade lá é frio e não dá para vend" +
            "er picolé. Eu peço para eles explicarem por que eles não me deixa" +
            "m ver minha namorada direito e eles não conseguem explicar, ficam" +
            " falando que são meus pais e este é o motivo, minha namorada é um" +
            "a pessoa muito boa, por isso gosto dela, ela me ajuda na ONG, me " +
            "ajuda em meus projetos, me dá Amor e Carinho, é uma pessoa maravi" +
            "lhosa para mim, eu não entendo por que eles são assim comigo.\n\n" +
            "\nEu não fiz nada de errado desta vez, minha namorada acha que eu" +
            " fiz, então ela terminou comigo mesmo sem ter motivo desta vez, e" +
            "la acredita em uma mentira pois não fiz, estou começando a ter es" +
            "quizofrenia e ficar gago quando falo com ela.\n\n\nInfelizmente n" +
            "ão gosto de tomar remédios, não tomo nem para dor, nem para febre" +
            " nem nada, eu fui em um médico recentemente e ele me passou um re" +
            "médio para tomar mas eu acabo não tomando, acho que ele pode me d" +
            "eixar dependente e futuramente quero ter uma vida saudável sem re" +
            "médios.\n\n\nDesisti da faculdade, estou agora passando mal com f" +
            "ebre, dor de cabeça, fraco, queria fazer meus projetos.\n\n\nNão " +
            "gosto de psicólogos, já tentei ir mas não me senti confortável.\n" +
            "\n\nDa vontade de fugir de casa e ir viver minha vida, minha famí" +
            "lia é muito ruim, eu consegui passar na faculdade particular maio" +
            "r conceituada na minha cidade natal e ainda ganhei bolsa integra" +
            "l e eles não estão nem aí, nem um parabéns ele deram, nem um apo" +
            "io eles me dão, eu estou fazendo um projeto de carro, montando c" +
            "hassi, motor, rodas, tudo, estou montando um projeto bem legal e" +
            " eles ficam falando para desistir, que não vai dar certo, tudo qu" +
            "e eu faço eles me colocam para pior.\n\n\nEla não quer conversar," +
            " já liguei várias vezes já tentei ir na casa dela.\n\n\nDa vontad" +
            "e de não fazer mais nada.\n\n\nEu comecei a faculdade faz uns qua" +
            "tro meses e estou fazendo em outra cidade, longe de todos morando" +
            " sozinho, estou dedicando aos estudos lá a semana toda e no fim d" +
            "e semana eu volto para minha cidade, quando volto tudo que quero " +
            "é descansar mas minha família fica dizendo que eu não faço as coi" +
            "sas direito, fica vigiando olhando minhas coisas da faculdade, el" +
            "es não confiam em mim e a única coisa que me deixa feliz agora é " +
            "minha namorada que mora na cidade que nasci, perto de onde minha " +
            "família mora e eles ficam me proibindo de ver minha namorada, fic" +
            "am brigando comigo, eu sou responsável, estou tirando boas notas," +
            " sempre respeitei eles, sou melhor possível com eles, mas eles pa" +
            "recem que gostam de complicar as coisas. Eu quero viver minha vid" +
            "a, fazer minhas coisas sozinho eu e minha namorada.\n\n\nNão gost" +
            "o.\n\n\n\n\nMinha namorada que era a única pessoa que me mantinha" +
            " feliz me deixou, agora a única coisa que vejo como saída é tenta" +
            "r o que título já diz, agradeço sua ajuda de tentar me fazer melh" +
            "orar, desculpa por lhe decepcionar também, mas parece que eu não " +
            "sou uma pessoa boa.\n\n\nSerá que conseguirei algo?\n\n\nTenho si" +
            "m, mais recente idéia minha é me internar para não suicidar e pen" +
            "sar na minha vida, penso em tomar muitos remédios ou também uma f" +
            "aca.\n\n\nJá pensei, mas eu penso que o suicídio seria uma forma " +
            "feia seria uma forma egoísta, mas as vezes eu penso sim, penso as" +
            " vezes em fugir de casa e não ver mais aquelas pessoas, mas fugin" +
            "do eu não teria minha namorada, não posso pedir para ela abandona" +
            "r a faculdade dela, o que penso em fazer para resolver tudo é arr" +
            "umar algum emprego com carteira assinada e eu pedir emancipação p" +
            "ara o Juiz, assim eu poderia alugar um lugar para mim e viver soz" +
            "inho, sem que os outros me tragam problemas desnecessários, mas a" +
            "s vezes isto parece muito longe de acontecer, ando dias e dias pr" +
            "ocurando emprego bato de porta em porta das empresas e ninguém me" +
            " da emprego.\n\n\nPor favor pode me ajudar, preciso muito de ajud" +
            "a neste momento, por favor me responde.\n";

    private static final String TEXT_8 = "Aquilo que queria falar, ou melhor " +
            "escrever, é sobre o meu percurso praxístico ate agora. No ano pa" +
            "ssado, tive um momento em que pensei desistir! Quando pensei sob" +
            "re o porquê não tinha bem a noção, mas não tinha paciência para " +
            "tudo aquilo que estavam a exigir de mim: todas as semanas tinha " +
            "que acordar às seis da manhã para aparecer e depois não achava e" +
            "ngraçadas algumas coisas que aconteciam e diziam. Percebi que ti" +
            "nha optado por ficar por causa dos meus colegas e porque queria " +
            "fazer parte deste mundo, apesar de tudo.\nO momento mais traumát" +
            "ico deste ano foi, sem dúvida, quando o meu avô foi para o hospi" +
            "tal devido a um AVC hemorrágico, na sexta-feira santa, mesmo ant" +
            "es da Páscoa. Estava em casa, tinha acordado cedo porque ia ter " +
            "com uma amiga, quando soube. A minha mãe saltou da cama,  (no me" +
            "u quarto consegui ouvir) e agarrada ao telemóvel desatou a corre" +
            "r ate ao meu quarto. Já não é a primeira vez que um telefonema d" +
            "estes acontece, mas fiquei alarmada de qualquer forma. Suspirei " +
            "logo quando a minha mãe me disse que o meu avô tinha ido para o " +
            "hospital, não pensei na altura que fosse grave. Pensei que o que" +
            " lhe estava a acontecer, fosse o que fosse, voltaria para casa n" +
            "esse dia ou no seguinte.\nMas isso não aconteceu. Continuou em c" +
            "oma durante quinze dias.\nEntretanto, quando voltei das férias d" +
            "a Páscoa, tive que avisar os meus colegas do porquê que não tinh" +
            "a cabeça para ir, nessa semana, à praxe. Se não andasse, de cert" +
            "o não contaria a ninguém, porque eu sou mui";

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
        } catch (LinguiniException e) {
            e.printStackTrace();
            Assertions.fail("Error thrown during test", e);
        }
    }
}
