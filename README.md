# TrieDictionary

A Java implementation of a Trie-based dictionary that supports word lookup, spelling correction, and auto-suggestions using the Levenshtein distance algorithm.

## Features
- Add words to the dictionary.
- Check if a word exists in the dictionary.
- Suggest similar words based on edit distance.

## Usage
1. Clone the repository.
2. Place your `words.txt` file in the same directory as `TrieDictionary.java`.
3. Compile and run the `TrieDictionary.java` file.

## Example
```java
String dictionaryFilePath = "words.txt";
TrieDictionary dictionary = new TrieDictionary(dictionaryFilePath);
System.out.println(dictionary.exists("hello")); // true or false