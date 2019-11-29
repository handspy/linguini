package pt.up.hs.linguini.test.unit.filters;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.platform.runner.JUnitPlatform;
import org.junit.runner.RunWith;
import pt.up.hs.linguini.models.Token;
import pt.up.hs.linguini.filters.TokenFilter;
import pt.up.hs.linguini.filters.WhitespaceTokenFilter;

/**
 * Unit tests for whitespace token filter.
 *
 * @author José Carlos Paiva <code>josepaiva94@gmail.com</code>
 */
@RunWith(JUnitPlatform.class)
public class TestWhitespaceTokenFilter {

    private TokenFilter filter;

    public TestWhitespaceTokenFilter() {
        super();
    }

    @BeforeEach
    public void setup() {
       filter = new WhitespaceTokenFilter();
    }

    @Test
    public final void testAccept() {
        Assertions.assertFalse(filter.accept(new Token(0, " ")));
        Assertions.assertFalse(filter.accept(new Token(0, "")));
        Assertions.assertTrue(filter.accept(new Token(0, "no")));
        Assertions.assertFalse(filter.accept(new Token(0, "\n")));
        Assertions.assertFalse(filter.accept(new Token(0, "\r")));
        Assertions.assertFalse(filter.accept(new Token(0, "         \n")));
        Assertions.assertFalse(filter.accept(new Token(0, "    ")));
        Assertions.assertTrue(filter.accept(new Token(0, ".")));
    }
}
