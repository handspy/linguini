package pt.up.hs.linguini.test.unit.nndep;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import pt.up.hs.linguini.analysis.ideadensity.Relation;
import pt.up.hs.linguini.exceptions.LinguiniException;
import pt.up.hs.linguini.nndep.NNDepParser;
import pt.up.hs.linguini.pipeline.Step;
import pt.up.hs.linguini.pos.PoSTagger;
import pt.up.hs.linguini.tokenization.Tokenizer;

import java.util.List;
import java.util.Locale;

/**
 * Unit tests for neural network dependency parser.
 *
 * @author José Carlos Paiva <code>josepaiva94@gmail.com</code>
 */
public class TestNNDepParser {
    private static final String SENTENCE_1 = "Era uma vez um gato que dormia todo o dia.";
    private static final String SENTENCE_2 = "O José vai desistir de fazer desporto.";

    private static final Locale LOCALE = new Locale("pt", "PT");

    @Test
    public final void testExecuteSentence1() throws LinguiniException {

        Step<String, List<Relation>> pipeline =
                new Tokenizer(LOCALE, true)
                        .pipe(new PoSTagger(LOCALE))
                        .pipe(NNDepParser.getInstance(LOCALE));

        List<Relation> relations = pipeline.execute(SENTENCE_1);

        Assertions.assertEquals(
                new Relation(0, new int[] { 3 }, "TOP", "TOP", null),
                relations.get(0)
        );
        Assertions.assertEquals(
                new Relation(1, new int[] {}, 3, "cop", "AUX", "Era"),
                relations.get(1)
        );
        Assertions.assertEquals(
                new Relation(2, new int[] {}, 3, "det", "DET", "uma"),
                relations.get(2)
        );
        Assertions.assertEquals(
                new Relation(3, new int[] { 1, 2, 5, 11 }, 0, "root", "NOUN", "vez"),
                relations.get(3)
        );
        Assertions.assertEquals(
                new Relation(4, new int[] {}, 5, "nummod", "NUM", "um"),
                relations.get(4)
        );
        Assertions.assertEquals(
                new Relation(5, new int[] { 4, 7 }, 3, "nsubj", "NOUN", "gato"),
                relations.get(5)
        );
        Assertions.assertEquals(
                new Relation(6, new int[] {}, 7, "nsubj", "PRON", "que"),
                relations.get(6)
        );
        Assertions.assertEquals(
                new Relation(7, new int[] { 6, 10 }, 5, "acl:relcl", "VERB", "dormia"),
                relations.get(7)
        );
        Assertions.assertEquals(
                new Relation(8, new int[] {},  10, "det", "DET", "todo"),
                relations.get(8)
        );
        Assertions.assertEquals(
                new Relation(9, new int[] {}, 10, "det", "DET", "o"),
                relations.get(9)
        );
        Assertions.assertEquals(
                new Relation(10, new int[] { 8, 9 }, 7, "obj", "NOUN", "dia"),
                relations.get(10)
        );
        Assertions.assertEquals(
                new Relation(11, new int[] {}, 3, "punct", "PUNCT", "."),
                relations.get(11)
        );
    }

    @Test
    public final void testExecuteSentence2() throws LinguiniException {

        Step<String, List<Relation>> pipeline =
                new Tokenizer(LOCALE, true)
                        .pipe(new PoSTagger(LOCALE))
                        .pipe(NNDepParser.getInstance(LOCALE));

        List<Relation> relations = pipeline.execute(SENTENCE_2);

        Assertions.assertEquals(
                new Relation(0, new int[] { 4 }, "TOP", "TOP", null),
                relations.get(0)
        );
        Assertions.assertEquals(
                new Relation(1, new int[] {}, 2, "det", "DET", "O"),
                relations.get(1)
        );
        Assertions.assertEquals(
                new Relation(2, new int[] { 1 }, 4, "nsubj", "PROPN", "José"),
                relations.get(2)
        );
        Assertions.assertEquals(
                new Relation(3, new int[] {}, 4, "aux", "AUX", "vai"),
                relations.get(3)
        );
        Assertions.assertEquals(
                new Relation(4, new int[] { 2, 3, 6, 8 }, 0, "root", "VERB", "desistir"),
                relations.get(4)
        );
        Assertions.assertEquals(
                new Relation(5, new int[] {}, 6, "mark", "SCONJ", "de"),
                relations.get(5)
        );
        Assertions.assertEquals(
                new Relation(6, new int[] { 5, 7 }, 4, "xcomp", "VERB", "fazer"),
                relations.get(6)
        );
        Assertions.assertEquals(
                new Relation(7, new int[] {}, 6, "obj", "NOUN", "desporto"),
                relations.get(7)
        );
        Assertions.assertEquals(
                new Relation(8, new int[] {}, 4, "punct", "PUNCT", "."),
                relations.get(8)
        );
    }
}
