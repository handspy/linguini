package pt.up.hs.linguini.tokenization;

import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.process.WordToSentenceProcessor;
import pt.up.hs.linguini.exceptions.LinguiniException;
import pt.up.hs.linguini.pipeline.Step;

import java.util.List;

public class TokenizedSentenceSplitter<T extends CoreLabel> implements Step<List<T>, List<List<T>>> {

    @Override
    public List<List<T>> execute(List<T> tokens) throws LinguiniException {
        return new WordToSentenceProcessor<T>()
                .process(tokens);
    }
}
