package pt.up.hs.linguini.pipeline;

import pt.up.hs.linguini.exceptions.LinguiniException;

import java.util.ArrayList;
import java.util.List;

/**
 * Batch step applier.
 *
 * @author José Carlos Paiva <code>josepaiva94@gmail.com</code>
 */
public class BatchStep<I, O> implements Step<List<I>, List<O>> {

    private final Step<I, O> step;

    public BatchStep(Step<I, O> step) {
        this.step = step;
    }

    @Override
    public List<O> execute(List<I> values) throws LinguiniException {
        List<O> os = new ArrayList<>();
        for (I i: values) {
            os.add(step.execute(i));
        }
        return os;
    }
}
