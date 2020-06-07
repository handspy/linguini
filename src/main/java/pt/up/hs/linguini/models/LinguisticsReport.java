package pt.up.hs.linguini.models;

import pt.up.hs.linguini.analysis.cooccurrence.CoOccurrence;

import java.util.List;
import java.util.Map;
import java.util.Set;

public class LinguisticsReport {

    // structural composition
    private int characterCount;
    private int nonBlankCharacterCount;
    private int wordCount;
    private int sentenceCount;

    // morphological composition
    private List<AnnotatedToken<String>> morphologicalAnnotations;
    private Map<String, Set<String>> wordsByCategory;
    private Map<String, Integer> contentWordFrequency;
    private Map<String, Integer> functionalWordFrequency;
    private Map<String, Integer> lemmaFrequency;

    // lexical diversity analysis
    private double baseTTR;
    private double hdd;
    private double mtld;
    private double vocd;

    // emotional analysis
    private List<AnnotatedToken<Emotion>> emotionalAnnotations;

    // co-occurrence analysis
    private List<CoOccurrence> coOccurrences;

    // idea density analysis
    private double ideaDensity;

    public LinguisticsReport() {
    }

    public int getCharacterCount() {
        return characterCount;
    }

    public void setCharacterCount(int characterCount) {
        this.characterCount = characterCount;
    }

    public int getNonBlankCharacterCount() {
        return nonBlankCharacterCount;
    }

    public void setNonBlankCharacterCount(int nonBlankCharacterCount) {
        this.nonBlankCharacterCount = nonBlankCharacterCount;
    }

    public int getWordCount() {
        return wordCount;
    }

    public void setWordCount(int wordCount) {
        this.wordCount = wordCount;
    }

    public int getSentenceCount() {
        return sentenceCount;
    }

    public void setSentenceCount(int sentenceCount) {
        this.sentenceCount = sentenceCount;
    }

    public List<AnnotatedToken<String>> getMorphologicalAnnotations() {
        return morphologicalAnnotations;
    }

    public void setMorphologicalAnnotations(List<AnnotatedToken<String>> morphologicalAnnotations) {
        this.morphologicalAnnotations = morphologicalAnnotations;
    }

    public Map<String, Integer> getContentWordFrequency() {
        return contentWordFrequency;
    }

    public void setContentWordFrequency(Map<String, Integer> contentWordFrequency) {
        this.contentWordFrequency = contentWordFrequency;
    }

    public Map<String, Integer> getFunctionalWordFrequency() {
        return functionalWordFrequency;
    }

    public void setFunctionalWordFrequency(Map<String, Integer> functionalWordFrequency) {
        this.functionalWordFrequency = functionalWordFrequency;
    }

    public Map<String, Integer> getLemmaFrequency() {
        return lemmaFrequency;
    }

    public void setLemmaFrequency(Map<String, Integer> lemmaFrequency) {
        this.lemmaFrequency = lemmaFrequency;
    }

    public Map<String, Set<String>> getWordsByCategory() {
        return wordsByCategory;
    }

    public void setWordsByCategory(Map<String, Set<String>> wordsByCategory) {
        this.wordsByCategory = wordsByCategory;
    }

    public double getBaseTTR() {
        return baseTTR;
    }

    public void setBaseTTR(double baseTTR) {
        this.baseTTR = baseTTR;
    }

    public double getHdd() {
        return hdd;
    }

    public void setHdd(double hdd) {
        this.hdd = hdd;
    }

    public double getMtld() {
        return mtld;
    }

    public void setMtld(double mtld) {
        this.mtld = mtld;
    }

    public double getVocd() {
        return vocd;
    }

    public void setVocd(double vocd) {
        this.vocd = vocd;
    }

    public List<AnnotatedToken<Emotion>> getEmotionalAnnotations() {
        return emotionalAnnotations;
    }

    public void setEmotionalAnnotations(List<AnnotatedToken<Emotion>> emotionalAnnotations) {
        this.emotionalAnnotations = emotionalAnnotations;
    }

    public List<CoOccurrence> getCoOccurrences() {
        return coOccurrences;
    }

    public void setCoOccurrences(List<CoOccurrence> coOccurrences) {
        this.coOccurrences = coOccurrences;
    }

    public double getIdeaDensity() {
        return ideaDensity;
    }

    public void setIdeaDensity(double ideaDensity) {
        this.ideaDensity = ideaDensity;
    }
}