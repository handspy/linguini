package pt.up.hs.linguini.dictionaries;

public interface ILexiconSearch {

    boolean contains(String lemma);

    boolean contains(String lemma, String posTag);

    String[] retrieveLemmas(String posTag);

    String[] retrievePoSTags(String lemma);

    int size();
}
