package pt.up.hs.linguini.tokenization;

import pt.up.hs.linguini.pipeline.Step;
import pt.up.hs.linguini.utils.SentenceStream;

import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

/**
 * Split text into sentences.
 *
 * @author José Carlos Paiva <code>josepaiva94@gmail.com</code>
 */
public class SentenceSplitter implements Step<String, List<String>> {

    private final Locale locale;

    public SentenceSplitter() {
        this(Locale.getDefault());
    }

    public SentenceSplitter(Locale locale) {
        this.locale = locale;
    }

    @Override
    public List<String> execute(String text) {
        return SentenceStream
                .sentences(locale, text.chars().mapToObj(i -> String.valueOf((char) i)))
                .collect(Collectors.toList());
    }
}
