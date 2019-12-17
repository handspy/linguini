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
    private int nrOfLemmas;

    private double avgWordLength;
    private double avgStopWordLength;
    private double avgFunctionalWordLength;
    private double avgContentWordLength;

    private Map<String, Integer> tokenFrequency;

    private Map<String, Integer> wordFrequency;
    private Set<String> stopWords;
    private Set<String> functionalWords;
    private Set<String> contentWords;

    private Map<String, Integer> lemmaFrequency;
    private Set<String> lemmas;

    private Map<Category, Set<String>> wordsByCategory;

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
            int nrOfLemmas,
            double avgWordLength,
            double avgStopWordLength,
            double avgFunctionalWordLength,
            double avgContentWordLength,
            Map<String, Integer> tokenFrequency,
            Map<String, Integer> wordFrequency,
            Set<String> stopWords,
            Set<String> functionalWords,
            Set<String> contentWords,
            Map<String, Integer> lemmaFrequency,
            Set<String> lemmas, Map<Category,
            Set<String>> wordsByCategory) {
        this.nrOfCharacters = nrOfCharacters;
        this.nrOfNonBlankCharacters = nrOfNonBlankCharacters;
        this.nrOfWords = nrOfWords;
        this.nrOfStopWords = nrOfStopWords;
        this.nrOfFunctionalWords = nrOfFunctionalWords;
        this.nrOfContentWords = nrOfContentWords;
        this.nrOfErrors = nrOfErrors;
        this.nrOfSentences = nrOfSentences;
        this.nrOfLemmas = nrOfLemmas;
        this.avgWordLength = avgWordLength;
        this.avgStopWordLength = avgStopWordLength;
        this.avgFunctionalWordLength = avgFunctionalWordLength;
        this.avgContentWordLength = avgContentWordLength;
        this.tokenFrequency = tokenFrequency;
        this.wordFrequency = wordFrequency;
        this.stopWords = stopWords;
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

    public int getNrOfLemmas() {
        return nrOfLemmas;
    }

    public void setNrOfLemmas(int nrOfLemmas) {
        this.nrOfLemmas = nrOfLemmas;
    }

    public double getAvgWordLength() {
        return avgWordLength;
    }

    public void setAvgWordLength(double avgWordLength) {
        this.avgWordLength = avgWordLength;
    }

    public double getAvgStopWordLength() {
        return avgStopWordLength;
    }

    public void setAvgStopWordLength(double avgStopWordLength) {
        this.avgStopWordLength = avgStopWordLength;
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

    public Map<String, Integer> getTokenFrequency() {
        return tokenFrequency;
    }

    public void setTokenFrequency(Map<String, Integer> tokenFrequency) {
        this.tokenFrequency = tokenFrequency;
    }

    public Map<String, Integer> getWordFrequency() {
        return wordFrequency;
    }

    public void setWordFrequency(Map<String, Integer> wordFrequency) {
        this.wordFrequency = wordFrequency;
    }

    public Set<String> getStopWords() {
        return stopWords;
    }

    public void setStopWords(Set<String> stopWords) {
        this.stopWords = stopWords;
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

    public Map<Category, Set<String>> getWordsByCategory() {
        return wordsByCategory;
    }

    public void setWordsByCategory(Map<Category, Set<String>> wordsByCategory) {
        this.wordsByCategory = wordsByCategory;
    }
}
