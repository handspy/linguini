package pt.up.hs.linguini.emotaix;

import pt.up.hs.linguini.models.Emotion;

import java.util.List;

public interface IEmotaixSearch {

    boolean contains(String word);

    List<Emotion> retrieveEmotions(String word);

    int size();
}
