package pt.up.hs.linguini.jspell;

import java.util.List;
import java.util.Map;

/**
 * Information from JSpell related to a word.
 *
 * @author José Carlos Paiva <code>josepaiva94@gmail.com</code>
 */
public class JSpellInfo {

    private boolean error;
    private boolean punctuation;

    private List<JSpellLex> related;
    private Map<String, List<JSpellLex>> errorCorrections;

    public JSpellInfo() {
        this.error = false;
        this.punctuation = true;
    }

    public JSpellInfo(Map<String, List<JSpellLex>> errorCorrections) {
        this.error = true;
        this.punctuation = false;
        this.errorCorrections = errorCorrections;
    }

    public JSpellInfo(List<JSpellLex> related) {
        this.error = false;
        this.punctuation = false;
        this.related = related;
    }

    public boolean isError() {
        return error;
    }

    public boolean isPunctuation() {
        return punctuation;
    }

    public List<JSpellLex> getRelated() {
        return related;
    }

    public Map<String, List<JSpellLex>> getErrorCorrections() {
        return errorCorrections;
    }
}
