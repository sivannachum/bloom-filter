package edu.smith.checkSpelling;

import java.util.AbstractSet;
import java.util.BitSet;
import java.util.Iterator;

import edu.smith.bloom.UniversalHash;
import edu.smith.listADT.JavaList;
import edu.smith.listADT.ListADT;

public class BloomFilter extends AbstractSet<String>{
	private int size;
	private BitSet bits;
	private int numHash;
	private ListADT<UniversalHash<String>> hashes;
	public BloomFilter(int size, int numHash) {
		this.size = size;
		bits = new BitSet(size);
		this.numHash = numHash;
		hashes = new JavaList<UniversalHash<String>>();
		for (int i=0; i<numHash; i++) {
			hashes.addBack(new UniversalHash<>());
		}
	}
	public void insert(String value) {
		for (int i = 0; i < numHash; i++) {
			int index = hashes.getIndex(i).hash(value) % size;
			bits.set(index, true);
		}
	}
	public boolean contains(String value) {
		for (int i = 0; i < numHash; i++) {
			int index = hashes.getIndex(i).hash(value) % size;
			if (!bits.get(index)) {
				return false;
			}
		}
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
		// cannot return how many items are in the filter because there is no way to know
		return size;
	}
}
