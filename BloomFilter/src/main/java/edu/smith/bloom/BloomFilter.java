package edu.smith.bloom;

import java.util.BitSet;

public class BloomFilter<ItemType>{
	private int size;
	private int numHash;
	private BitSet bits;
	public BloomFilter(int size, int numHash) {
		this.size = size;
		this.numHash = numHash;
		bits = new BitSet(size);
	}
	public void insert(ItemType value) {
		
	}
	public void contains(ItemType value) {
		
	}
}
