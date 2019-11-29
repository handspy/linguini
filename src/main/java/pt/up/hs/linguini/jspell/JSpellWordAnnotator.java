package pt.up.hs.linguini.jspell;

import pt.up.hs.linguini.models.Token;
import pt.up.hs.linguini.models.*;
import pt.up.hs.linguini.utils.InMemoryCache;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Word annotator using JSpell.
 *
 * @author José Carlos Paiva <code>josepaiva94@gmail.com</code>
 */
public class JSpellWordAnnotator implements AutoCloseable {

    private static final Pattern HEADER_PATTERN =
            Pattern.compile("^([*&])\\s([^\\s]+)\\s([^\\s]+)\\s:");
    private static final Pattern LEX_PATTERN =
            Pattern.compile("lex\\((.[^,\\s]*),\\s*\\[([^]]*)],\\s*\\[([^]]*)],\\s*\\[([^]]*)],\\s*\\[([^]]*)]\\)");
    private static final Pattern ERROR_CORRECTIONS_PATTERN =
            Pattern.compile("([^=]+)=\\s+lex\\((.[^,\\s]+),\\s*\\[([^]]*)],\\s*\\[([^]]*)],\\s*\\[([^]]*)],\\s*\\[([^]]*)]\\)[,;]?\\s?");

    protected static InMemoryCache<String, AnnotatedToken<JSpellInfo>> cache =
            new InMemoryCache<>(3600, 600, 500);

    private static final String WORD_CATEGORY_PROP = "CAT";
    private static final String WORD_GLOBAL_EMOTION_PROP = "EmoGlobal";
    private static final String WORD_INTERMEDIATE_EMOTION_PROP = "EmoIntermediate";
    private static final String WORD_SPECIFIC_EMOTION_PROP = "EmoSpecific";

    private JSpellWrapper jSpell;

    public JSpellWordAnnotator(Locale locale) throws IOException, URISyntaxException {
        this.jSpell = new JSpellWrapper(locale);
        jSpell.start();
    }

    public AnnotatedToken<JSpellInfo> annotate(Token token) throws IOException {
        AnnotatedToken<JSpellInfo> annotatedToken = cache.get(token.getWord());
        if (annotatedToken == null) {
            String output = jSpell.process(token.getWord());
            annotatedToken = processJSpellOutput(token, output);
            cache.put(token.getWord(), annotatedToken);
        }
        return annotatedToken;
    }

    @Override
    public void close() throws IOException {
        jSpell.close();
    }

    private AnnotatedToken<JSpellInfo> processJSpellOutput(Token token, String output) {

        output = output.trim();

        Matcher m = HEADER_PATTERN.matcher(output);

        if (m.find()) { // normal word or spelling error

            String matching = m.group(0);
            String symbol = m.group(1);

            if (symbol.equals("*")) { // normal word
                return new AnnotatedToken<>(
                        token,
                        new JSpellInfo(processLexes(output.substring(matching.length())))
                );
            } else { // spelling error
                return new AnnotatedToken<>(
                        token,
                        new JSpellInfo(processSuggestedCorrections(output.substring(matching.length())))
                );
            }
        } else { // punctuation
            return new AnnotatedToken<>(token, new JSpellInfo());
        }
    }

    private List<JSpellLex> processLexes(String output) {

        List<JSpellLex> lexes = new ArrayList<>();

        Matcher m = LEX_PATTERN.matcher(output);
        while (m.find()) {
            JSpellLex lex = buildLex(
                    m.group(1), m.group(2), m.group(3), m.group(4), m.group(5)
            );
            lexes.add(lex);
        }

        return lexes;
    }

    private Map<String, List<JSpellLex>> processSuggestedCorrections(
            String output) {

        Map<String, List<JSpellLex>> corrections = new HashMap<>();

        Matcher m = ERROR_CORRECTIONS_PATTERN.matcher(output);
        while (m.find()) {

            String suggested = m.group(1).toLowerCase();

            JSpellLex lex = buildLex(
                    m.group(2), m.group(3), m.group(4), m.group(5), m.group(6));

            corrections
                    .computeIfAbsent(suggested, w -> new ArrayList<>())
                    .add(lex);
        }

        return corrections;
    }

    private JSpellLex buildLex(String lemma,
                                              String classification,
                                              String prefix,
                                              String suffixT1, String suffixT2) {

        JSpellLex lex = new JSpellLex();
        lex.setLemma(lemma.toLowerCase());

        Map<String, String> classificationProps = extractKeyValueData(classification);

        if (classificationProps.containsKey(WORD_GLOBAL_EMOTION_PROP)) {

            Emotion emotion = JSpellMapper.mapEmotion(
                    classificationProps.get(WORD_GLOBAL_EMOTION_PROP),
                    classificationProps.get(WORD_INTERMEDIATE_EMOTION_PROP),
                    classificationProps.get(WORD_SPECIFIC_EMOTION_PROP));

            lex.setEmotion(emotion);
        }

        Map<String, String> prefixProps = extractKeyValueData(prefix);
        Map<String, String> suffixT1Props = extractKeyValueData(suffixT1);
        Map<String, String> suffixT2Props = extractKeyValueData(suffixT2);

        if (prefixProps.containsKey(WORD_CATEGORY_PROP)) {
            lex.setCategory(
                    JSpellMapper.mapCategory(prefixProps.get(WORD_CATEGORY_PROP))
            );
        } else if (suffixT1Props.containsKey(WORD_CATEGORY_PROP)) {
            lex.setCategory(
                    JSpellMapper.mapCategory(suffixT1Props.get(WORD_CATEGORY_PROP))
            );
        } else if (suffixT2Props.containsKey(WORD_CATEGORY_PROP)) {
            lex.setCategory(
                    JSpellMapper.mapCategory(suffixT2Props.get(WORD_CATEGORY_PROP))
            );
        } else if (classificationProps.containsKey(WORD_CATEGORY_PROP)) {
            lex.setCategory(
                    JSpellMapper.mapCategory(classificationProps.get(WORD_CATEGORY_PROP))
            );
        } else {
            lex.setCategory(Category.UNKNOWN);
        }

        lex.setPrefixProps(prefixProps);
        lex.setSuffixT1Props(suffixT1Props);
        lex.setSuffixT2Props(suffixT2Props);

        return lex;
    }

    private Map<String, String> extractKeyValueData(String s) {

        Map<String, String> data = new HashMap<>();

        if (s.isEmpty())
            return data;

        // hack to deal with dictionary problem that has multiple emotions per lemma
        boolean extractingEmotion = false;

        String[] pairs = s.split(",");
        for (String pair: pairs) {
            String[] keyValue = pair.split("=");

            if (keyValue[0].toLowerCase().startsWith("emo")) {

                if (extractingEmotion) {

                    if (!(keyValue[0].equalsIgnoreCase(WORD_INTERMEDIATE_EMOTION_PROP) ||
                            keyValue[0].equalsIgnoreCase(WORD_SPECIFIC_EMOTION_PROP))) {
                        extractingEmotion = false;
                        continue;
                    }
                } else if (data.containsKey("EmoGlobal"))
                    continue;

                extractingEmotion = true;
            }

            data.put(keyValue[0], keyValue[1]);
        }

        return data;
    }
}
