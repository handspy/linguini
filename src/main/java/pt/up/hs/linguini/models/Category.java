package pt.up.hs.linguini.models;

/**
 * Category of the word. All words belong to categories (or parts of speech)
 * according to the part they play in a sentence.
 *
 * @author José Carlos Paiva <code>josepaiva94@gmail.com</code>
 */
public enum Category {

    VERB,
    PREPOSITION,
    PERSONAL_PRONOUN,
    DEMONSTRATIVE_PRONOUN,
    POSSESSIVE_PRONOUN,
    INDEFINITE_PRONOUN,
    RELATIVE_PRONOUN,
    INTERROGATIVE_PRONOUN,
    CONJUNCTION,
    INTERJECTION,
    NOUN,
    COMMON_NOUN,
    PROPER_NAME,
    APASSIVATING_PARTICLE,
    ARTICLE,
    CONTRACTION,
    CARDINAL,
    ORDINAL,
    ADJECTIVE,
    ADVERB,
    UNKNOWN;

    @Override
    public String toString() {

        String wordClass = super.toString()
                .replace('_', ' ')
                .toLowerCase();

        return wordClass.substring(0,1).toUpperCase() +
                wordClass.substring(1).toLowerCase();
    }
}
