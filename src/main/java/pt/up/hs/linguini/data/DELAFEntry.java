package pt.up.hs.linguini.data;

import java.util.Objects;

public class DELAFEntry {
    private String word;
    private String lemma;
    private String pos;
    private String subcategory;
    private String morphAttributes;

    public DELAFEntry(
            String word, String lemma,
            String pos, String subcategory,
            String morphAttributes) {
        this.word = word;
        this.lemma = lemma;
        this.pos = pos;
        this.subcategory = subcategory;
        this.morphAttributes = morphAttributes;
    }

    public String getWord() {
        return word;
    }

    public void setWord(String word) {
        this.word = word;
    }

    public String getLemma() {
        return lemma;
    }

    public void setLemma(String lemma) {
        this.lemma = lemma;
    }

    public String getPos() {
        return pos;
    }

    public void setPos(String pos) {
        this.pos = pos;
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
        if (o == null || getClass() != o.getClass()) return false;
        DELAFEntry that = (DELAFEntry) o;
        return Objects.equals(getWord(), that.getWord()) &&
                Objects.equals(getLemma(), that.getLemma()) &&
                Objects.equals(getPos(), that.getPos()) &&
                Objects.equals(getSubcategory(), that.getSubcategory()) &&
                Objects.equals(getMorphAttributes(), that.getMorphAttributes());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getWord(), getLemma(), getPos(), getSubcategory(), getMorphAttributes());
    }
}
