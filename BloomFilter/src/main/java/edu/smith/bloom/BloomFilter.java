package edu.smith.bloom;

import java.util.BitSet;

public class BloomFilter<ItemType>{
	private int size;
	private int numElements;
	private BitSet bits;
	public BloomFilter(int size, int numElements) {
		this.size = size;
		this.numElements = numElements;
		bits = new BitSet(size);
	}
	public void insert(ItemType value) {
		
	}
}
