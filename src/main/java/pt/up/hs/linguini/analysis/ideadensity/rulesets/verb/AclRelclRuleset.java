package pt.up.hs.linguini.analysis.ideadensity.rulesets.verb;

/**
 * A clausal modifier of noun (acl) is either an infinitive clause, a
 * participial clause, or a clausal complement that modifies the head of a noun
 * phrase.
 *
 * @author José Carlos Paiva <code>josepaiva94@gmail.com</code>
 */
public class AclRelclRuleset extends RcmodRuleset {

    public AclRelclRuleset() {
        super("acl:relcl");
    }
}
