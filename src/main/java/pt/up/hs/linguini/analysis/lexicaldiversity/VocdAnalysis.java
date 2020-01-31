package pt.up.hs.linguini.analysis.lexicaldiversity;

import pt.up.hs.linguini.analysis.Analysis;
import pt.up.hs.linguini.analysis.exceptions.AnalysisException;
import pt.up.hs.linguini.models.HasWord;
import pt.up.hs.linguini.utils.Pair;

import java.util.*;

/**
 * [Description here]
 *
 * @author José Carlos Paiva <code>josepaiva94@gmail.com</code>
 */
public class VocdAnalysis<T extends HasWord>
        implements Analysis<List<T>, Double> {
    private static final int MINIMUM_TOKENS = 50;

    private static final int DEFAULT_NR_OF_TRIALS = 10;
    private static final int DEFAULT_VOCD_MIN_SAMPLE_SIZE = 35;
    private static final int DEFAULT_VOCD_MAX_SAMPLE_SIZE = 50;
    private static final int DEFAULT_INCR_SAMPLE_SIZE = 1;
    private static final int DEFAULT_NR_OF_STEP_TRIALS = 100;

    private static final double EPSILON = 0.001;

    private int nrTrials;
    private int minSampleSize;
    private int maxSampleSize;
    private int incrSampleSize;

    private int nrStepTrials;

    public VocdAnalysis() {
        this.nrTrials = DEFAULT_NR_OF_TRIALS;
        this.minSampleSize = DEFAULT_VOCD_MIN_SAMPLE_SIZE;
        this.maxSampleSize = DEFAULT_VOCD_MAX_SAMPLE_SIZE;
        this.incrSampleSize = DEFAULT_INCR_SAMPLE_SIZE;
        this.nrStepTrials = DEFAULT_NR_OF_STEP_TRIALS;
    }

    public VocdAnalysis(
            int nrTrials,
            int minSampleSize,
            int maxSampleSize,
            int incrSampleSize,
            int nrStepTrials
    ) {
        this.nrTrials = nrTrials;
        this.minSampleSize = minSampleSize;
        this.maxSampleSize = maxSampleSize;
        this.incrSampleSize = incrSampleSize;
        this.nrStepTrials = nrStepTrials;
    }

    @Override
    public Double execute(List<T> tokens) throws AnalysisException {

        if (tokens.size() < MINIMUM_TOKENS) {
            throw new AnalysisException("Cannot calculate lexical" +
                    " diversity in texts with less than " + MINIMUM_TOKENS +
                    " words.");
        }

        return computeD(tokens);
    }

    private double computeD(List<T> tokens) {

        List<DMinValue> dMinTrials = new ArrayList<>();
        for (int j = 0; j < nrTrials; j++) {

            List<NTValue> ntTuples = new ArrayList<>();
            for (int i = minSampleSize; i <= maxSampleSize; i += incrSampleSize) {

                Pair<Double, Double> avgTtr = calculateAvgTtr(tokens, i);

                NTValue nt = new NTValue();
                nt.N = i;
                nt.S = nrStepTrials;
                nt.T = avgTtr.getFirst();
                nt.SD = avgTtr.getSecond();
                nt.D = calculateDGivenNAndTtr(i, avgTtr.getFirst());

                ntTuples.add(nt);
            }

            /*for (NTValue ntValue: ntTuples) {
                System.out.printf(
                        "%d %d %.4f %.4f %.4f\n",
                        ntValue.N, ntValue.S, ntValue.T, ntValue.SD, ntValue.D
                );
            }*/

            // calculate mean value of d
            int discard = 0;
            double dAvg = 0.0;
            for (NTValue ntTuple: ntTuples) {
                if (Double.compare(ntTuple.D, 0D) == 0) {
                    discard++;
                    continue;
                }
                dAvg += ntTuple.D;
            }
            dAvg = dAvg / (double) (ntTuples.size() - discard);

            // calculate std dev value of d
            discard = 0;
            double dStd = 0.0;
            for (NTValue ntTuple: ntTuples) {
                if (Double.compare(ntTuple.D, 0D) == 0) {
                    discard++;
                    continue;
                }
                dStd += (ntTuple.D - dAvg) * (ntTuple.D - dAvg);
            }
            dStd = Math.sqrt(dStd / ((double) ntTuples.size() - discard));

            DValue d = new DValue();
            d.ntTuples = ntTuples;
            d.D = dAvg;
            d.STD = dStd;

            /*System.out.printf("D: average = %.3f; std dev. = %.3f\n", d.D, d.STD);*/

            DMinValue dMin = findMinD(dAvg, ntTuples);

            /*System.out.printf("D_optimum     <%.2f; min least sq val = %.3f>\n", dMin.dmin, dMin.mls);*/

            dMinTrials.add(dMin);
        }

        /*double ttr = calculateTTRFromSeq(tokens);
        System.out.printf("   Types,Tokens,TTR:  <%f>\n", ttr);*/

        double dMinAvg = 0;
        for (int j = 0; j < nrTrials; j++) {
            dMinAvg += dMinTrials.get(j).dmin;
        }
        dMinAvg = dMinAvg / nrTrials;

        /*System.out.printf("  D_optimum average:  %.2f\n", dMinAvg);*/

        return dMinAvg;
    }

    private Pair<Double, Double> calculateAvgTtr(List<T> tokens, int sampleSize) {

        List<Double> ttrTrials = new ArrayList<>();

        for (int i = 0; i < nrStepTrials; i++) {

            List<T> mixedList = new ArrayList<>(tokens);
            Collections.shuffle(mixedList);

            List<T> samplingList = mixedList.subList(0, sampleSize);

            double ttr = new BaseTtrAnalysis<>().execute((List<HasWord>) samplingList);

            /*System.out.println("Sample Size = " + sampleSize);
            System.out.println("TTR = " + String.format("%.3f", ttr));*/

            ttrTrials.add(ttr);
        }

        double mean = ttrTrials.parallelStream()
                .mapToDouble(Double::doubleValue)
                .average()
                .orElse(0);

        double stdDev = Math.sqrt(
                ttrTrials.parallelStream()
                    .map(i -> i - mean)
                    .map(i -> i * i)
                    .mapToDouble(i -> i)
                    .average()
                    .orElse(0)
        );

        return new Pair<>(mean, stdDev);
    }

    private double calculateTtrGivenNAndD(int n, double d) {
        return d / n * (Math.sqrt(1 + 2 * n / d) - 1);
    }

    private double calculateDGivenNAndTtr(int n, double ttr) {
        double tmp = 1.0D - ttr;
        if (Double.compare(tmp, 0D) == 0) {
            return 0;
        }
        return 0.5D * (((double) n) * ttr * ttr) / (1.0 - ttr);
    }

    private DMinValue findMinD(double dAv, List<NTValue> ntTuples) {

        double diff = calculateLsGivenDAndPairsNtTuples(dAv, ntTuples) -
                calculateLsGivenDAndPairsNtTuples(dAv - EPSILON, ntTuples);

        int k;
        if (Double.compare(diff, 0) > 0) {
            k = -1;
        } else if (Double.compare(diff, 0) < 0) {
            k = 1;
        } else {
            k = 0;
        }

        if (k == 0) {
            DMinValue dMinValue = new DMinValue();
            dMinValue.dmin = dAv;
            return dMinValue;
        }

        double prevdDLs = dAv;
        double d;
        for (d = dAv; Double.compare(d, 0) > 0 && Double.compare(d, 2 * dAv) < 0; d += k * EPSILON) {
            double next = calculateLsGivenDAndPairsNtTuples(d, ntTuples);
            if (Double.compare(prevdDLs, next) < 0) {
                break;
            }
            prevdDLs = next;
        }

        DMinValue dMinValue = new DMinValue();
        dMinValue.dmin = d;
        dMinValue.mls = prevdDLs;

        return dMinValue;
    }

    private double calculateLsGivenDAndPairsNtTuples(double d, List<NTValue> ntTuples) {
        double ls = 0D;
        for (NTValue ntTuple : ntTuples) {
            double ttr = calculateTtrGivenNAndD(ntTuple.N, d);
            ls += (ntTuple.T - ttr) * (ntTuple.T - ttr);
        }
        return ls;
    }

    static class NTValue {
        int     N;     /* number of tokens */
        int     S;     /* number of segments*/
        double  T;     /* average ttr value */
        double SD;     /* std dev. for ttr  */
        double  D;     /* value of D calculated from equation */
    }

    static class DValue {
        List<NTValue>  ntTuples;    /* set of nt values    */
        double         D;           /* average value of D  */
        double         STD;         /* standard dev.       */
    }

    static class DMinValue {
        double dmin;  /* minimum estimated D value  */
        double mls;   /* minimum least square value */
    }
}
