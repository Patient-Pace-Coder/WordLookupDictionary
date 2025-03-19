import java.io.*;
import java.util.*;

/**
 * TrieDictionary.java
 * Author: Rahul katteda
 * GitHub: https://github.com/Patient-Pace-Coder
 * Description: A Java implementation of a Trie-based dictionary that supports word lookup,
 * spelling correction, and auto-suggestions using the Levenshtein distance algorithm.
 * Date: 10/01/2025
 */

/**
 * A Trie-based dictionary that supports word lookup, spelling correction,
 * and auto-suggestions using the Levenshtein distance algorithm.
 */
public class TrieDictionary {

    private Trie trie = new Trie();
    private Map<String, Integer> wordFrequency = new HashMap<>();

    /**
     * Constructs the TrieDictionary by loading words from the specified dictionary file.
     *
     * @param dictionaryFilePath the path to the dictionary file
     * @throws IOException if an I/O error occurs
     */
    public TrieDictionary(String dictionaryFilePath) throws IOException {
        loadDictionary(dictionaryFilePath);
    }

    /**
     * Loads words from the dictionary file into the Trie and records their frequencies.
     *
     * @param dictionaryFilePath the path to the dictionary file
     * @throws IOException if an I/O error occurs
     */
    private void loadDictionary(String dictionaryFilePath) throws IOException {
        try (BufferedReader reader = new BufferedReader(new FileReader(dictionaryFilePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String word = line.trim().toLowerCase();
                if (!word.isEmpty()) {
                    trie.addWord(word);
                    wordFrequency.put(word, wordFrequency.getOrDefault(word, 0) + 1);
                }
            }
        }
    }

    /**
     * Checks if a word exists in the dictionary.
     *
     * @param word the word to check
     * @return true if the word exists, false otherwise
     */
    public boolean exists(String word) {
        return trie.wordExists(word.toLowerCase());
    }

    /**
     * Suggests the most similar word in the dictionary to the given input word.
     * It considers words with an edit distance of up to 2.
     *
     * @param inputWord the input word
     * @return the most similar word in the dictionary, or null if no similar word is found
     */
    public String suggestSimilarWord(String inputWord) {
        if (inputWord == null || inputWord.isEmpty()) {
            return null;
        }

        String lowerInput = inputWord.toLowerCase();
        if (trie.wordExists(lowerInput)) {
            return lowerInput;
        }

        TreeMap<Integer, TreeMap<Integer, TreeSet<String>>> suggestions = new TreeMap<>();

        for (String word : wordFrequency.keySet()) {
            int distance = calculateLevenshteinDistance(lowerInput, word);
            if (distance <= 2) {
                int frequency = wordFrequency.get(word);
                suggestions
                        .computeIfAbsent(distance, k -> new TreeMap<>(Collections.reverseOrder()))
                        .computeIfAbsent(frequency, k -> new TreeSet<>())
                        .add(word);
            }
        }
        System.out.println(suggestions); // to see the matched list of words
        if (!suggestions.isEmpty()) {
            return suggestions.firstEntry().getValue().firstEntry().getValue().first();
        }
        return null;
    }

    /**
     * Calculates the Levenshtein distance between two words.
     *
     * @param word1 the first word
     * @param word2 the second word
     * @return the Levenshtein distance
     */
    private int calculateLevenshteinDistance(String word1, String word2) {
        int len1 = word1.length();
        int len2 = word2.length();
        int[][] dp = new int[len1 + 1][len2 + 1];

        for (int i = 0; i <= len1; i++) {
            for (int j = 0; j <= len2; j++) {
                if (i == 0) {
                    dp[i][j] = j;
                } else if (j == 0) {
                    dp[i][j] = i;
                } else if (word1.charAt(i - 1) == word2.charAt(j - 1)) {
                    dp[i][j] = dp[i - 1][j - 1];
                } else {
                    dp[i][j] = 1 + Math.min(dp[i - 1][j - 1],
                            Math.min(dp[i - 1][j], dp[i][j - 1]));
                }
            }
        }

        return dp[len1][len2];
    }

    /**
     * Trie data structure for efficient word storage and lookup.
     */
    private static class Trie {

        private TrieNode root;

        public Trie() {
            root = new TrieNode();
        }

        /**
         * Adds a word to the Trie.
         *
         * @param word the word to add
         */
        public void addWord(String word) {
            TrieNode current = root;
            for (char c : word.toCharArray()) {
                // Skip non-alphabetic characters
                if (c < 'a' || c > 'z') {
                    continue;
                }
                int index = c - 'a';
                if (current.children[index] == null) {
                    current.children[index] = new TrieNode();
                }
                current = current.children[index];
            }
            current.isEndOfWord = true;
        }

        /**
         * Checks if a word exists in the Trie.
         *
         * @param word the word to check
         * @return true if the word exists, false otherwise
         */
        public boolean wordExists(String word) {
            TrieNode current = root;
            for (char c : word.toCharArray()) {
                // Skip non-alphabetic characters
                if (c < 'a' || c > 'z') {
                    continue;
                }
                int index = c - 'a';
                if (current.children[index] == null) {
                    return false;
                }
                current = current.children[index];
            }
            return current.isEndOfWord;
        }
    }

    /**
     * Trie node representing each character in the Trie.
     */
    private static class TrieNode {

        private TrieNode[] children;
        private boolean isEndOfWord;

        public TrieNode() {
            children = new TrieNode[26];
            isEndOfWord = false;
        }
    }

    /**
     * Main method for testing the TrieDictionary.
     *
     * @param args command-line arguments
     * @throws IOException if an I/O error occurs
     */
    public static void main(String[] args) throws IOException {
        // Adjust the path to your dictionary file accordingly
        // String dictionaryFilePath = "sowpods.txt";
        // String dictionaryFilePath = "src/sowpods.txt";
        String dictionaryFilePath = "src/words.txt_";
        TrieDictionary dictionary = new TrieDictionary(dictionaryFilePath);

        // Words for testing
        String[] testWords = {"example", "heroni", "sampl", "tes", "quikc", "correct","nerodh"};
        for (String word : testWords) {
            if (dictionary.exists(word)) {
                System.out.println(word + " exists in the dictionary.");
            } else {
                String suggestion = dictionary.suggestSimilarWord(word);
                if (suggestion != null) {
                    System.out.println("Did you mean '" + suggestion + "' instead of '" + word + "'?");
                } else {
                    System.out.println("No suggestions found for '" + word + "'.");
                }
            }
        }
    }
}