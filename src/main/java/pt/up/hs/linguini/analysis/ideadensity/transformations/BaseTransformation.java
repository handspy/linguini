package pt.up.hs.linguini.analysis.ideadensity.transformations;

import pt.up.hs.linguini.Config;
import pt.up.hs.linguini.analysis.ideadensity.Relation;

import java.util.List;

/**
 * Base transformation to be extended by more specific transformations.
 *
 * @author José Carlos Paiva <code>josepaiva94@gmail.com</code>
 */
public abstract class BaseTransformation implements Transformation {

    protected Config config;

    public BaseTransformation(Config config) {
        this.config = config;
    }

    @Override
    public abstract void transform(List<Relation> relations);
}
