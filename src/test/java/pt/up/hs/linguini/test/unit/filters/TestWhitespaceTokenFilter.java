package pt.up.hs.linguini.test.unit.filters;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.platform.runner.JUnitPlatform;
import org.junit.runner.RunWith;
import pt.up.hs.linguini.exceptions.LinguiniException;
import pt.up.hs.linguini.models.Token;
import pt.up.hs.linguini.filtering.TokenFilter;
import pt.up.hs.linguini.filtering.WhitespaceTokenFilter;

import java.util.Arrays;
import java.util.List;

/**
 * Unit tests for whitespace token filter.
 *
 * @author José Carlos Paiva <code>josepaiva94@gmail.com</code>
 */
@RunWith(JUnitPlatform.class)
public class TestWhitespaceTokenFilter {

    private TokenFilter<Token> filter;

    public TestWhitespaceTokenFilter() {
        super();
    }

    @BeforeEach
    public void setup() {
       filter = new WhitespaceTokenFilter<>();
    }

    @Test
    public final void testAccept() {

        try {
            List<Token> tokens = filter.execute(
                    Arrays.asList(
                        new Token(0, " "),
                        new Token(0, ""),
                        new Token(0, "no"),
                        new Token(0, "\n"),
                        new Token(0, "\r"),
                        new Token(0, "         \n"),
                        new Token(0, "    "),
                        new Token(0, ".")
                    )
            );

            Assertions.assertIterableEquals(
                    Arrays.asList(
                            new Token(0, "no"),
                            new Token(0, ".")
                    ),
                    tokens
            );
        } catch (LinguiniException e) {
            Assertions.fail("Error thrown during test", e);
        }
    }
}
