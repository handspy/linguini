package pt.up.hs.linguini.dictionaries;

public interface IDictionarySearch {

    boolean contains(String inflectedForm);

    boolean contains(String inflectedForm, String posTag);

    DictionaryEntry[] getAllEntries();

    DictionaryEntry[] getEntries(String inflectedForm);

    String[] getLemmas(String inflectedForm, String posTag);
}
