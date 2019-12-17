package pt.up.hs.linguini.dictionaries;

import java.util.Objects;

/**
 * Entry of the dictionary.
 *
 * @author José Carlos Paiva <code>josepaiva94@gmail.com</code>
 */
public class DictionaryEntry {
    private String inflectedForm;
    private String lemma;
    private String partOfSpeech;
    private String subcategory;
    private String morphAttributes;

    public DictionaryEntry(
            String inflectedForm, String lemma,
            String partOfSpeech, String subcategory,
            String morphAttributes) {
        this.inflectedForm = inflectedForm;
        this.lemma = lemma;
        this.partOfSpeech = partOfSpeech;
        this.subcategory = subcategory;
        this.morphAttributes = morphAttributes;
    }

    public String getInflectedForm() {
        return inflectedForm;
    }

    public void setInflectedForm(String inflectedForm) {
        this.inflectedForm = inflectedForm;
    }

    public String getLemma() {
        return lemma;
    }

    public void setLemma(String lemma) {
        this.lemma = lemma;
    }

    public String getPartOfSpeech() {
        return partOfSpeech;
    }

    public void setPartOfSpeech(String partOfSpeech) {
        this.partOfSpeech = partOfSpeech;
    }

    public String getSubcategory() {
        return subcategory;
    }

    public void setSubcategory(String subcategory) {
        this.subcategory = subcategory;
    }

    public String getMorphAttributes() {
        return morphAttributes;
    }

    public void setMorphAttributes(String morphAttributes) {
        this.morphAttributes = morphAttributes;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof DictionaryEntry)) return false;
        DictionaryEntry that = (DictionaryEntry) o;
        return Objects.equals(inflectedForm, that.inflectedForm) &&
                Objects.equals(lemma, that.lemma) &&
                Objects.equals(partOfSpeech, that.partOfSpeech) &&
                Objects.equals(subcategory, that.subcategory) &&
                Objects.equals(morphAttributes, that.morphAttributes);
    }

    @Override
    public int hashCode() {
        return Objects.hash(inflectedForm, lemma, partOfSpeech, subcategory, morphAttributes);
    }
}
