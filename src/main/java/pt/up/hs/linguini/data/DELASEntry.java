package pt.up.hs.linguini.data;

import java.util.Objects;

public class DELASEntry {

    private String lemma;
    private String pos;

    public DELASEntry(String lemma, String pos) {
        this.lemma = lemma;
        this.pos = pos;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DELASEntry that = (DELASEntry) o;
        return Objects.equals(getLemma(), that.getLemma()) &&
                Objects.equals(getPos(), that.getPos());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getLemma(), getPos());
    }
}
