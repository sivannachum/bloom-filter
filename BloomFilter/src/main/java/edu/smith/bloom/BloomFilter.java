package edu.smith.bloom;

import java.util.BitSet;

import edu.smith.listADT.JavaList;
import edu.smith.listADT.ListADT;
/**
 * A Bloom Filter is a probabilistic data structure.
 * It supports the methods insert and contains, however its contains method can return false positives.
 * This data structure is more space efficient that a HashSet because it does not retain the actual values it store.
 * @author sivan
 */
public class BloomFilter<T>{
	// This variable keeps track of the size of the Bloom Filter's bits array.
	private int size;
	// This is the bits array in which the Bloom Filter "stores" values inserted into it.
	private BitSet bits;
	// This variable keeps track of how many hashes the Bloom Filter is using.
	private int numHash;
	// This is a list of the hashes the Bloom Filter is using.
	private ListADT<UniversalHash<T>> hashes;
	
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
		hashes = new JavaList<UniversalHash<T>>();
		// Create the hash functions for the Bloom Filter.
		for (int i=0; i<numHash; i++) {
			hashes.addBack(new UniversalHash<>());
		}
	}
	
	/**
	 * Insert a value into the Bloom Filter.
	 * @param value - the value to be inserted
	 */
	public void insert(T value) {
		// Hash the value to be inserted with all the hash functions.
		// Take the newly obtained values and set those indices in the bits array to 1/true.
		for (int i = 0; i < numHash; i++) {
			int index = hashes.getIndex(i).hash(value) % size;
			bits.set(index, true);
		}
	}
	
	/**
	 * Check if the Bloom Filter maybe contains a certain value.
	 * @param value - the value we want to know is in the Bloom Filter or not
	 * @return true if all the index values that the value hashes to are set to 1/true in the bits array 
	 */
	public boolean contains(T value) {
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
}