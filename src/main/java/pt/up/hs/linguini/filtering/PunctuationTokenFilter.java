package pt.up.hs.linguini.filtering;

import pt.up.hs.linguini.models.HasWord;
import pt.up.hs.linguini.utils.StringUtils;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Token filter that accepts only non-punctuation characters.
 *
 * @author José Carlos Paiva <code>josepaiva94@gmail.com</code>
 */
public class PunctuationTokenFilter<T extends HasWord>
        implements TokenFilter<T> {

    @Override
    public List<T> execute(List<T> tokens) {
        return tokens
                .parallelStream()
                .filter(token -> !StringUtils.isPunctuation(token.word().trim()))
                .collect(Collectors.toList());
    }
}
