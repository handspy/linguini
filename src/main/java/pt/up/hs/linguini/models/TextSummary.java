package pt.up.hs.linguini.models;

import java.util.Map;
import java.util.Set;

/**
 * Summary of stats of the text.
 *
 * @author José Carlos Paiva <code>josepaiva94@gmail.com</code>
 */
public class TextSummary {

    private int nrOfCharacters;
    private int nrOfNonBlankCharacters;

    private int nrOfWords;
    private int nrOfStopWords;
    private int nrOfFunctionalWords;
    private int nrOfContentWords;

    private int nrOfErrors;

    private int nrOfSentences;
    private int nrOfDistinctLemmas;

    private double avgWordLength;
    private double avgNonStopWordLength;
    private double avgFunctionalWordLength;
    private double avgContentWordLength;

    private Map<String, Integer> wordFrequency;
    private Map<String, Integer> contentWordFrequency;
    private Map<String, Integer> functionalWordFrequency;

    private Set<String> functionalWords;
    private Set<String> contentWords;

    private Map<String, Integer> lemmaFrequency;
    private Set<String> lemmas;

    private Map<String, Set<String>> wordsByCategory;

    public TextSummary() {
    }

    public TextSummary(
            int nrOfCharacters,
            int nrOfNonBlankCharacters,
            int nrOfWords,
            int nrOfStopWords,
            int nrOfFunctionalWords,
            int nrOfContentWords,
            int nrOfErrors,
            int nrOfSentences,
            int nrOfDistinctLemmas,
            double avgWordLength,
            double avgNonStopWordLength,
            double avgFunctionalWordLength,
            double avgContentWordLength,
            Map<String, Integer> wordFrequency,
            Map<String, Integer> contentWordFrequency,
            Map<String, Integer> functionalWordFrequency,
            Set<String> functionalWords,
            Set<String> contentWords,
            Map<String, Integer> lemmaFrequency,
            Set<String> lemmas,
            Map<String, Set<String>> wordsByCategory) {
        this.nrOfCharacters = nrOfCharacters;
        this.nrOfNonBlankCharacters = nrOfNonBlankCharacters;
        this.nrOfWords = nrOfWords;
        this.nrOfStopWords = nrOfStopWords;
        this.nrOfFunctionalWords = nrOfFunctionalWords;
        this.nrOfContentWords = nrOfContentWords;
        this.nrOfErrors = nrOfErrors;
        this.nrOfSentences = nrOfSentences;
        this.nrOfDistinctLemmas = nrOfDistinctLemmas;
        this.avgWordLength = avgWordLength;
        this.avgNonStopWordLength = avgNonStopWordLength;
        this.avgFunctionalWordLength = avgFunctionalWordLength;
        this.avgContentWordLength = avgContentWordLength;
        this.wordFrequency = wordFrequency;
        this.contentWordFrequency = contentWordFrequency;
        this.functionalWordFrequency = functionalWordFrequency;
        this.functionalWords = functionalWords;
        this.contentWords = contentWords;
        this.lemmaFrequency = lemmaFrequency;
        this.lemmas = lemmas;
        this.wordsByCategory = wordsByCategory;
    }

    public int getNrOfCharacters() {
        return nrOfCharacters;
    }

    public void setNrOfCharacters(int nrOfCharacters) {
        this.nrOfCharacters = nrOfCharacters;
    }

    public int getNrOfNonBlankCharacters() {
        return nrOfNonBlankCharacters;
    }

    public void setNrOfNonBlankCharacters(int nrOfNonBlankCharacters) {
        this.nrOfNonBlankCharacters = nrOfNonBlankCharacters;
    }

    public int getNrOfWords() {
        return nrOfWords;
    }

    public void setNrOfWords(int nrOfWords) {
        this.nrOfWords = nrOfWords;
    }

    public int getNrOfStopWords() {
        return nrOfStopWords;
    }

    public void setNrOfStopWords(int nrOfStopWords) {
        this.nrOfStopWords = nrOfStopWords;
    }

    public int getNrOfFunctionalWords() {
        return nrOfFunctionalWords;
    }

    public void setNrOfFunctionalWords(int nrOfFunctionalWords) {
        this.nrOfFunctionalWords = nrOfFunctionalWords;
    }

    public int getNrOfContentWords() {
        return nrOfContentWords;
    }

    public void setNrOfContentWords(int nrOfContentWords) {
        this.nrOfContentWords = nrOfContentWords;
    }

    public int getNrOfErrors() {
        return nrOfErrors;
    }

    public void setNrOfErrors(int nrOfErrors) {
        this.nrOfErrors = nrOfErrors;
    }

    public int getNrOfSentences() {
        return nrOfSentences;
    }

    public void setNrOfSentences(int nrOfSentences) {
        this.nrOfSentences = nrOfSentences;
    }

    public int getNrOfDistinctLemmas() {
        return nrOfDistinctLemmas;
    }

    public void setNrOfDistinctLemmas(int nrOfDistinctLemmas) {
        this.nrOfDistinctLemmas = nrOfDistinctLemmas;
    }

    public double getAvgWordLength() {
        return avgWordLength;
    }

    public void setAvgWordLength(double avgWordLength) {
        this.avgWordLength = avgWordLength;
    }

    public double getAvgNonStopWordLength() {
        return avgNonStopWordLength;
    }

    public void setAvgNonStopWordLength(double avgNonStopWordLength) {
        this.avgNonStopWordLength = avgNonStopWordLength;
    }

    public double getAvgFunctionalWordLength() {
        return avgFunctionalWordLength;
    }

    public void setAvgFunctionalWordLength(double avgFunctionalWordLength) {
        this.avgFunctionalWordLength = avgFunctionalWordLength;
    }

    public double getAvgContentWordLength() {
        return avgContentWordLength;
    }

    public void setAvgContentWordLength(double avgContentWordLength) {
        this.avgContentWordLength = avgContentWordLength;
    }

    public Map<String, Integer> getWordFrequency() {
        return wordFrequency;
    }

    public void setWordFrequency(Map<String, Integer> wordFrequency) {
        this.wordFrequency = wordFrequency;
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

    public Set<String> getFunctionalWords() {
        return functionalWords;
    }

    public void setFunctionalWords(Set<String> functionalWords) {
        this.functionalWords = functionalWords;
    }

    public Set<String> getContentWords() {
        return contentWords;
    }

    public void setContentWords(Set<String> contentWords) {
        this.contentWords = contentWords;
    }

    public Map<String, Integer> getLemmaFrequency() {
        return lemmaFrequency;
    }

    public void setLemmaFrequency(Map<String, Integer> lemmaFrequency) {
        this.lemmaFrequency = lemmaFrequency;
    }

    public Set<String> getLemmas() {
        return lemmas;
    }

    public void setLemmas(Set<String> lemmas) {
        this.lemmas = lemmas;
    }

    public Map<String, Set<String>> getWordsByCategory() {
        return wordsByCategory;
    }

    public void setWordsByCategory(Map<String, Set<String>> wordsByCategory) {
        this.wordsByCategory = wordsByCategory;
    }
}
