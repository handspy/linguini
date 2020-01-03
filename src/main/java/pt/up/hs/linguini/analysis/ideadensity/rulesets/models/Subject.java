package pt.up.hs.linguini.analysis.ideadensity.rulesets.models;

import java.util.Map;

/**
 * [Description here]
 *
 * @author José Carlos Paiva <code>josepaiva94@gmail.com</code>
 */
public class Subject {

    private String[] returnList;
    private int[] rcmodIds;
    private Map<String, Object> rcmodWdt;

    public Subject() {
    }

    public Subject(String[] returnList, Map<String, Object> rcmodWdt) {
        this.returnList = returnList;
        this.rcmodWdt = rcmodWdt;
    }

    public String[] getReturnList() {
        return returnList;
    }

    public void setReturnList(String[] returnList) {
        this.returnList = returnList;
    }

    public Map<String, Object> getRcmodWdt() {
        return rcmodWdt;
    }

    public void setRcmodWdt(Map<String, Object> rcmodWdt) {
        this.rcmodWdt = rcmodWdt;
    }

    public int[] getRcmodIds() {
        return rcmodIds;
    }

    public void setRcmodIds(int[] rcmodIds) {
        this.rcmodIds = rcmodIds;
    }
}
