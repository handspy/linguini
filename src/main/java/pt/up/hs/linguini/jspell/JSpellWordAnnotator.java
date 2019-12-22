package pt.up.hs.linguini.jspell;

import pt.up.hs.linguini.jspell.exceptions.JSpellAnnotatorException;
import pt.up.hs.linguini.models.Token;
import pt.up.hs.linguini.models.*;
import pt.up.hs.linguini.caching.InMemoryCache;
import pt.up.hs.linguini.pipeline.Step;
import pt.up.hs.linguini.utils.StringUtils;

import java.io.IOException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Word annotator using JSpell.
 *
 * @author José Carlos Paiva <code>josepaiva94@gmail.com</code>
 */
public class JSpellWordAnnotator implements
        Step<List<Token>, List<AnnotatedToken<JSpellInfo>>> {

    private static final Pattern HEADER_PATTERN =
            Pattern.compile("^([*&])\\s([^\\s]+)\\s([^\\s]+)\\s:");
    private static final Pattern LEX_PATTERN =
            Pattern.compile("lex\\((.[^,\\s]*),\\s*\\[([^]]*)],\\s*\\[([^]]*)],\\s*\\[([^]]*)],\\s*\\[([^]]*)]\\)");
    private static final Pattern ERROR_CORRECTIONS_PATTERN =
            Pattern.compile("([^=]+)=\\s+lex\\((.[^,\\s]+),\\s*\\[([^]]*)],\\s*\\[([^]]*)],\\s*\\[([^]]*)],\\s*\\[([^]]*)]\\)[,;]?\\s?");

    private static InMemoryCache<String, JSpellInfo> cache =
            new InMemoryCache<>(3600, 600, 500);

    private static final String WORD_CATEGORY_PROP = "CAT";
    private static final String WORD_GLOBAL_EMOTION_PROP = "EmoGlobal";
    private static final String WORD_INTERMEDIATE_EMOTION_PROP = "EmoIntermediate";
    private static final String WORD_SPECIFIC_EMOTION_PROP = "EmoSpecific";

    private final JSpellWrapper jSpell;

    public JSpellWordAnnotator(Locale locale) {
        this.jSpell = new JSpellWrapper(locale);
    }

    @Override
    public List<AnnotatedToken<JSpellInfo>> execute(List<Token> tokens)
            throws JSpellAnnotatorException {

        try {
            jSpell.start();
        } catch (IOException e) {
            throw new JSpellAnnotatorException(
                    "JSpell annotator's process could not be started", e);
        }

        List<AnnotatedToken<JSpellInfo>> annotatedTokens = new ArrayList<>();

        for (Token token: tokens) {

            if (StringUtils.isBlankString(token.getWord())) {
                annotatedTokens.add(new AnnotatedToken<>(token, null));
            }

            JSpellInfo info = cache.get(token.getWord());
            if (info == null) {
                synchronized (jSpell) {
                    String output;
                    try {
                        output = jSpell.process(token.getWord());
                    } catch (IOException e) {
                        throw new JSpellAnnotatorException(
                                "Failed to annotate token with JSpell", e);
                    }
                    info = processJSpellOutput(output);
                    cache.put(token.getWord(), info);
                }
            }

            annotatedTokens.add(new AnnotatedToken<>(token, info));
        }

        try {
            jSpell.close();
        } catch (IOException e) {
            throw new JSpellAnnotatorException(
                    "JSpell annotator's process could not be stopped", e);
        }

        return annotatedTokens;
    }

    private JSpellInfo processJSpellOutput(String output) {

        output = output.trim();

        Matcher m = HEADER_PATTERN.matcher(output);

        if (m.find()) { // normal word or spelling error

            String matching = m.group(0);
            String symbol = m.group(1);

            if (symbol.equals("*")) { // normal word
                return new JSpellInfo(processLexes(
                        output.substring(matching.length())
                ));
            } else { // spelling error
                return new JSpellInfo(processSuggestedCorrections(
                        output.substring(matching.length())
                ));
            }
        } else { // punctuation
            return new JSpellInfo();
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

    private JSpellLex buildLex(
            String lemma,
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
            lex.setCategory(prefixProps.get(WORD_CATEGORY_PROP));
        } else if (suffixT1Props.containsKey(WORD_CATEGORY_PROP)) {
            lex.setCategory(suffixT1Props.get(WORD_CATEGORY_PROP));
        } else if (suffixT2Props.containsKey(WORD_CATEGORY_PROP)) {
            lex.setCategory(suffixT2Props.get(WORD_CATEGORY_PROP));
        } else lex.setCategory(classificationProps.getOrDefault(WORD_CATEGORY_PROP, "_"));

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
                } else if (data.containsKey(WORD_GLOBAL_EMOTION_PROP))
                    continue;

                extractingEmotion = true;
            }

            data.put(keyValue[0], keyValue[1]);
        }

        return data;
    }
}
