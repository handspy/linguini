package pt.up.hs.linguini.utils;

/**
 * Utilities functions for calculations.
 *
 * @author José Carlos Paiva <code>josepaiva94@gmail.com</code>
 */
public class MathUtils {

    /**
     * Hypergeometric probability: the probability that an n-trial hypergeometric
     * experiment results in exactly x successes, when the population consists of
     * N items, k of which are classified as successes.
     * <p>
     * (here, population = N, population_successes = k, sample = n, sample_successes = x)
     * h(x; N, n, k) = [ kCx ] * [ N-kCn-x ] / [ NCn ]
     *
     * @param population          total items
     * @param populationSuccesses total successes
     * @param sample              sample size
     * @param sampleSuccesses     successes in sample
     * @return hypergeometric probability
     */
    public static double hypergeometric(
            int population,
            int populationSuccesses,
            int sample,
            int sampleSuccesses) {
        return (combination(populationSuccesses, sampleSuccesses) *
                combination(population - populationSuccesses, sample - sampleSuccesses)) /
                combination(population, sample);
    }

    /**
     * Combinatorics 'from n choose r'
     *
     * @param n Size of population
     * @param r Size of sample
     * @return nr of combinations
     */
    public static double combination(int n, int r) {
        long rFactorial = factorial(r);
        double numerator = 1.0;
        double num = n - r + 1.0;
        while (num < n + 1.0) {
            numerator *= num;
            num += 1.0;
        }
        return numerator / rFactorial;
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
