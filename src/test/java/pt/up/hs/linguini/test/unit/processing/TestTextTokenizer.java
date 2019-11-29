package pt.up.hs.linguini.test.unit.processing;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.platform.runner.JUnitPlatform;
import org.junit.runner.RunWith;
import pt.up.hs.linguini.models.Token;
import pt.up.hs.linguini.processing.TextTokenizer;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Unit tests for text tokenizer.
 *
 * @author José Carlos Paiva <code>josepaiva94@gmail.com</code>
 */
@RunWith(JUnitPlatform.class)
public class TestTextTokenizer {
    private static final String SENTENCE = "Ao tomar conhecimento, Maria " +
            "Eduarda, agora rica, parte para o estrangeiro; e Carlos, para " +
            "se distrair, vai correr o mundo.";

    @Test
    public final void testSentenceWithBlanksAndPunctuation() {
        TextTokenizer tokenizer = new TextTokenizer(SENTENCE);
        List<String> tokens = tokenizer
                .collectAll(true, true)
                .parallelStream()
                .map(Token::getOriginal)
                .collect(Collectors.toList());;

        Assertions.assertIterableEquals(
                Arrays.asList(
                        "Ao", " ", "tomar", " ", "conhecimento", ",", " ", "Maria", " ",
                        "Eduarda", ",", " ", "agora", " ", "rica", ",", " ", "parte", " ",
                        "para", " ", "o", " ", "estrangeiro", ";", " ", "e", " ", "Carlos", ",", " ",
                        "para", " ", "se", " ", "distrair", ",", " ", "vai", " ", "correr", " ", "o", " ",
                        "mundo", "."),
                tokens);
    }

    @Test
    public final void testSentenceWithPunctuation() {
        TextTokenizer tokenizer = new TextTokenizer(SENTENCE);
        List<String> tokens = tokenizer
                .collectAll(false, true)
                .parallelStream()
                .map(Token::getOriginal)
                .collect(Collectors.toList());;

        Assertions.assertIterableEquals(
                Arrays.asList(
                        "Ao", "tomar", "conhecimento", ",", "Maria",
                        "Eduarda", ",", "agora", "rica", ",", "parte",
                        "para", "o", "estrangeiro", ";", "e", "Carlos", ",",
                        "para", "se", "distrair", ",", "vai", "correr", "o",
                        "mundo", "."),
                tokens);
    }

    @Test
    public final void testSentenceOnlyWords() {
        TextTokenizer tokenizer = new TextTokenizer(SENTENCE);
        List<String> tokens = tokenizer
                .collectAll(false, false)
                .parallelStream()
                .map(Token::getOriginal)
                .collect(Collectors.toList());

        Assertions.assertIterableEquals(
                Arrays.asList(
                        "Ao", "tomar", "conhecimento", "Maria",
                        "Eduarda", "agora", "rica", "parte",
                        "para", "o", "estrangeiro", "e", "Carlos",
                        "para", "se", "distrair", "vai", "correr", "o",
                        "mundo"),
                tokens);
    }

    @Test
    public final void testSentenceOnlyWordsPosition() {
        TextTokenizer tokenizer = new TextTokenizer(SENTENCE);
        List<Token> tokens = tokenizer
                .collectAll(false, false);

        Assertions.assertEquals("tomar", tokens.get(1).getOriginal());
        Assertions.assertEquals(3, tokens.get(1).getStart());
        Assertions.assertEquals("rica", tokens.get(6).getOriginal());
        Assertions.assertEquals(44, tokens.get(6).getStart());
        Assertions.assertEquals("estrangeiro", tokens.get(10).getOriginal());
        Assertions.assertEquals(63, tokens.get(10).getStart());
        Assertions.assertEquals("mundo", tokens.get(19).getOriginal());
        Assertions.assertEquals(117, tokens.get(19).getStart());
    }
}
