package pt.up.hs.linguini.test.unit.filters;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pt.up.hs.linguini.exceptions.LinguiniException;
import pt.up.hs.linguini.filtering.StopTokenFilter;
import pt.up.hs.linguini.filtering.TokenFilter;
import pt.up.hs.linguini.filtering.exceptions.FilteringException;
import pt.up.hs.linguini.models.Token;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;

/**
 * Unit tests for stopwords token filter.
 *
 * @author José Carlos Paiva <code>josepaiva94@gmail.com</code>
 */
public class TestStopTokenFilter {

    private TokenFilter<Token> filter;

    public TestStopTokenFilter() {
        super();
    }

    @BeforeEach
    public void setup() {
        try {
            filter = new StopTokenFilter<>(new Locale("pt", "PT"));
        } catch (FilteringException fe) {
            Assertions.fail("Failed to initialize filter", fe);
        }
    }

    @Test
    public final void testAccept() {

        try {
            List<Token> tokens = filter.execute(
                    Arrays.asList(
                            new Token(0, "a"),
                            new Token(0, "e"),
                            new Token(0, "teste"),
                            new Token(0, "ainda"),
                            new Token(0, "nós"),
                            new Token(0, "aquelas"),
                            new Token(0, "portanto"),
                            new Token(0, "anda"),
                            new Token(0, "ora"),
                            new Token(0, "")
                    )
            );

            Assertions.assertIterableEquals(
                    Arrays.asList(
                            new Token(0, "teste"),
                            new Token(0, "anda"),
                            new Token(0, "")
                    ),
                    tokens
            );
        } catch (LinguiniException e) {
            Assertions.fail("Error thrown during test", e);
        }
    }
}
