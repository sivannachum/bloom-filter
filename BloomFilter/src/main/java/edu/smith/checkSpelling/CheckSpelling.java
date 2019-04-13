package edu.smith.checkSpelling;

import edu.smith.bloom.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.TreeSet;

public class CheckSpelling {
	/**
	 * Read all lines from the UNIX dictionary.
	 * @return a list of words!
	 */
	public static List<String> loadDictionary() {
		long start = System.nanoTime();
		List<String> words;
		try {
			// Read from a file:
			words = Files.readAllLines(new File("src/main/resources/words").toPath());
		} catch (IOException e) {
			throw new RuntimeException("Couldn't find dictionary.", e);
		}
		long end = System.nanoTime();
		double time = (end - start) / 1e9;
		System.out.println("Loaded " + words.size() + " entries in " + time +" seconds.");
		return words;
	}
	
	/**
	 * Return all lists from some Project Gutenberg book.
	 * @return a list of all the words in the book!
	 */
	public static List<String> loadBook() {
		List<String> words = new ArrayList<String>();
		List<String> lines;
		long start = System.nanoTime();
		// take all the text in the book
		try {
			lines = Files.readAllLines(new File("src/main/resources/book").toPath());
		} catch (IOException e) {
			throw new RuntimeException("Couldn't find the book.", e);
		}
		// split all that text into the individual words
		for (String s : lines) {
			words.addAll(WordSplitter.splitTextToWords(s));
		}
		long end = System.nanoTime();
		double time = (end - start) / 1e9;
		System.out.println("Loaded " + words.size() + " entries in " + time + " seconds.");
		return words;
	}
	
	/**
	 * This method looks for all the words in a dictionary.
	 * @param words - the "queries"
	 * @param dictionary - the data structure.
	 */
	public static void timeLookup(List<String> words, Collection<String> dictionary) {
		long startLookup = System.nanoTime();
		
		int found = 0;
		for (String w : words) {
			// The commented out code is how I figured out what kinds of words were considered "Mis-spelled."
			// I left it in in case you wanted to see how I did it.
			// Random generator = new Random();
			if (dictionary.contains(w)) {
				found++;
			}
			/*
			else if (generator.nextInt(10000) == 1) {
				System.out.print(w + " ");
			}
			*/
		}
		
		long endLookup = System.nanoTime();
		double fractionFound = found / (double) words.size();
		double timeSpentPerItem = (endLookup - startLookup) / ((double) words.size());
		int nsPerItem = (int) timeSpentPerItem;
		System.out.println(dictionary.getClass().getSimpleName()+": Lookup of items found="+fractionFound+" time="+nsPerItem+" ns/item");
	}
	
	/**
	 * Creates a data set with some real and some not real words
	 * @param yesWords Words that are in the dictionary
	 * @param numSamples the number of words you want in the dataset
	 * @param fractionYes the number of real words you want in the data set
	 * @return a dataset with some real and some not real words
	 */
	public static List<String> createMixedDataset(List<String> yesWords, int numSamples, double fractionYes) {
		// Hint to the ArrayList that it will need to grow to numSamples size:
		List<String> output = new ArrayList<>(numSamples);
		int n = 0;
		int yesWordsSize = yesWords.size();
		// Add words from the dictionary to the output list randomly, without repeats
		Random generator = new Random();
		while (n < numSamples * fractionYes) {
			int r = generator.nextInt(yesWordsSize);
			String word = yesWords.get(r);
			if (output.contains(word)) {
				continue;
			}
			else {
				output.add(word);
				n++;
			}
		}
		// Make the rest of the words in the output list not real words
		while (output.size() < numSamples) {
			int r = generator.nextInt(yesWordsSize);
			String word = yesWords.get(r);
			int length = word.length();
			String newWord;
			if (length <= 2) {
				// the odds of this being a real word are low
				newWord = word + "zs";
			}
			else {
				int z = generator.nextInt(length - 2);
				z++;
				String sub1 = word.substring(0, z);
				String sub2 = word.substring(z);
				// the odds of this being a real word are low
				newWord = sub1 + "zs" + sub2;
			}
			output.add(newWord);
		}
		return output;
	}
	
	
	public static void main(String[] args) {
		// --- Load the dictionary.
		List<String> listOfWords = loadDictionary();
		
		// --- Create a bunch of data structures for testing:
		// Time how long their creation takes and print it out.
		long start = System.nanoTime();
		TreeSet<String> treeOfWords = new TreeSet<>(listOfWords);
		long end = System.nanoTime();
		double time = (end - start) / 1e9;
		System.out.println("Loaded TreeSet in " + time +" seconds.");
		start = System.nanoTime();
		HashSet<String> hashOfWords = new HashSet<>(listOfWords);
		end = System.nanoTime();
		time = (end - start) / 1e9;
		System.out.println("Loaded HashSet in " + time +" seconds.");
		
		// Load TreeSet and HashSet with a loop instead
		start = System.nanoTime();
		TreeSet<String> treeWords = new TreeSet<>();
		for (String w : listOfWords) {
			treeWords.add(w);
		}
		end = System.nanoTime();
		time = (end - start) / 1e9;
		System.out.println("Loaded TreeSet with for loop in " + time +" seconds.");
		start = System.nanoTime();
		HashSet<String> hashWords = new HashSet<>();
		for (String w : listOfWords) {
			hashWords.add(w);
		}
		end = System.nanoTime();
		time = (end - start) / 1e9;
		System.out.println("Loaded HashSet with for loop in " + time +" seconds.");
		
		start = System.nanoTime();
		SortedStringListSet bsl = new SortedStringListSet(listOfWords);
		end = System.nanoTime();
		time = (end - start) / 1e9;
		System.out.println("Loaded SortedStringListSet in " + time +" seconds.");
		start = System.nanoTime();
		CharTrie trie = new CharTrie();
		for (String w : listOfWords) {
			trie.insert(w);
		}
		end = System.nanoTime();
		time = (end - start) / 1e9;
		System.out.println("Loaded CharTrie in " + time +" seconds.");
		start = System.nanoTime();
		LLHash hm100k = new LLHash(100000);
		for (String w : listOfWords) {
			hm100k.add(w);
		}
		end = System.nanoTime();
		time = (end - start) / 1e9;
		System.out.println("Loaded LLHash in " + time +" seconds.");
		// --- Make sure that every word in the dictionary is in the dictionary:
		timeLookup(listOfWords, treeOfWords);
		timeLookup(listOfWords, hashOfWords);
		timeLookup(listOfWords, bsl);
		timeLookup(listOfWords, trie);
		timeLookup(listOfWords, hm100k);
		
		
		for (int i=0; i<10; i++) {
			// --- Create a dataset of mixed hits and misses with p=i/10.0
			List<String> hitsAndMisses = createMixedDataset(listOfWords, 10_000, i/10.0);
			
			// --- Time the data structures.
			timeLookup(hitsAndMisses, treeOfWords);
			timeLookup(hitsAndMisses, hashOfWords);
			timeLookup(hitsAndMisses, bsl);
			timeLookup(hitsAndMisses, trie);
			timeLookup(hitsAndMisses, hm100k);
		}
		
		// See how long it takes the data structures to find all the words in the book
		List<String> book = loadBook();
		timeLookup(book, treeOfWords);
		timeLookup(book, hashOfWords);
		timeLookup(book, bsl);
		timeLookup(book, trie);
		timeLookup(book, hm100k);

		
		// --- linear list timing:
		// Looking up in a list is so slow, we need to sample:
		System.out.println("Start of list: ");
		timeLookup(listOfWords.subList(0, 1000), listOfWords);
		System.out.println("End of list: ");
		timeLookup(listOfWords.subList(listOfWords.size()-100, listOfWords.size()), listOfWords);
		
	
		// --- print statistics about the data structures:
		System.out.println("Count-Nodes: "+trie.countNodes());
		System.out.println("Count-Items: "+hm100k.size());

		System.out.println("Count-Collisions[100k]: "+hm100k.countCollisions());
		System.out.println("Count-Used-Buckets[100k]: "+hm100k.countUsedBuckets());
		System.out.println("Load-Factor[100k]: "+hm100k.countUsedBuckets() / 100000.0);

		
		System.out.println("log_2 of listOfWords.size(): "+listOfWords.size());
		
		System.out.println("Done!");
	}
}

