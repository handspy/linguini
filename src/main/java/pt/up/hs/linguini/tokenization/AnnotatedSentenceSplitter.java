package pt.up.hs.linguini.tokenization;

import pt.up.hs.linguini.models.AnnotatedToken;
import pt.up.hs.linguini.pipeline.Step;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Split text into sentences.
 *
 * @author José Carlos Paiva <code>josepaiva94@gmail.com</code>
 */
public class AnnotatedSentenceSplitter implements Step<List<AnnotatedToken<String>>, List<List<AnnotatedToken<String>>>> {

    private final Locale locale;

    public AnnotatedSentenceSplitter() {
        this(Locale.getDefault());
    }

    public AnnotatedSentenceSplitter(Locale locale) {
        this.locale = locale;
    }

    @Override
    public List<List<AnnotatedToken<String>>> execute(List<AnnotatedToken<String>> value) {

        List<List<AnnotatedToken<String>>> sentences = new ArrayList<>();

        List<AnnotatedToken<String>> sentence = null;
        for (AnnotatedToken<String> annotatedToken: value) {
            if (sentence == null) {
                sentence = new ArrayList<>();
            }
            sentence.add(annotatedToken);
            if (
                    annotatedToken.getInfo() != null &&
                            annotatedToken.getInfo().equalsIgnoreCase("PUNCT") &&
                            annotatedToken.word().matches("^[?.!]$")
            ) {
                if (sentence.size() == 1 && !sentences.isEmpty()) {
                    sentences.get(sentences.size() - 1).addAll(sentence);
                } else {
                    sentences.add(sentence);
                }
                sentence = null;
            }
        }

        if (sentence != null) {
            sentences.add(sentence);
        }

        return sentences;
    }
}
