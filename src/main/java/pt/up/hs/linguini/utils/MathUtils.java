package pt.up.hs.linguini.utils;

/**
 * Utilities functions for calculations.
 *
 * @author José Carlos Paiva <code>josepaiva94@gmail.com</code>
 */
public class MathUtils {

    /**
     * Hypergeometric probability
     *
     * @param successes           total successes
     * @param sampleSize          sample size
     * @param populationSize      total items
     * @param populationSuccesses                successes in sample
     * @return hypergeometric probability
     */
    public static double hypergeometric(
            int successes, int sampleSize, int populationSize, int populationSuccesses
    ) {
        try {
            double prob = 1.0 - (
                    (
                            (double) choose(populationSuccesses, successes) *
                                    choose(
                                            (populationSize - populationSuccesses),
                                            (sampleSize - successes)
                                    )
                    ) / (double) choose(populationSize, sampleSize)
            );
            prob = prob * (1.0D / (double) sampleSize);
            return prob;
        } catch (ArithmeticException e) {
            return 0;
        }
    }

    /**
     * 'from n choose r'
     *
     * @param n Size of population
     * @param k Size of sample
     * @return nr of combinations
     */
    public static double choose(int n, int k) {
        if (k >= 0 && k <= n) {
            double ntok = 1;
            double ktok = 1;
            int it = Math.min(k, n - k) + 1;
            for (int t = 1; t < it; t++) {
                ntok *= n;
                ktok *= t;
                n -= 1;
            }
            return ntok / ktok;
        } else {
            return 0;
        }
    }

    /**
     * Calculate the factorial of x, i.e., x!
     *
     * @param x the number to calculate the factorial of
     * @return x!
     */
    public static long factorial(long x) {
        if (x <= 1) {
            return 1;
        } else {
            return x * factorial(x - 1);
        }
    }
}
