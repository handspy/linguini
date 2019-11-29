package pt.up.hs.linguini.processing;

import pt.up.hs.linguini.models.Token;
import pt.up.hs.linguini.utils.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

/**
 * Tokenization of text.
 *
 * @author José Carlos Paiva <code>josepaiva94@gmail.com</code>
 */
public class TextTokenizer extends StringTokenizer {
    private static final String DELIMITERS = ",.:;?!\\[\\]()\"/*+%={}#$<>'«»" + " \t\n\r\f";

    public TextTokenizer(String text) {
        super(text, DELIMITERS, true);
    }

    public List<Token> collectAll(boolean returnBlanks, boolean returnPunct) {
        List<Token> tokens = new ArrayList<>();

        int start = 0;
        while (hasMoreTokens()) {
            String str = nextToken();
            if (StringUtils.isBlankString(str)) {
                if (returnBlanks)
                    tokens.add(new Token(start, str));
            } else if (StringUtils.isPunctuation(str)) {
                if (returnPunct)
                    tokens.add(new Token(start, str));
            } else {
                tokens.add(new Token(start, str));
            }

            start += str.length();
        }

        return tokens;
    }
}
