package pt.up.hs.linguini.test.unit.filters;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.platform.runner.JUnitPlatform;
import org.junit.runner.RunWith;
import pt.up.hs.linguini.exceptions.LinguiniException;
import pt.up.hs.linguini.filtering.TokenFilter;
import pt.up.hs.linguini.models.Token;
import pt.up.hs.linguini.filtering.PunctuationTokenFilter;

import java.util.Arrays;
import java.util.List;

/**
 * Unit tests for punctuation token filter.
 *
 * @author José Carlos Paiva <code>josepaiva94@gmail.com</code>
 */
@RunWith(JUnitPlatform.class)
public class TestPunctuationTokenFilter {
    
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
}
