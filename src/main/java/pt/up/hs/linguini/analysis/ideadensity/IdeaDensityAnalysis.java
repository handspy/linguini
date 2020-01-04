package pt.up.hs.linguini.analysis.ideadensity;

import pt.up.hs.linguini.analysis.exceptions.AnalysisException;
import pt.up.hs.linguini.exceptions.LinguiniException;
import pt.up.hs.linguini.pipeline.Step;

import java.util.List;
import java.util.Locale;

/**
 * Analysis of Idea Density based on IDD3 (Propositional Idea Density from
 * Dependency Trees).
 *
 * @author José Carlos Paiva <code>josepaiva94@gmail.com</code>
 */
public class IdeaDensityAnalysis implements Step<List<Relation>, List<Proposition>> {

    private Locale locale;

    private Engine engine;

    public IdeaDensityAnalysis(Locale locale) throws AnalysisException {
        this.locale = locale;
        this.engine = new Engine(locale);
    }

    @Override
    public List<Proposition> execute(List<Relation> relations)
            throws LinguiniException {

        /*for (Relation relation: relations) {
            System.out.println(relation);
        }*/

        engine.analyze(relations, 0, null, null);

        /*for (Proposition proposition: engine.getProps()) {
            System.out.println(proposition);
        }*/

        return engine.getProps();
    }
}
