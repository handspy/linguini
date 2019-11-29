package pt.up.hs.linguini.test.unit.transformers;

import org.junit.jupiter.api.*;
import org.junit.platform.runner.JUnitPlatform;
import org.junit.runner.RunWith;
import pt.up.hs.linguini.jspell.JSpellWordAnnotator;
import pt.up.hs.linguini.models.Token;
import pt.up.hs.linguini.exceptions.TransformerException;
import pt.up.hs.linguini.transformers.LemmaTokenTransformer;
import pt.up.hs.linguini.transformers.TokenTransformer;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Locale;

/**
 * Unit tests for lemma token transformer.
 *
 * @author José Carlos Paiva <code>josepaiva94@gmail.com</code>
 */
@RunWith(JUnitPlatform.class)
public class TestLemmaTokenTransformer {

    private JSpellWordAnnotator wordAnnotator;
    private TokenTransformer transformer;

    public TestLemmaTokenTransformer() {
        super();
    }

    @BeforeEach
    public void setup() {
        try {
            wordAnnotator = new JSpellWordAnnotator(new Locale("pt", "PT"));
            transformer = new LemmaTokenTransformer(wordAnnotator);
        } catch (IOException | URISyntaxException e) {
            Assertions.fail("Failed to initialize transformer", e);
        }
    }

    @AfterEach
    public void tearDown() throws IOException {
        wordAnnotator.close();
    }

    @Test
    public final void testTransform() {
        try {
            Assertions.assertEquals("rir",
                    transformer.transform(new Token(0,"riu")).getWord());
            Assertions.assertEquals("rir",
                    transformer.transform(new Token(0,"rir")).getWord());
            Assertions.assertEquals("corar",
                    transformer.transform(new Token(0,"corou")).getWord());
            Assertions.assertEquals("oceano",
                    transformer.transform(new Token(0,"Oceano")).getWord());
            Assertions.assertEquals("bonito",
                    transformer.transform(new Token(0,"bonita")).getWord());
            Assertions.assertEquals("engraçar",
                    transformer.transform(new Token(0,"engraçadíssimo")).getWord());
            Assertions.assertEquals("error",
                    transformer.transform(new Token(0,"error")).getWord());
        } catch (TransformerException e) {
            Assertions.fail("Failed to transform token", e);
        }
    }
}
