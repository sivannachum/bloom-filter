package edu.smith.checkSpelling;


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
			if (dictionary.contains(w)) {
				found++;
			}
		}
		
		long endLookup = System.nanoTime();
		double fractionFound = found / (double) words.size();
		double timeSpentPerItem = (endLookup - startLookup) / ((double) words.size());
		int nsPerItem = (int) timeSpentPerItem;
		System.out.println("Lookup of items found="+fractionFound+" time="+nsPerItem+" ns/item");
	}
	
	/**
	 * This method looks for all the words in a dictionary
	 * by first seeing if a bloom filter contains it,
	 * then if the bloom filter says yes, it makes sure
	 * that it's actually in the dictionary.
	 * @param words - the "queries"
	 * @param dictionary - the data structure.
	 * @param bloom - the assisting bloom filter.
	 */
	public static void timeLookupBloom(List<String> words, Collection<String> dictionary, BloomFilter bloom) {
		long startLookup = System.nanoTime();
		
		int found = 0;
		for (String w : words) {
			if (bloom.contains(w)) {
				if (dictionary.contains(w)) {
					found++;
				}
			}
		}
		
		long endLookup = System.nanoTime();
		double fractionFound = found / (double) words.size();
		double timeSpentPerItem = (endLookup - startLookup) / ((double) words.size());
		int nsPerItem = (int) timeSpentPerItem;
		System.out.println(dictionary.getClass().getSimpleName()+" with Bloom assistance: Lookup of items found="+fractionFound+" time="+nsPerItem+" ns/item");
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
		
		start = System.nanoTime();
		BloomFilter bloom = new BloomFilter(1751201, 5);
		for (String w : listOfWords) {
			bloom.insert(w);
		}
		end = System.nanoTime();
		time = (end - start) / 1e9;
		System.out.println("Loaded BloomFilter in " + time +" seconds.");
		
		System.out.println("");
		System.out.println("Looking up all the words in the dictionary: ");
		// --- Make sure that every word in the dictionary is in the dictionary:
		System.out.print("TreeSet: ");
		timeLookup(listOfWords, treeOfWords);
		System.out.print("HashSet: ");
		timeLookup(listOfWords, hashOfWords);
		System.out.print("SortedStringListSet: ");
		timeLookup(listOfWords, bsl);
		System.out.print("CharTrie: ");
		timeLookup(listOfWords, trie);
		System.out.print("LLHash: ");
		timeLookup(listOfWords, hm100k);
		System.out.print("BloomFilter: ");
		timeLookup(listOfWords, bloom);
		
		System.out.println("");
		System.out.println("Bloom filter assistance, looking up all the words in the dictionary: ");
		timeLookupBloom(listOfWords, treeOfWords, bloom);
		timeLookupBloom(listOfWords, hashOfWords, bloom);
		timeLookupBloom(listOfWords, bsl, bloom);
		timeLookupBloom(listOfWords, trie, bloom);
		timeLookupBloom(listOfWords, hm100k, bloom);
		
		System.out.println("");
		System.out.println("Mixed data set checking: ");
		for (int i=0; i<10; i++) {
			// --- Create a dataset of mixed hits and misses with p=i/10.0
			List<String> hitsAndMisses = createMixedDataset(listOfWords, 10_000, i/10.0);
			
			// --- Time the data structures.
			System.out.print("TreeSet: ");
			timeLookup(hitsAndMisses, treeOfWords);
			System.out.print("HashSet: ");
			timeLookup(hitsAndMisses, hashOfWords);
			System.out.print("SortedStringListSet: ");
			timeLookup(hitsAndMisses, bsl);
			System.out.print("CharTrie: ");
			timeLookup(hitsAndMisses, trie);
			System.out.print("LLHash: ");
			timeLookup(hitsAndMisses, hm100k);
			System.out.print("BloomFilter: ");
			timeLookup(hitsAndMisses, bloom);
		}
		
		System.out.println("");
		System.out.println("Bloom filter assistance, mixed data set checking: ");
		for (int i=0; i<10; i++) {
			// --- Create a dataset of mixed hits and misses with p=i/10.0
			List<String> hitsAndMisses = createMixedDataset(listOfWords, 10_000, i/10.0);
			
			// --- Time the data structures.
			timeLookupBloom(hitsAndMisses, treeOfWords, bloom);
			timeLookupBloom(hitsAndMisses, hashOfWords, bloom);
			timeLookupBloom(hitsAndMisses, bsl, bloom);
			timeLookupBloom(hitsAndMisses, trie, bloom);
			timeLookupBloom(hitsAndMisses, hm100k, bloom);
		}
		
		// See how long it takes the data structures to find all the words in the book
		System.out.println("");
		System.out.println("Looking for words in a book: ");
		List<String> book = loadBook();
		System.out.print("TreeSet: ");
		timeLookup(book, treeOfWords);
		System.out.print("HashSet: ");
		timeLookup(book, hashOfWords);
		System.out.print("SortedStringListSet: ");
		timeLookup(book, bsl);
		System.out.print("CharTrie: ");
		timeLookup(book, trie);
		System.out.print("LLHash: ");
		timeLookup(book, hm100k);
		System.out.print("BloomFilter: ");
		timeLookup(book, bloom);
		
		System.out.println("");
		System.out.println("Bloom filter assistance, looking for words in a book: ");
		timeLookupBloom(book, treeOfWords, bloom);
		timeLookupBloom(book, hashOfWords, bloom);
		timeLookupBloom(book, bsl, bloom);
		timeLookupBloom(book, trie, bloom);
		timeLookupBloom(book, hm100k, bloom);
	}
}

