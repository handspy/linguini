package pt.up.hs.linguini.pos;

import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.ling.SentenceUtils;
import edu.stanford.nlp.tagger.maxent.MaxentTagger;
import pt.up.hs.linguini.pipeline.Step;
import pt.up.hs.linguini.models.AnnotatedToken;
import pt.up.hs.linguini.models.Token;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Part-of-Speech tagger.
 *
 * @author José Carlos Paiva <code>josepaiva94@gmail.com</code>
 */
public class PoSTagger implements Step<List<Token>, List<AnnotatedToken<String>>> {
    private static final String FILE_PATH_FORMAT =
            "/%s/models/%s.tagger";

    private Locale locale;

    private MaxentTagger maxentTagger;

    public PoSTagger(Locale locale) {
        this.locale = locale;
        this.maxentTagger = new MaxentTagger(
                PoSTagger.class.getResourceAsStream(
                        String.format(
                                FILE_PATH_FORMAT,
                                locale.toString(),
                                locale.toString()
                        )
                )
        );
    }

    /**
     * Annotate a list of tokens with its PoS tag using the part of speech
     * tagger.
     *
     * @param tokens {@link List} list of tokens
     * @return {@link List} list of annotated tokens with its PoS tag
     */
    @Override
    public List<AnnotatedToken<String>> execute(List<Token> tokens) {

        String[] words = tokens
                .parallelStream()
                .map(Token::getWord)
                .toArray(String[]::new);

        assert words.length == tokens.size();

        List<CoreLabel> labels = SentenceUtils.toCoreLabelList(words);

        maxentTagger.tagCoreLabels(labels);

        assert labels.size() == tokens.size();

        List<AnnotatedToken<String>> annotatedTokens = new ArrayList<>();
        for (int i = 0; i < tokens.size(); i++) {
            annotatedTokens.add(
                    new AnnotatedToken<>(
                            tokens.get(i),
                            labels.get(i).tag()
                    )
            );
        }

        return annotatedTokens;
    }
}
