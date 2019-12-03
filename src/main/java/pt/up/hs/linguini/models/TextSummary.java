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
    private int nrOfNonStopWords;

    private int nrOfErrors;

    private int nrOfSentences;
    private int nrOfLemmas;

    private double avgWordLength;
    private double avgContentWordLength;

    private Map<String, Integer> wordFrequency;
    private Map<String, Integer> contentWordFrequency;
    private Map<Category, Set<String>> wordsByCategory;

    public TextSummary() {
    }

    public TextSummary(int nrOfCharacters, int nrOfNonBlankCharacters, int nrOfWords, int nrOfNonStopWords, int nrOfErrors, int nrOfSentences, int nrOfLemmas, double avgWordLength, double avgContentWordLength, Map<String, Integer> wordFrequency, Map<String, Integer> contentWordFrequency, Map<Category, Set<String>> wordsByCategory) {
        this.nrOfCharacters = nrOfCharacters;
        this.nrOfNonBlankCharacters = nrOfNonBlankCharacters;
        this.nrOfWords = nrOfWords;
        this.nrOfNonStopWords = nrOfNonStopWords;
        this.nrOfErrors = nrOfErrors;
        this.nrOfSentences = nrOfSentences;
        this.nrOfLemmas = nrOfLemmas;
        this.avgWordLength = avgWordLength;
        this.avgContentWordLength = avgContentWordLength;
        this.wordFrequency = wordFrequency;
        this.contentWordFrequency = contentWordFrequency;
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

    public int getNrOfNonStopWords() {
        return nrOfNonStopWords;
    }

    public void setNrOfNonStopWords(int nrOfNonStopWords) {
        this.nrOfNonStopWords = nrOfNonStopWords;
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

    public Map<Category, Set<String>> getWordsByCategory() {
        return wordsByCategory;
    }

    public void setWordsByCategory(Map<Category, Set<String>> wordsByCategory) {
        this.wordsByCategory = wordsByCategory;
    }
}
