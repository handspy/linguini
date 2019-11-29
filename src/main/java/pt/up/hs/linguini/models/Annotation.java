package pt.up.hs.linguini.models;

/**
 * Generic annotation in a text.
 *
 * @author José Carlos Paiva <code>josepaiva94@gmail.com</code>
 */
public class Annotation<T> {
    private int start;
    private int length;

    private T annot;

    public Annotation() {
    }

    public Annotation(int start, int length, T annot) {
        this.start = start;
        this.length = length;
        this.annot = annot;
    }

    public int getStart() {
        return start;
    }

    public void setStart(int start) {
        this.start = start;
    }

    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }

    public T getAnnot() {
        return annot;
    }

    public void setAnnot(T annot) {
        this.annot = annot;
    }
}
