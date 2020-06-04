package pt.up.hs.linguini.models;

import java.util.Objects;

/**
 * Representation of an emotion.
 *
 * @author José Carlos Paiva <code>josepaiva94@gmail.com</code>
 */
public class Emotion {

    private Global global;
    private Intermediate intermediate;
    private Specific specific;
    private Polarity  polarity;

    public Emotion() {
    }

    public Emotion(Polarity polarity, Global global, Intermediate intermediate, Specific specific) {
        this.polarity = polarity;
        this.global = global;
        this.intermediate = intermediate;
        this.specific = specific;
    }

    public Global getGlobal() {
        return global;
    }

    public void setGlobal(Global global) {
        this.global = global;
    }

    public Intermediate getIntermediate() {
        return intermediate;
    }

    public void setIntermediate(Intermediate intermediate) {
        this.intermediate = intermediate;
    }

    public Specific getSpecific() {
        return specific;
    }

    public void setSpecific(Specific specific) {
        this.specific = specific;
    }

    public Polarity getPolarity() {
        return polarity;
    }

    public void setPolarity(Polarity polarity) {
        this.polarity = polarity;
    }

    public enum Polarity {
        POSITIVE,
        NEGATIVE
    }

    public enum Global {
        BENEVOLENCE,
        MALEVOLENCE,

        COMFORT,
        DISCOMFORT,

        SAFETY,
        ANXIETY,

        SURPRISE,
        INDIFFERENCE,

        NON_SPECIFIC
    }

    public enum Intermediate {

        // benevolence
        AFFECTION,
        KINDNESS,

        // malevolence
        HATE,
        AGGRESSIVENESS,

        // comfort
        HAPPINESS,
        LUCIDITY,
        ENTHUSIASM,
        RELIEF,
        SATISFACTION,

        // discomfort
        SUFFERING,
        MADNESS,
        DEPRESSION,
        DISTURBANCE,
        DISSATISFACTION,

        // safety
        COURAGE,
        CALM,

        // anxiety
        FEAR,
        TENSION
    }

    public enum Specific {

        // benevolence > affection
        LOVE,
        DESIRE,
        ADMIRATION,
        ATTRACTION,

        // benevolence > kindness
        GOODNESS,
        SWEETNESS,
        PATIENCE,
        HUMILITY,

        // malevolence > hate
        RESENTMENT,
        DISGUST,
        CONTEMPT,
        IRRITATION,

        // malevolence > aggressiveness
        CRUELTY,
        RAGE,
        IRE,
        ARROGANCE,

        // comfort > happiness
        HAPPY,
        PLEASURE,
        LAUGH,

        // comfort > lucidity
        MENTAL_HEALTH,
        BALANCE,

        // comfort > enthusiasm
        JOY,
        VIVACITY,
        ALERT,

        // comfort > relief
        APPEASE,
        FREEDOM,

        // comfort > satisfaction
        ESTEEM,
        SATIETY,
        LIKE,

        // discomfort > suffering
        DRAMA,
        PAIN,
        CRY,

        // discomfort > madness
        MENTAL_DISEASE,
        IMBALANCE,

        // discomfort > depression
        SADNESS,
        FATIGUE,
        APATHY,

        // discomfort > disturbance
        AGITATION,
        REMORSE,

        // discomfort > dissatisfaction
        HUMILIATION,
        FRUSTRATION,
        DISLIKE,

        // safety > courage
        AUDACITY,
        EXTROVERSION,

        // safety > calm
        TRANQUILITY,
        RELAXATION,

        // anxiety > fear
        DREAD,
        INTROVERSION,

        // anxiety > tension
        ANGUISH,
        CONCERN;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Emotion emotion = (Emotion) o;
        return getGlobal() == emotion.getGlobal() &&
                getIntermediate() == emotion.getIntermediate() &&
                getSpecific() == emotion.getSpecific() &&
                getPolarity() == emotion.getPolarity();
    }

    @Override
    public int hashCode() {
        return Objects.hash(getGlobal(), getIntermediate(), getSpecific(), getPolarity());
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        if (polarity != null) {
            sb.append(polarity.equals(Polarity.POSITIVE) ? "(+)" : "(-)").append(' ');
        }
        if (global != null) {
            sb.append(global.toString());

            if (intermediate != null) {
                sb.append(" > ").append(intermediate.toString());

                if (specific != null) {
                    sb.append(" > ").append(specific.toString());
                }
            }
        } else {
            return "UNKNOWN";
        }
        return sb.toString();
    }
}
