package pt.up.hs.linguini.test.unit.tokenization;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import pt.up.hs.linguini.exceptions.LinguiniException;
import pt.up.hs.linguini.filtering.WhitespaceTokenFilter;
import pt.up.hs.linguini.models.AnnotatedToken;
import pt.up.hs.linguini.pos.PoSTagger;
import pt.up.hs.linguini.tokenization.AnnotatedSentenceSplitter;
import pt.up.hs.linguini.tokenization.Tokenizer;

import java.util.List;
import java.util.Locale;

public class TestAnnotatedSentenceSplitter {

    private static final Locale LOCALE = new Locale("pt", "PT");

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
            " por vezes pai! De seguida tenho que limpar as coisas da cozinha" +
            "... Costumo sentar-me/deitar-me a descansar um pouco. Por vezes dá" +
            "-me vontade de cozinhar um doce a acabo fazendo 2 tarte ou bolo " +
            "ou biscoitos, o que me vier à mente. Volto a limpar o que sujei!!";


    @Test
    public final void testSentenceSplitting() {

        try {
            List<List<AnnotatedToken<String>>> posTaggedSentences =
                    // 1. tokenize text
                    new Tokenizer(LOCALE, true)
                    // 2. remove whitespaces
                    .pipe(new WhitespaceTokenFilter<>())
                    // 3. PoS Tagging
                    .pipe(new PoSTagger(LOCALE))
                    // 4. Sentence splitting
                    .pipe(new AnnotatedSentenceSplitter(LOCALE))
                    .execute(TEXT);

            Assertions.assertEquals(17, posTaggedSentences.size());
        } catch (LinguiniException e) {
            Assertions.fail(e);
        }
    }
}
