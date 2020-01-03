package pt.up.hs.linguini.analysis.ideadensity;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * Represents a relation in the dependency tree.
 *
 * @author José Carlos Paiva <code>josepaiva94@gmail.com</code>
 */
public class Relation {

    // the index of the word in the sentence, starting at 1 (0 in case of the
    // ROOT relation).
    private int address;
    // an array containing the indexes of the words whose root is the
    // corresponding word.
    private int[] deps;
    // the head of this relation (null, in case of ROOT)
    private Integer head;
    // the relation label (e.g., nsubj, det. 'TOP' in case of ROOT; 'null' or
    // 'root' in case of element pointed to by ROOT).
    private String rel;
    // PoS-tag of the corresponding word ('TOP' in case of ROOT).
    private String tag;
    // the corresponding word (null in case of ROOT).
    private String word;

    private boolean processed = false;

    public Relation(
            int address, int[] deps, String rel,
            String tag, String word) {
        this.address = address;
        this.deps = deps;
        this.rel = rel;
        this.tag = tag;
        this.word = word;
    }

    public Relation(
            int address, int[] deps, int head, String rel,
            String tag, String word) {
        this.address = address;
        this.deps = deps;
        this.head = head;
        this.rel = rel;
        this.tag = tag;
        this.word = word;
    }

    public int address() {
        return address;
    }

    public void address(int address) {
        this.address = address;
    }

    public int[] deps() {
        return deps;
    }

    public void deps(int[] deps) {
        this.deps = deps;
    }

    public Integer head() {
        return head;
    }

    public void head(Integer head) {
        this.head = head;
    }

    public String rel() {
        return rel;
    }

    public void rel(String rel) {
        this.rel = rel;
    }

    public String tag() {
        return tag;
    }

    public void tag(String tag) {
        this.tag = tag;
    }

    public String word() {
        return word;
    }

    public void word(String word) {
        this.word = word;
    }

    public boolean isProcessed() {
        return processed;
    }

    public void setProcessed(boolean processed) {
        this.processed = processed;
    }

    /**
     * Check whether the corresponding word has children in the tree connected
     * to it through a relation of a certain label, returning the indices of
     * the other relations if they exist, and an empty list otherwise.
     *
     * @param rel {@link String} the label of the potential child relation
     * @param relations {@link List} all the relations of the sentence
     * @param index {@code int} index of the current relation
     * @return {@link int[]} a list containing the indices of a relation
     *                       connected to this one through the 'dep' label if
     *                       they exist, and an empty list otherwise.
     */
    public static int[] getChildrenWithDep(
            String rel, List<Relation> relations, int index) {

        return Arrays.stream(relations.get(index).deps())
                .filter(i -> relations.get(i).rel.equals(rel) &&
                        relations.get(i).head == index)
                .toArray();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Relation)) return false;
        Relation relation = (Relation) o;
        return address == relation.address &&
                processed == relation.processed &&
                Arrays.equals(deps, relation.deps) &&
                Objects.equals(head, relation.head) &&
                Objects.equals(rel, relation.rel) &&
                Objects.equals(tag, relation.tag) &&
                Objects.equals(word, relation.word);
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(address, head, rel, tag, word, processed);
        result = 31 * result + Arrays.hashCode(deps);
        return result;
    }

    @Override
    public String toString() {
        return "{" +
                "address: " + address +
                ", deps: " + Arrays.toString(deps) +
                ", head: " + head +
                ", rel: '" + rel + '\'' +
                ", tag: '" + tag + '\'' +
                ", word: '" + word + '\'' +
                '}';
    }
}
