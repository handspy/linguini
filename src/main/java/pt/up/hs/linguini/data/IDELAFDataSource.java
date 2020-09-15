package pt.up.hs.linguini.data;

import java.util.Collection;

public interface IDELAFDataSource {

    void add(String word, String lemma, String pos, String subcategory, String morphAttributes);

    Collection<DELAFEntry> remove(String word);

    Collection<DELAFEntry> remove(String word, String pos);

    Collection<DELAFEntry> getAllEntries();

    Collection<DELAFEntry> getEntries(String word);

    boolean contains(String word);

    boolean contains(String word, String posTag);

    Collection<String> getLemmas(String word, String posTag);

    int size();
}
