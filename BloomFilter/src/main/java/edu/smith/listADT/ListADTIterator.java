package edu.smith.listADT;

import java.util.Iterator;

/**
 * This is an object that walks/loops/iterates/traverses through a ListADT type.
 * 
 * @author jfoley
 *
 * @param <ItemType>
 */
public class ListADTIterator<ItemType> implements Iterator<ItemType> {
	/**
	 * Which list are we walking through?
	 */
	ListADT<ItemType> source;
	/**
	 * How far are we?
	 */
	int i = 0;

	/**
	 * Construct this kind of object from a ListADT object.
	 * 
	 * @param list - the list to loop/traverse/iterate over.
	 */
	public ListADTIterator(ListADT<ItemType> list) {
		this.source = list;
	}

	/**
	 * Does this iterator have more data?
	 */
	@Override
	public boolean hasNext() {
		return i < source.size();
	}

	/**
	 * Get me the next item to use in my loop.
	 */
	@Override
	public ItemType next() {
		return source.getIndex(i++);
	}
}