package pt.up.hs.linguini.test.unit.filters;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.platform.runner.JUnitPlatform;
import org.junit.runner.RunWith;

import pt.up.hs.linguini.models.Token;
import pt.up.hs.linguini.exceptions.AnalyzerException;
import pt.up.hs.linguini.filters.StopTokenFilter;

import java.util.Locale;

/**
 * Unit tests for stopwords token filter.
 *
 * @author José Carlos Paiva <code>josepaiva94@gmail.com</code>
 */
@RunWith(JUnitPlatform.class)
public class TestStopTokenFilter {

    private StopTokenFilter filter;

    public TestStopTokenFilter() {
        super();
    }

    @BeforeEach
    public void setup() {
        try {
            filter = new StopTokenFilter(new Locale("pt", "PT"));
        } catch (AnalyzerException ae) {
            Assertions.fail("Failed to initialize filter", ae);
        }
    }

    @Test
    public final void testAccept() {
        Assertions.assertFalse(filter.accept(new Token(0, "a")));
        Assertions.assertFalse(filter.accept(new Token(0, "e")));
        Assertions.assertTrue(filter.accept(new Token(0, "teste")));
        Assertions.assertFalse(filter.accept(new Token(0, "ainda")));
        Assertions.assertFalse(filter.accept(new Token(0, "nós")));
        Assertions.assertFalse(filter.accept(new Token(0, "aquelas")));
        Assertions.assertFalse(filter.accept(new Token(0, "portanto")));
        Assertions.assertTrue(filter.accept(new Token(0, "anda")));
        Assertions.assertFalse(filter.accept(new Token(0, "ora")));
        Assertions.assertTrue(filter.accept(new Token(0, "")));
    }
}
