package pt.up.hs.linguini.data;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;

public interface IDELASDataSource {

    void add(String lemma, String posTag);

    HashSet<DELASEntry> remove(String lemma);

    List<DELASEntry> remove(String lemma, String posTag);

    Collection<DELASEntry> getAllEntries();

    Collection<DELASEntry> getEntries(String lemma);

    boolean contains(String lemma);

    boolean contains(String lemma, String posTag);

    int size();
}
