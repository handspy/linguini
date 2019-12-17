package pt.up.hs.linguini.models;

/**
 * Category of the word. All words belong to categories (or parts of speech)
 * according to the part they play in a sentence.
 *
 * @author José Carlos Paiva <code>josepaiva94@gmail.com</code>
 */
public enum Category {
    ADJECTIVE,
    PREPOSITION,
    ADVERB,
    AUXILIAR_VERB,
    DETERMINER,
    COORDINATING_CONJUNCTION,
    INTERJECTION,
    NOUN,
    NUMERAL,
    PARTICLE,
    PRONOUN,
    PROPER_NOUN,
    PUNCTUATION,
    SUBORDINATING_CONJUNCTION,
    SYMBOL,
    VERB,
    OTHER;

    @Override
    public String toString() {

        String wordClass = super.toString()
                .replace('_', ' ')
                .toLowerCase();

        return wordClass.substring(0,1).toUpperCase() +
                wordClass.substring(1).toLowerCase();
    }
}
