package pt.up.hs.linguini.nndep;

import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.ling.IndexedWord;
import edu.stanford.nlp.parser.nndep.DependencyParser;
import edu.stanford.nlp.trees.GrammaticalStructure;
import edu.stanford.nlp.trees.TypedDependency;

import pt.up.hs.linguini.analysis.ideadensity.Relation;
import pt.up.hs.linguini.exceptions.LinguiniException;
import pt.up.hs.linguini.models.AnnotatedToken;
import pt.up.hs.linguini.pipeline.Step;

import java.util.*;
import java.util.stream.Collectors;

/**
 * A dependency parser analyzes the grammatical structure of a sentence,
 * establishing relationships between "head" words and words which modify those
 * heads.
 *
 * @author José Carlos Paiva <code>josepaiva94@gmail.com</code>
 */
public class NNDepParser implements Step<List<AnnotatedToken<String>>, List<Relation>> {
    private static final String FILE_PATH_FORMAT =
            "%s/models/%s.depparser.txt.gz";

    /*private static Map<Locale, NNDepParser> nnDepParsers = new HashMap<>();*/

    private Locale locale;

    private DependencyParser depParser;

    private NNDepParser(Locale locale) {
        this.locale = locale;
        this.depParser = DependencyParser.loadFromModelFile(
                String.format(
                        FILE_PATH_FORMAT,
                        locale.toString(),
                        locale.toString()
                )
        );
    }

    public static NNDepParser getInstance() {
        return getInstance(Locale.getDefault());
    }

    public static NNDepParser getInstance(Locale locale) {
        /*if (nnDepParsers.containsKey(locale)) {
            return nnDepParsers.get(locale);
        }*/
        NNDepParser nnDepParser = new NNDepParser(locale);
        /*nnDepParsers.put(locale, nnDepParser);*/
        return nnDepParser;
    }

    @Override
    public List<Relation> execute(List<AnnotatedToken<String>> sentence)
            throws LinguiniException {

        GrammaticalStructure gs = depParser
                .predict(sentence.parallelStream()
                .map(w -> {
                    CoreLabel lbl = CoreLabel.wordFromString(w.word());
                    lbl.setTag(w.getInfo());
                    return lbl;
                })
                .collect(Collectors.toList()));

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

        return relations;
    }
}
