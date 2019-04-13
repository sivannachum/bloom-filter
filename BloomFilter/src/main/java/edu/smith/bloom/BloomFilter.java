package edu.smith.bloom;

import java.util.BitSet;

import edu.smith.listADT.JavaList;
import edu.smith.listADT.ListADT;

public class BloomFilter<T>{
	private int size;
	private BitSet bits;
	private int numHash;
	private ListADT<UniversalHash<T>> hashes;
	public BloomFilter(int size, int numHash) {
		this.size = size;
		bits = new BitSet(size);
		this.numHash = numHash;
		hashes = new JavaList<UniversalHash<T>>();
		for (int i=0; i<numHash; i++) {
			hashes.addBack(new UniversalHash<>());
		}
	}
	public void insert(T value) {
		for (int i = 0; i < numHash; i++) {
			int index = hashes.getIndex(i).hash(value) % size;
			bits.set(index, true);
		}
	}
	public boolean contains(T value) {
		for (int i = 0; i < numHash; i++) {
			int index = hashes.getIndex(i).hash(value) % size;
			if (!bits.get(index)) {
				return false;
			}
		}
		return true;
	}
}