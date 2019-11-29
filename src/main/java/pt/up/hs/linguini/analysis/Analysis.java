package pt.up.hs.linguini.analysis;

import pt.up.hs.linguini.exceptions.AnalyzerException;
import pt.up.hs.linguini.models.Token;

import java.util.List;

/**
 * Interface implemented by distinct analyses.
 *
 * @author José Carlos Paiva <code>josepaiva94@gmail.com</code>
 */
public interface Analysis<T, V> {

    /**
     * Execute analysis on a list of tokens from text.
     *
     * @param tokens {@link List} list of tokens for analysis.
     * @return {@link Analysis} the analysis for chaining
     * @throws AnalyzerException if an error occurs during the analysis.
     */
    Analysis<T, V> preprocess(List<Token> tokens) throws AnalyzerException;

    /**
     * Skip pre-processing step.
     *
     * @param tokens {@link List} list of tokens for analysis.
     * @param t {@code V} result of the pre-processing step.
     * @return {@link Analysis} the analysis for chaining
     * @throws AnalyzerException if an error occurs during the analysis.
     */
    Analysis<T, V> skipPreprocessing(List<Token> tokens, T t) throws AnalyzerException;

    /**
     * Execute analysis on a list of pre-processed tokens.
     *
     * @return {@link Analysis} the analysis for chaining
     * @throws AnalyzerException if an error occurs during the analysis.
     */
    Analysis<T, V> execute() throws AnalyzerException;

    /**
     * Get the result of the analysis.
     *
     * @return {@code V} result of the analysis.
     */
    V getResult();
}
