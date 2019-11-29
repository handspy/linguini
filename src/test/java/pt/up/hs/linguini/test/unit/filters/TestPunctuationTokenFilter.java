package pt.up.hs.linguini.test.unit.filters;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.platform.runner.JUnitPlatform;
import org.junit.runner.RunWith;
import pt.up.hs.linguini.models.Token;
import pt.up.hs.linguini.filters.PunctuationTokenFilter;

/**
 * Unit tests for punctuation token filter.
 *
 * @author José Carlos Paiva <code>josepaiva94@gmail.com</code>
 */
@RunWith(JUnitPlatform.class)
public class TestPunctuationTokenFilter {
    
    private PunctuationTokenFilter filter;

    public TestPunctuationTokenFilter() {
        super();
    }

    @BeforeEach
    public void setup() {
       filter = new PunctuationTokenFilter();
    }

    @Test
    public final void testAccept() {
        Assertions.assertFalse(filter.accept(new Token(0, ".")));
        Assertions.assertFalse(filter.accept(new Token(0, ",")));
        Assertions.assertTrue(filter.accept(new Token(0, "no")));
        Assertions.assertFalse(filter.accept(new Token(0, "!")));
        Assertions.assertFalse(filter.accept(new Token(0, "\\")));
        Assertions.assertFalse(filter.accept(new Token(0, "?")));
        Assertions.assertFalse(filter.accept(new Token(0, "|")));
        Assertions.assertTrue(filter.accept(new Token(0, "")));
        Assertions.assertFalse(filter.accept(new Token(0, "...")));
    }
}
