package pt.up.hs.linguini.jspell;

import pt.up.hs.linguini.models.Category;
import pt.up.hs.linguini.models.Emotion;

import java.util.HashMap;
import java.util.Map;

/**
 * Lex representation from JSpell
 *
 * @author José Carlos Paiva <code>josepaiva94@gmail.com</code>
 */
public class JSpellLex {
    private String lemma;

    private String category;

    private Map<String, String> classificationProps;
    private Map<String, String> prefixProps;
    private Map<String, String> suffixT1Props;
    private Map<String, String> suffixT2Props;

    private Emotion emotion;

    public JSpellLex() {
        this.classificationProps = new HashMap<>();
        this.prefixProps = new HashMap<>();
        this.suffixT1Props = new HashMap<>();
        this.suffixT2Props = new HashMap<>();
    }

    public String getLemma() {
        return lemma;
    }

    public void setLemma(String lemma) {
        this.lemma = lemma;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public Map<String, String> getClassificationProps() {
        return classificationProps;
    }

    public void setClassificationProps(Map<String, String> classificationProps) {
        this.classificationProps = classificationProps;
    }

    public Map<String, String> getPrefixProps() {
        return prefixProps;
    }

    public void setPrefixProps(Map<String, String> prefixProps) {
        this.prefixProps = prefixProps;
    }

    public Map<String, String> getSuffixT1Props() {
        return suffixT1Props;
    }

    public void setSuffixT1Props(Map<String, String> suffixT1Props) {
        this.suffixT1Props = suffixT1Props;
    }

    public Map<String, String> getSuffixT2Props() {
        return suffixT2Props;
    }

    public void setSuffixT2Props(Map<String, String> suffixT2Props) {
        this.suffixT2Props = suffixT2Props;
    }

    public Emotion getEmotion() {
        return emotion;
    }

    public void setEmotion(Emotion emotion) {
        this.emotion = emotion;
    }

    @Override
    public String toString() {
        return "Lex { " +
                "lemma='" + lemma + '\'' +
                ", category=" + category +
                ", emotion=" + emotion +
                " }";
    }
}
