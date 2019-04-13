package edu.smith.bloom;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.junit.Assert;
import org.junit.Test;

public class TestBloomFilter {
	
	/**
	 * Read all lines from the UNIX dictionary. Helper method for our tests.
	 * @return a list of words!
	 */
	public static List<String> loadDictionary() {
		List<String> words;
		try {
			// Read from a file:
			words = Files.readAllLines(new File("src/main/resources/words").toPath());
		} catch (IOException e) {
			throw new RuntimeException("Couldn't find dictionary.", e);
		}
		return words;
	}
	
	/**
	 * Take in real words and make fake words out of them. Helper method for our tests.
	 * @param realWords real words from which to craft fake words
	 * @param numSamples the number of fake words the user is seeking
	 * @return a list of fake words (words not in the dictionary)
	 */
	public static List<String> createFakeWords(List<String> realWords, int numSamples) {
		// Hint to the ArrayList that it will need to grow to numSamples size:
		List<String> output = new ArrayList<>(numSamples);
		int n = 0;
		int realWordsSize = realWords.size();
		Random generator = new Random();
		// Add words from the dictionary to the output list randomly, without repeats
		while (n < numSamples) {
			int r = generator.nextInt(realWordsSize);
			String word = realWords.get(r);
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
			n++;
		}
		return output;
	}
	
	// Following are some String class Bloom Filter tests
	
	@Test
	public void testEmpty() {
		BloomFilter<String> bloom = new BloomFilter<String>(100, 1);
		List<String> words = loadDictionary();
		for (String w : words) {
			Assert.assertFalse(bloom.contains(w));
		}
	}
	
	@Test
	public void testOne() {
		BloomFilter<String> bloom = new BloomFilter<String>(1000, 3);
		bloom.insert("asdfjkl");
		List<String> words = loadDictionary();
		for (String w : words) {
			Assert.assertFalse(bloom.contains(w));
		}
		Assert.assertTrue(bloom.contains("asdfjkl"));
	}
	
	/**
	 * loadDictionary contains 235886 entries
	 * fewer than 10 bits per element required for a 1% false positive probability, according to Wikipedia
	 * let's go for ~7 bits per element, so size ~1651202
	 * ideal number of hash functions = (size/numElementsInserting)(ln2) ~= 4.85, according to Wikipedia
	 */
	@Test
	public void testMany() {
		BloomFilter<String> bloom = new BloomFilter<String>(1751201, 5);
		List<String> words = loadDictionary();
		for (String w : words) {
			bloom.insert(w);
		}
		for (String w : words) {
			Assert.assertTrue(bloom.contains(w));
		}
		int count = 0;
		List<String> fakeWords = createFakeWords(words, 1000);
		for (String w : fakeWords) {
			if (bloom.contains(w)) {
				count++;
			}
		}
		/**
		 * In the ideal world, none of our fakeWords would be "in" the bloom filter.
		 * In the real world, let's hope less that 50 are in it.
		 */
		Assert.assertTrue(count < 50);
	}
	
	// Following are some Integer class Bloom Filter tests
	
	@Test
	public void testEmptyInt() {
		BloomFilter<Integer> bloom = new BloomFilter<Integer>(100, 1);
		for (int i = -1000; i <= 1000; i++) {
			Assert.assertFalse(bloom.contains(i));
		}
	}
	
	@Test
	public void testAFewInt() {
		BloomFilter<Integer> bloom = new BloomFilter<Integer>(1000, 3);
		bloom.insert(-1);
		bloom.insert(0);
		bloom.insert(1);
		for (int i = -1000; i <= 1000; i++) {
			if (i == -1 || i == 0 || i == 1) {
				Assert.assertTrue(bloom.contains(i));
			}
			else {
				Assert.assertFalse(bloom.contains(i));
			}
		}
	}
	
	/**
	 * let's insert 200000 entries
	 * fewer than 10 bits per element required for a 1% false positive probability, according to Wikipedia
	 * let's go for ~7 bits per element, so size ~1400000
	 * ideal number of hash functions = (size/numElementsInserting)(ln2) ~= 4.85, according to Wikipedia
	 */
	@Test
	public void testManyInt() {
		BloomFilter<Integer> bloom = new BloomFilter<Integer>(1400001, 5);
		for (int i = -100000; i < 100000; i++) {
			bloom.insert(i);
		}
		for (int i = -100000; i < 100000; i++) {
			Assert.assertTrue(bloom.contains(i));
		}
		int count = 0;
		for (int i = -100001; i >= -100500; i--) {
			if (bloom.contains(i)) {
				count++;
			}
		}
		for (int i = 100000; i <= 100499; i++) {
			if (bloom.contains(i)) {
				count++;
			}
		}
		/**
		 * In the ideal world, none of our numbers not in the bloom filter would be "in" the bloom filter.
		 * In the real world, let's hope less that 50 are in it.
		 */
		Assert.assertTrue(count < 50);
	}
	
	
}
