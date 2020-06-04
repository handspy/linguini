package pt.up.hs.linguini.test.unit.utils;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import pt.up.hs.linguini.utils.MathUtils;

/**
 * Tests on math utilities.
 *
 * @author José Carlos Paiva <code>josepaiva94@gmail.com</code>
 */
public class MathUtilsTest {
    private static final double EPSILON = 0.001;

    @Test
    public final void testChoose() {
        Assertions.assertEquals(1, MathUtils.choose(2, 0));
        Assertions.assertEquals(35, MathUtils.choose(7, 4));
        Assertions.assertEquals(35, MathUtils.choose(7, 3));
        Assertions.assertEquals(126, MathUtils.choose(9, 4));
        Assertions.assertEquals(4.4223468690222905E42, MathUtils.choose(192, 42));
    }

    @Test
    public final void testHypergeometric() {
        double h = MathUtils.hypergeometric(2, 3, 52, 4);
        Assertions.assertTrue(Double.compare(0.3289 + EPSILON, h) > 0);
        Assertions.assertTrue(Double.compare(0.3289 - EPSILON, h) < 0);
        h = MathUtils.hypergeometric(0, 42, 192, 2);
        Assertions.assertTrue(Double.compare(0.0093 + EPSILON, h) > 0);
        Assertions.assertTrue(Double.compare(0.0093 - EPSILON, h) < 0);
    }
}
