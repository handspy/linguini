package pt.up.hs.linguini;

import pt.up.hs.linguini.analysis.EmotionalAnalysis;
import pt.up.hs.linguini.analysis.LexicalDiversityAnalysis;
import pt.up.hs.linguini.analysis.SimpleTextAnalysis;
import pt.up.hs.linguini.exceptions.AnalyzerException;
import pt.up.hs.linguini.models.AnnotatedToken;
import pt.up.hs.linguini.models.Emotion;
import pt.up.hs.linguini.models.TextSummary;
import pt.up.hs.linguini.models.Token;
import pt.up.hs.linguini.processing.TextTokenizer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

/**
 * Text analyzer.
 *
 * @author José Carlos Paiva <code>josepaiva94@gmail.com</code>
 */
public class TextAnalyzer {

    private Locale locale;
    private List<Token> tokens;

    private TextAnalyzer(Locale locale, List<Token> tokens) {
        this.locale = locale;
        this.tokens = tokens;
    }

    public static TextAnalyzer fromText(String text) {
        return TextAnalyzer.fromText(text, Locale.ENGLISH);
    }

    public static TextAnalyzer fromText(String text, Locale locale) {
        TextTokenizer tokenizer = new TextTokenizer(text);
        List<Token> tokens = tokenizer.collectAll(true, true);
        return new TextAnalyzer(locale, tokens);
    }

    public TextSummary summarize() throws AnalyzerException {

        SimpleTextAnalysis analysis = new SimpleTextAnalysis(locale);

        return analysis
                .preprocess(new ArrayList<>(tokens))
                .execute()
                .getResult();
    }

    public List<AnnotatedToken<Emotion>> analyzeEmotions() throws AnalyzerException {

        EmotionalAnalysis analysis = new EmotionalAnalysis(locale);

        return analysis
                .preprocess(new ArrayList<>(tokens))
                .execute()
                .getResult();
    }

    public Double analyzeLexicalDiversity(
            LexicalDiversityAnalysis.Algorithm algorithm,
            boolean lemmatize, Double mtldThreshold,
            Integer hddSampleSize) throws AnalyzerException {

        LexicalDiversityAnalysis analysis;
        switch (algorithm) {
            case MTLD:
                analysis = new LexicalDiversityAnalysis(
                        locale, algorithm, lemmatize, mtldThreshold);
                break;
            case HDD:
                analysis = new LexicalDiversityAnalysis(
                        locale, algorithm, lemmatize, hddSampleSize);
                break;
            default:
                throw new AnalyzerException("Unknown lexical diversity algorithm.");
        }

        return analysis
                .preprocess(new ArrayList<>(tokens))
                .execute()
                .getResult();
    }
}
