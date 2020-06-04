package pt.up.hs.linguini.jspell;

import pt.up.hs.linguini.models.Emotion;

import static pt.up.hs.linguini.models.Emotion.Global.*;
import static pt.up.hs.linguini.models.Emotion.Intermediate.*;
import static pt.up.hs.linguini.models.Emotion.Specific.*;

/**
 * Maps JSpell values into EmoSpell values
 *
 * @author José Carlos Paiva <code>josepaiva94@gmail.com</code>
 */
public class JSpellMapper {

    public static Emotion mapEmotion(String global, String intermediate, String specific) {
        return new Emotion(
                null,
                mapGlobalEmotion(global),
                mapIntermediateEmotion(intermediate),
                mapSpecificEmotion(specific)
        );
    }

    public static Emotion.Global mapGlobalEmotion(String emotion) {

        if (emotion == null)
            return null;

        switch (emotion.trim().toUpperCase()) {

            case "BENEVOLÊNCIA":
                return BENEVOLENCE;
            case "MALEVOLÊNCIA":
                return MALEVOLENCE;
            case "BEM ESTAR":
                return COMFORT;
            case "MAL ESTAR":
                return DISCOMFORT;
            case "SEGURANÇA":
                return SAFETY;
            case "ANSIEDADE":
                return ANXIETY;
            case "SURPRESA":
                return SURPRISE;
            case "EMOÇÕES NÃO ESPECÍFICAS":
                return NON_SPECIFIC;
            case "INDIFERENÇA":
                return INDIFFERENCE;
            default:
                return null;
        }
    }

    public static Emotion.Intermediate mapIntermediateEmotion(String emotion) {

        if (emotion == null)
            return null;

        switch (emotion.trim().toUpperCase()) {

            case "AFEIÇÃO":
                return AFFECTION;
            case "GENTILEZA":
                return KINDNESS;
            case "ÓDIO":
                return HATE;
            case "AGRESSIVIDADE":
                return AGGRESSIVENESS;
            case "FELICIDADE":
                return HAPPINESS;
            case "SOFRIMENTO":
                return SUFFERING;
            case "LUCIDEZ":
                return LUCIDITY;
            case "LOUCURA":
                return MADNESS;
            case "ENTUSIASMO":
                return ENTHUSIASM;
            case "DEPRESSÃO":
                return DEPRESSION;
            case "ALÍVIO":
                return RELIEF;
            case "PERTURBAÇÃO":
                return DISTURBANCE;
            case "SATISFAÇÃO":
                return SATISFACTION;
            case "INSATISFAÇÃO":
                return DISSATISFACTION;
            case "CORAGEM":
                return COURAGE;
            case "MEDO":
                return FEAR;
            case "CALMA":
                return CALM;
            case "TENSÃO":
                return TENSION;
            default:
                return null;
        }
    }

    public static Emotion.Specific mapSpecificEmotion(String emotion) {

        if (emotion == null)
            return null;

        switch (emotion.trim().toUpperCase()) {

            case "ADMIRAÇÃO":
                return ADMIRATION;
            case "AMOR":
                return LOVE;
            case "ATRACÇÃO":
                return ATTRACTION;
            case "BONDADE":
                return GOODNESS;
            case "DOÇURA":
                return SWEETNESS;
            case "CRUELDADE":
                return CRUELTY;
            case "DESEJO":
                return DESIRE;
            case "DRAMA":
                return DRAMA;
            case "PACIÊNCIA":
                return PATIENCE;
            case "HUMILDADE":
                return HUMILITY;
            case "RESSENTIMENTO":
                return RESENTMENT;
            case "DESGOSTO":
                return DISGUST;
            case "DESPREZO":
                return CONTEMPT;
            case "IRRITAÇÃO":
                return IRRITATION;
            case "RAIVA":
                return RAGE;
            case "IRA":
                return IRE;
            case "ORGULHO":
                return ARROGANCE;
            case "FELIZ":
                return HAPPY;
            case "PRAZER":
                return PLEASURE;
            case "RISO":
                return LAUGH;
            case "DOR":
                return PAIN;
            case "CHORO":
                return CRY;
            case "SAÚDE MENTAL":
                return MENTAL_HEALTH;
            case "EQUILÍBRIO":
                return BALANCE;
            case "DOENÇA MENTAL":
                return MENTAL_DISEASE;
            case "DESIQUILÍBRIO":
                return IMBALANCE;
            case "ALEGRIA":
                return JOY;
            case "VIVACIDADE":
                return VIVACITY;
            case "ALERTA":
                return ALERT;
            case "TRISTEZA":
                return SADNESS;
            case "FADIGA":
                return FATIGUE;
            case "APATIA":
                return APATHY;
            case "APAZIGUAR":
                return APPEASE;
            case "LIBERTAÇÃO":
                return FREEDOM;
            case "AGITAÇÃO":
                return AGITATION;
            case "REMORSO":
                return REMORSE;
            case "ESTIMA":
                return ESTEEM;
            case "SACIEDADE":
                return SATIETY;
            case "AGRADO":
                return LIKE;
            case "HUMILHAÇÃO":
                return HUMILIATION;
            case "FRUSTRAÇÃO":
                return FRUSTRATION;
            case "DESAGRADO":
                return DISLIKE;
            case "AUDÁCIA":
                return AUDACITY;
            case "EXTROVERSÃO":
                return EXTROVERSION;
            case "PAVOR":
                return DREAD;
            case "INTROVERSÃO":
                return INTROVERSION;
            case "TRANQUILIDADE":
                return TRANQUILITY;
            case "RELAXAMENTO":
                return RELAXATION;
            case "ANGÚSTIA":
                return ANGUISH;
            case "INQUIETUDE":
                return CONCERN;
            default:
                return null;
        }
    }
}
