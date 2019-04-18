package edu.smith.checkSpelling;

import java.util.AbstractSet;
import java.util.BitSet;
import java.util.Iterator;

import edu.smith.bloom.UniversalHash;
import edu.smith.listADT.JavaList;
import edu.smith.listADT.ListADT;

/**
 * A Bloom Filter is a probabilistic data structure.
 * It supports the methods insert and contains, however its contains method can return false positives.
 * This data structure is more space efficient that a HashSet because it does not retain the actual values it store.
 * This version extends AbstractSet<String> so we can call the timeLookup method in the CheckSpelling class.
 * @author sivan
 */
public class BloomFilter extends AbstractSet<String>{
	// This variable keeps track of the size of the Bloom Filter's bits array.
	private int size;
	// This is the bits array in which the Bloom Filter "stores" values inserted into it.
	private BitSet bits;
	// This variable keeps track of how many hashes the Bloom Filter is using.
	private int numHash;
	// This is a list of the hashes the Bloom Filter is using.
	private ListADT<UniversalHash<String>> hashes;
	
	/**
	 * Bloom Filter constructor.
	 * The user is responsible for picking a reasonable size and number of hash functions
	 * based on how much data they think they will be inserting.
	 * @param size - the size for the Bloom Filter's bits array
	 * @param numHash - the number of hash functions the Bloom Filter will use
	 */
	public BloomFilter(int size, int numHash) {
		this.size = size;
		bits = new BitSet(size);
		this.numHash = numHash;
		hashes = new JavaList<UniversalHash<String>>();
		// Create the hash functions for the Bloom Filter.
		for (int i=0; i<numHash; i++) {
			hashes.addBack(new UniversalHash<>());
		}
	}
	
	/**
	 * Insert a value (in this case a word) into the Bloom Filter.
	 * @param value - the value to be inserted
	 */
	public void insert(String value) {
		// Hash the value to be inserted with all the hash functions.
		// Take the newly obtained values and set those indices in the bits array to 1/true.		
		for (int i = 0; i < numHash; i++) {
			int index = hashes.getIndex(i).hash(value) % size;
			bits.set(index, true);
		}
	}
	
	/**
	 * Check if the Bloom Filter maybe contains a certain value (in this case, a certain word).
	 * @param obj - the value we want to know is in the Bloom Filter or not
	 * @return true if all the index values that the value hashes to are set to 1/true in the bits array 
	 */
	@Override
	public boolean contains(Object obj) {
		// Cast the Object as a String so we can hash it the same way as we did in the insert method.
		String value = (String) obj;
		// Hash the value which we are checking if it is in the Bloom Filter with all the hash functions.
		// If any of the index values generated from the hash in the bits array are set to 0/false,
		// the item has definitely not been inserted, so return false.		
		for (int i = 0; i < numHash; i++) {
			int index = hashes.getIndex(i).hash(value) % size;
			if (!bits.get(index)) {
				return false;
			}
		}
		// Otherwise the value MIGHT be in the Bloom Filter, so return true.
		// The user should realize that this may be a false positive.
		return true;
	}
	
	@Override
	public Iterator<String> iterator() {
		// cannot iterate over this data structure because it doesn't actually store its values
		return null;
	}
	@Override
	public int size() {
		// return how large our bit array is
		// cannot return how many items are in the filter because there is no way to know without a Counting Filter.
		return size;
	}
}
