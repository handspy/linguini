package pt.up.hs.linguini.test.unit.nndep;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.platform.runner.JUnitPlatform;
import org.junit.runner.RunWith;
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
@RunWith(JUnitPlatform.class)
public class TestNNDepParser {
    private static final String SENTENCE_1 = "Era uma vez um gato, que dormia todo o dia.";
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
                new Relation(3, new int[] { 1, 2, 5, 6, 11, 12 }, 0, "root", "NOUN", "vez"),
                relations.get(3)
        );
        Assertions.assertEquals(
                new Relation(4, new int[] {}, 5, "det", "NUM", "um"),
                relations.get(4)
        );
        Assertions.assertEquals(
                new Relation(5, new int[] { 4 }, 3, "nsubj", "NOUN", "gato"),
                relations.get(5)
        );
        Assertions.assertEquals(
                new Relation(6, new int[] {}, 3, "punct", "PUNCT", ","),
                relations.get(6)
        );
        Assertions.assertEquals(
                new Relation(7, new int[] {}, 11, "case", "PRON", "que"),
                relations.get(7)
        );
        Assertions.assertEquals(
                new Relation(8, new int[] {}, 11, "nummod", "VERB", "dormia"),
                relations.get(8)
        );
        Assertions.assertEquals(
                new Relation(9, new int[] { 10 }, 11, "det", "DET", "todo"),
                relations.get(9)
        );
        Assertions.assertEquals(
                new Relation(10, new int[] {}, 9, "fixed", "DET", "o"),
                relations.get(10)
        );
        Assertions.assertEquals(
                new Relation(11, new int[] { 7, 8, 9 }, 3, "obl", "NOUN", "dia"),
                relations.get(11)
        );
        Assertions.assertEquals(
                new Relation(12, new int[] {}, 3, "punct", "PUNCT", "."),
                relations.get(12)
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

        /*Step<String, List<List<AnnotatedToken<String>>>> pipeline =
                new SentenceSplitter(LOCALE)
                    .pipe(new BatchStep<>(new Tokenizer(LOCALE, true)))
                    .pipe(new BatchStep<>(new PoSTagger(LOCALE)));

        List<List<AnnotatedToken<String>>> taggedTokens =
                pipeline.execute(SENTENCE_2);

        List<GrammaticalStructure> gss =
                new BatchStep<>(new NNDepParser(LOCALE))
                        .execute(taggedTokens);

        System.out.println("Nr. of grammatical structs " + gss.size());

        gss.parallelStream().forEach(gs -> {

            List<TypedDependency> tdl = new ArrayList<>(
                    gs.typedDependenciesCollapsed());
            Collections.sort(tdl);

            List<Relation> relations = new ArrayList<>();
            relations.add(new Relation(
                    0, new int[0], "TOP", "TOP", null));

            for (TypedDependency td: tdl) {
                int[] deps = tdl.parallelStream()
                        .filter(td2 ->
                                td2.gov() != null &&
                                        td2.gov().equals(td.dep())
                        )
                        .map(TypedDependency::dep)
                        .mapToInt(IndexedWord::index)
                        .toArray();

                int address = td.dep().index();
                String rel = td.reln().getShortName();

                if (rel.equalsIgnoreCase("root")) {
                    relations.get(0).deps(new int[] { address });
                }

                Relation relation = new Relation(
                        address,
                        deps,
                        td.gov() != null ? td.gov().index() : 0,
                        rel,
                        td.dep().tag(),
                        td.dep().word()
                );

                relations.add(relation);
            }

            for (Relation relation: relations) {
                System.out.println(relation.toString());
            }

            try {
                Engine engine = new Engine(new Locale("pt", "PT"));
                engine.analyze(
                        relations, 0, new int[0], new HashMap<>());
                for (Proposition proposition: engine.getProps()) {
                    System.out.println(proposition);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }*/

            /*List<Dependency<Label, Label, Object>> sortedDeps =
                    new ArrayList<>();
            for (TypedDependency dep : tdl) {
                String gov;
                if (dep.gov() == null || dep.gov().word() == null) {
                    gov = "";
                } else {
                    System.out.println("parent: " + dep.gov().toString());
                    gov = dep.gov().word();
                }
                String depStr;
                if (dep.dep() == null || dep.dep().word() == null) {
                    depStr = "";
                } else {
                    System.out.println("child: " + dep.dep().toString());
                    System.out.println("child tag: " + dep.dep().tag());
                    depStr = dep.dep().word();
                }

                NamedDependency nd = new NamedDependency(
                        gov,
                        depStr,
                        dep.reln().toString());
                sortedDeps.add(nd);*/

                /*int[] deps = sortedDeps.parallelStream()
                        .filter(snd -> snd.governor().value()
                                .equals(nd.name().toString()))
                        .mapToInt(snd -> wordIndices.get(snd.name().toString()))
                        .toArray();*/

                /*Relation relation = new Relation(
                        address, deps, nd.name().toString(), nd.);*/
                /*System.out.println(address++);*/
                /*System.out.println("rel: " + dep.reln().toString());
                System.out.println("deps: " + IntStream.of(deps)
                        .mapToObj(Integer::toString)
                        .collect(Collectors.joining()));
            }*/
            /*sortedDeps.sort(Dependencies.dependencyIndexComparator());

            IntStream
                    .range(0, sortedDeps.size())
                    .boxed()
                    .forEachOrdered(i -> {
                        System.out.println(i + " " + sortedDeps.get(i).dependent().value());
                    });*/
            /*Map<String, Integer> wordIndices = IntStream
                    .range(0, sortedDeps.size())
                    .boxed()
                    .collect(Collectors.toConcurrentMap(
                            i -> sortedDeps.get(i).governor().value(),
                            i -> i
                    ));*/

            // List<Relation> relations = new Relation[sortedDeps.size()];

            /*int address = 0;
            for (Dependency<Label, Label, Object> nd: sortedDeps) {

                int[] deps = sortedDeps.parallelStream()
                        .filter(snd -> snd.governor().value()
                                .equals(nd.name().toString()))
                        .mapToInt(snd -> wordIndices.get(snd.name().toString()))
                        .toArray();

                Relation relation = new Relation(
                        address, deps, nd.name().toString(), nd.);
                System.out.println(address++);
                System.out.println("parent: " + nd.governor().toString());
                System.out.println("child: " + nd.dependent().toString());
                System.out.println("rel: " + nd.name().toString());
                System.out.println("deps: " + IntStream.of(deps)
                        .mapToObj(Integer::toString)
                        .collect(Collectors.joining()));
                System.out.println("--------------------------------");
            }*/

            /*Collection<TypedDependency> tdl = gs.typedDependenciesCCprocessed();
            for (TypedDependency td : tdl) {
                String name = td.reln().getShortName();
                if (td.reln().getSpecific() != null)
                    name += "-" + td.reln().getSpecific();
                int gov = td.gov().index();
                int dep = td.dep().index();
                if (gov == dep) {
                    // System.err.println("same???");
                }
                if (gov < 1 || dep < 1) {
                    continue;
                }
                System.out.println(String.format("%d %d %s", gov - 1, dep - 1, name));
            }*/

            /*..constituents().parallelStream()
                    .forEachOrdered(c -> {
                        c.labels().stream().forEachOrdered(lbl -> {

                        });
                    });
            gs.typedDependencies().parallelStream()
                    .forEachOrdered(td -> System.out.println(td.toString()));
            gs.typedDependencies().parallelStream().forEachOrdered(td -> {
                System.out.println("All: " + td.reln()..gov().toString());
                System.out.println("PoS Tag: " + td.dep().tag());
                System.out.println("Relation: " + td.reln().getShortName());
                System.out.println(td.toString());
            });*/
        /*});*/
    }
}
