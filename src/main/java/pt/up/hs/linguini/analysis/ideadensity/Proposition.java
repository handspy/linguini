package pt.up.hs.linguini.analysis.ideadensity;

import pt.up.hs.linguini.analysis.ideadensity.utils.Tuple;

/**
 * Represents a proposition, with its content and kind.
 *
 * @author José Carlos Paiva <code>josepaiva94@gmail.com</code>
 */
public class Proposition {

    // the content of the proposition. E.g.: (ran, the cat)
    private Tuple content;
    // the kind of the proposition. E.g., P (predication), M (modification),
    // C (connection), and so on
    private String kind;

    public Proposition(Tuple content, String kind) {
        this.content = content;
        this.kind = kind;
    }

    public Tuple getContent() {
        return content;
    }

    public void setContent(Tuple content) {
        this.content = content;
    }

    public String getKind() {
        return kind;
    }

    public void setKind(String kind) {
        this.kind = kind;
    }

    @Override
    public String toString() {
        return "Proposition{" +
                "content=" + content +
                ", kind='" + kind + '\'' +
                '}';
    }
}
