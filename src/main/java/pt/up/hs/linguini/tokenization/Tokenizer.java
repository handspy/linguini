package pt.up.hs.linguini.tokenization;

import edu.stanford.nlp.ling.Word;
import edu.stanford.nlp.process.PTBTokenizer;
import edu.stanford.nlp.process.WordTokenFactory;
import pt.up.hs.linguini.models.Replacement;
import pt.up.hs.linguini.models.Token;
import pt.up.hs.linguini.pipeline.Step;
import pt.up.hs.linguini.resources.ResourceLoader;
import pt.up.hs.linguini.resources.exceptions.ResourceLoadingException;
import pt.up.hs.linguini.tokenization.exceptions.TokenizationException;
import pt.up.hs.linguini.utils.StringUtils;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Text tokenizer.
 *
 * @author José Carlos Paiva <code>josepaiva94@gmail.com</code>
 */
public class Tokenizer implements Step<String, List<Token>> {
    private static final String ABBREV_FILE_PATH_FORMAT =
            "/%s/replacements/abbreviations.json";
    private static final String CONTRACTIONS_FILE_PATH_FORMAT =
            "/%s/replacements/contractions.json";
    private static final String CLITICS_FILE_PATH_FORMAT =
            "/%s/replacements/clitics.json";

    private final boolean expandTokens;

    private Pattern abbreviationTarget;
    private Pattern[] contractionTargets;
    private Pattern[] cliticTargets;

    private final Replacement[] abbreviations;
    private final Replacement[] contractions;
    private final Replacement[] clitics;

    public Tokenizer() throws TokenizationException {
        this(Locale.getDefault(), false);
    }

    public Tokenizer(Locale locale, boolean expandTokens) throws TokenizationException {
        try {
            this.abbreviations = ResourceLoader.readReplacements(
                    String.format(ABBREV_FILE_PATH_FORMAT, locale.toString())
            );
            this.contractions = ResourceLoader.readReplacements(
                    String.format(CONTRACTIONS_FILE_PATH_FORMAT, locale.toString())
            );
            this.clitics = ResourceLoader.readReplacements(
                    String.format(CLITICS_FILE_PATH_FORMAT, locale.toString())
            );
        } catch (ResourceLoadingException e) {
            throw new TokenizationException("Could not load tokenizer's replacements", e);
        }
        this.expandTokens = expandTokens;

        load();
    }

    public Tokenizer(
            boolean expandTokens,
            Replacement[] abbreviations,
            Replacement[] contractions,
            Replacement[] clitics
    ) {
        this.expandTokens = expandTokens;
        this.abbreviations = abbreviations;
        this.contractions = contractions;
        this.clitics = clitics;

        load();
    }

    private void load() {
        Arrays.sort(contractions);
        Arrays.sort(clitics);
        Arrays.sort(abbreviations);

        String abbreviationTargetsStr = Arrays.stream(abbreviations)
                .map(Replacement::getTarget)
                .collect(Collectors.joining("|"));
        abbreviationTarget = Pattern.compile(abbreviationTargetsStr);
        contractionTargets = new Pattern[contractions.length];
        for (int i = 0; i < contractions.length; i++) {
            contractionTargets[i] = Pattern.compile(
                    contractions[i].getTarget());
        }
        cliticTargets = new Pattern[clitics.length];
        for (int i = 0; i < clitics.length; i++) {
            cliticTargets[i] = Pattern.compile(clitics[i].getPrefix()
                    + clitics[i].getTarget());
        }
    }

    @Override
    public List<Token> execute(String text) {

        PTBTokenizer<Word> tokenizer = new PTBTokenizer<>(
                new StringReader(text), new WordTokenFactory(), "");

        List<Token> tokens = tokenizer
                .tokenize()
                .parallelStream()
                .map(word -> new Token(word.beginPosition(), word.word()))
                .collect(Collectors.toList());

        for (int i = 0; i < tokens.size(); i++) {
            Token token = tokens.get(i);

            Matcher abbrevMatcher = abbreviationTarget.matcher(token.getWord().toLowerCase());

            // check for overlooked cases
            if (StringUtils.hasPunctuation(token.getWord())) {
                if (
                        !token.getWord().matches(".*\\d\\.\\d.*") &&
                                !token.getWord().matches(".*\\d,\\d.*") &&
                                !token.getWord().matches(".*\\d:\\d.*") &&
                                !token.getWord().matches(".*\\d/\\d.*") &&
                                !abbrevMatcher.matches()) {
                    token.setWord(StringUtils.separatePunctuation(token.getWord()));
                }
            }

            if (expandTokens) {
                for (int j = 0; j < contractions.length; j++) {
                    if (contractionTargets[j].matcher(
                            token.getWord().toLowerCase()).matches()) {
                        if (isCapitalized(token.getWord(), true)) {
                            token.setWord(capitalize(contractions[j].getReplacement(), true));
                        }
                        else if (isCapitalized(token.getWord())) {
                            token.setWord(capitalize(contractions[j].getReplacement()));
                        }
                        else {
                            token.setWord(contractions[j].getReplacement());
                        }
                    }
                }

                for (int j = 0; j < clitics.length; j++) {
                    String[] parts = token.getWord().split("\\s");
                    for (int k = 0; k < parts.length; k++) {
                        String part = parts[k];
                        if (cliticTargets[j].matcher(part.toLowerCase()).matches()) {
                            parts[k] = part.substring(
                                    0,
                                    part.length() - clitics[j].getTarget().length()
                            ) + clitics[j].getReplacement();
                        }
                    }
                    token.setWord(String.join(" ", parts));
                }
            }
        }

        List<Token> preprocessedTokens = new ArrayList<>();

        for (Token token: tokens) {
            String[] parts = token.getWord().split("\\s");
            int start = token.getStart();
            String original = token.getOriginal();
            for (String part: parts) {
                if (!StringUtils.isBlankString(part)) {
                    Token partToken = new Token(start, original);
                    partToken.setWord(part);
                    preprocessedTokens.add(partToken);
                }
            }
        }

        return preprocessedTokens;
    }

    public String groupTokens(String[] tokens) {
        StringBuilder sentence = new StringBuilder();
        for (String token : tokens) {
            sentence.append(token).append(" ");
        }
        return sentence.toString().trim();
    }

    public String groupTokens(String[] tokens, String[] tokenGroups) {
        String sentence = groupTokens(tokens);
        for (String tokenGroup : tokenGroups) {
            if (tokenGroup.length() > 0) {
                sentence = sentence.replaceAll("\\b" + Pattern.quote(tokenGroup)
                        + "\\b", Matcher.quoteReplacement(
                        tokenGroup.replaceAll("\\s", "_")));
            }
        }
        return sentence;
    }

    private boolean isCapitalized(String word) {
        return this.isCapitalized(word, false);
    }

    private boolean isCapitalized(String word, boolean all) {
        if (all) {
            if (word.length() > 0) {
                for (int i = 0; i < word.length(); i++) {
                    if (!Character.isUpperCase(word.charAt(i))) {
                        return false;
                    }
                }
                return true;
            }
            return false;
        }
        if (word.length() > 0) {
            return Character.isUpperCase(word.charAt(0));
        }
        return false;
    }

    private String capitalize(String word, boolean all) {
        if (all) {
            return word.toUpperCase();
        }
        if (word.length() > 0) {
            String[] elements = word.split("\\s");
            String capitalizedWord = "";
            for (String element : elements) {
                String capitalizedElement = element.substring(0, 1).toUpperCase();
                if (element.length() > 1) {
                    capitalizedElement = capitalizedElement.concat(
                            element.substring(1));
                }
                capitalizedWord = capitalizedWord.concat(" " + capitalizedElement);
            }
            return capitalizedWord.trim();
        }

        return word;
    }

    private String capitalize(String word) {
        return this.capitalize(word, false);
    }
}
