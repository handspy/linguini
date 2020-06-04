package pt.up.hs.linguini.test.unit.filters;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pt.up.hs.linguini.filtering.PunctuationTokenFilter;
import pt.up.hs.linguini.filtering.TokenFilter;
import pt.up.hs.linguini.models.Token;
import pt.up.hs.linguini.pipeline.Step;
import pt.up.hs.linguini.tokenization.Tokenizer;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

/**
 * Unit tests for punctuation token filter.
 *
 * @author José Carlos Paiva <code>josepaiva94@gmail.com</code>
 */
public class TestPunctuationTokenFilter {
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
    
    private TokenFilter<Token> filter;

    public TestPunctuationTokenFilter() {
        super();
    }

    @BeforeEach
    public void setup() {
       filter = new PunctuationTokenFilter<>();
    }

    @Test
    public final void testAccept() {

        try {
            List<Token> tokens = filter.execute(
                    Arrays.asList(
                            new Token(0, "."),
                            new Token(0, ","),
                            new Token(0, "no"),
                            new Token(0, "!"),
                            new Token(0, "\\"),
                            new Token(0, "?"),
                            new Token(0, "|"),
                            new Token(0, ""),
                            new Token(0, "...")
                    )
            );

            Assertions.assertIterableEquals(
                    Arrays.asList(
                            new Token(0, "no"),
                            new Token(0, "")
                    ),
                    tokens
            );
        } catch (Exception e) {
            e.printStackTrace();
            Assertions.fail("Error thrown during test", e);
        }
    }

    @Test
    public final void removeFromText() {
        try {

            Step<String, List<Token>> preprocessPipeline =
                    // 1. tokenize text
                    new Tokenizer(new Locale("pt", "PT"), true)
                            // 2. remove punctuation
                            .pipe(new PunctuationTokenFilter<>());

            String noPunctuation = preprocessPipeline.execute(PARAGRAPH)
                    .stream()
                    .map(Token::word)
                    .collect(Collectors.joining());

            Assertions.assertTrue(noPunctuation.matches("^[^,.:;?!_\\[\\]()\"`/*+%={}#$<>'«»\\\\|]+$"));
        } catch (Exception e) {
            e.printStackTrace();
            Assertions.fail("Error thrown during test", e);
        }
    }
}
