package com.tesu.example;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.Scanner;

public class SpellChecker {

	private static int wordsCountInDictionary = 0;

	// Depends on number of words present in dictionary, take the next prime number
	private static int tableSize = 45491;

	private static String[] hashTable;
	/** number of words to be spell-checked */
	private static int numberOfWords = 0;
	/** number of misspelled or questionable words */
	private static int wordsNotFoundInDictionary = 0;

	private static PrintStream out;

	/**
	 * 
	 * @param args
	 *            command line arguments
	 * @throws FileNotFoundException
	 *             if the input and/or dictionary files are not found
	 */
	public static void main(String[] args) throws FileNotFoundException {
		// Create the output folder
		createOutputFolder();

		File dictionaryFile = new File(System.getProperty("user.dir") + File.separator + "lib" + File.separator + "dictionary.txt");
		File inputFile = new File(System.getProperty("user.dir") + File.separator + "input" + File.separator + "input1.txt");
		File outputFile = new File(System.getProperty("user.dir") + File.separator + "output" + File.separator + System.currentTimeMillis() + ".txt");

		out = new PrintStream(outputFile);

		// Build a new hash table and fill it with the words from the
		// dictionary
		buildHashTable(dictionaryFile);

		// ---- Output the Results ----
		out.println("=============================================");
		out.println("===== Misspelled/ Questionable Words: =======");
		out.println("=============================================");

		processWords(inputFile);

		out.println("=============================================");
		out.printf("Number of words in dictionary: %d \n", wordsCountInDictionary);
		out.printf("Number of words to be examined: %d \n", numberOfWords);
		out.printf("Number of misspelled/ questionable words: %d \n", wordsNotFoundInDictionary);
		out.println("=============================================");

	}

	private static void createOutputFolder() {
		File output = new File(System.getProperty("user.dir") + File.separator + "output");
		output.mkdir();

	}

	/**
	 * 
	 * @param dict
	 *            dictionary of words to add to the hash table
	 * @throws FileNotFoundException
	 *             if dictionary cannot be read
	 */
	private static void buildHashTable(File dict) throws FileNotFoundException {

		// Set the hash table to be a new table of this size
		hashTable = new String[tableSize + 1];

		Scanner readDictionary = new Scanner(dict);
		// read in the words from the dictionary, perform the hash function, and
		// then add them to the hash table//
		while (readDictionary.hasNext()) {
			String word = readDictionary.next();
			int hf = hashFunction(word);
			while (hashTable[hf] != null) {
				hf++;
			}
			hashTable[hf] = word;
			wordsCountInDictionary++;
		}
		readDictionary.close();

	}

	/**
	 * 
	 * 
	 * @param input
	 * @throws FileNotFoundException
	 */
	private static void processWords(File input) throws FileNotFoundException {
		// Reads the input file word by word, ensuring that the words are in the proper format before checking them against the dictionary
		Scanner in = new Scanner(input);
		boolean found = false;
		while (in.hasNext()) {
			String s = format(in.next());

			numberOfWords++;
			found = spellCheck(s);
			if (!found) {
				wordsNotFoundInDictionary++;
				out.println(s);
			}
		}

		in.close();

	}

	/**
	 * 
	 * 
	 * @param word
	 * @return
	 */
	private static boolean spellCheck(String word) {
		int index = hashFunction(word);
		while (hashTable[index] != null) {
			if (word.equals(hashTable[index])) {
				return true;
			} else {
				index++;
			}
		}

		// ---- set up some of the sneak characters/ character strings that
		// might make a correctly spelled word seem misspelled.

		// Because all single letters are in the dictionary, any word that
		// reaches this point is guaranteed to have at least 2 characters, so we
		// do not need to test for length before assigning the end string
		if (Character.isUpperCase(word.charAt(0))) {
			word = word.toLowerCase();
			return spellCheck(word);
		}
		String end = "";
		if (word.length() >= 2) {
			end = word.substring(word.length() - 2);
		}
		char fin = word.charAt(word.length() - 1);
		// However, we do need to check that a string has three characters
		// before assigning the 3-char end string to check for 'ing'
		String ing = "";
		if (word.length() > 3) {
			ing = word.substring(word.length() - 3);
		}
		if (ing.equals("ing")) {
			word = word.substring(0, word.length() - 3);
			return spellCheck(word);
		} else if (end.equals("ly") || end.equals("'s")) {
			word = word.substring(0, word.length() - 2);
			return spellCheck(word);
		} else if (fin == 's') {
			word = word.substring(0, word.length() - 1);
			Boolean finFound = spellCheck(word);
			if (!finFound && word.charAt(word.length() - 1) == 'e') {
				word = word.substring(0, word.length() - 1);
				finFound = spellCheck(word);
			}
			return finFound;
		} else if (fin == 'd') {
			word = word.substring(0, word.length() - 1);
			Boolean finFound = spellCheck(word);
			if (!finFound && word.charAt(word.length() - 1) == 'e') {
				word = word.substring(0, word.length() - 1);
				finFound = spellCheck(word);
			}
			return finFound;
		} else if (fin == 'r') {
			word = word.substring(0, word.length() - 1);
			Boolean finFound = spellCheck(word);
			if (!finFound && word.charAt(word.length() - 1) == 'e') {
				word = word.substring(0, word.length() - 1);
				finFound = spellCheck(word);
			}
			return finFound;
		}

		return false;

	}

	/**
	 * 
	 * 
	 * @param s
	 * @return
	 */
	private static String format(String s) {
		// Removing any special symbol if found
		if (!Character.isLetterOrDigit(s.charAt(0)) && s.length() > 1) {
			s = s.substring(1);
		}

		while (!Character.isLetter(s.charAt(s.length() - 1)) && s.length() > 1) {
			s = s.substring(0, s.length() - 1);
		}

		return s;

	}

	/**
	 * 
	 * @param s
	 *            the word to hash
	 * @return the hash-code index into the table
	 */
	private static int hashFunction(String s) {
		int hashKey = 0;

		for (int x = 0; x < s.length(); x++) {
			int askey = Math.abs(s.charAt(x) - 47);
			hashKey = (hashKey * 47 + askey) % tableSize;
		}
		return hashKey;

	}

}
