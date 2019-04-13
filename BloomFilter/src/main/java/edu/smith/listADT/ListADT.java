package edu.smith.listADT;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Random;

import edu.smith.listErrors.*;

/**
 * This is an abstract view of a List data structure.
 * 
 * @author jfoley
 *
 * @param <ItemType> - the type of the item stored in this list.
 */
public abstract class ListADT<ItemType> implements Iterable<ItemType> {

	/**
	 * Is this list of size zero? Might be easier than counting all the elements.
	 * @return true if this list is empty.
	 */
	public abstract boolean isEmpty();

	/**
	 * The size of this list.
	 * 
	 * @return the size of the list or number of items.
	 */
	public abstract int size();

	/**
	 * Set the item stored at the given index to value.
	 * 
	 * @param index - the index; from 0 to size() inclusive.
	 * @param value - the value to put in the list.
	 */
	public abstract void setIndex(int index, ItemType value);

	/**
	 * Get the value at the given index.
	 * 
	 * @param index - the item index to retrieve.
	 * @return the value stored at that position.
	 */
	public abstract ItemType getIndex(int index);

	/**
	 * Get the first item in the list.
	 * 
	 * @return this.getIndex(0);
	 */
	public abstract ItemType getFront();

	/**
	 * Get the last item in the list.
	 * 
	 * @return this.getIndex(this.size()-1);
	 */
	public abstract ItemType getBack();

	/**
	 * Add an item with value before the item at the given index.
	 * 
	 * @param index - a number from 0 to size (inclusive).
	 * @param value - the value to insert into the list.
	 */
	public abstract void addIndex(int index, ItemType value);

	/**
	 * Add value to the front of the list.
	 * 
	 * @param value - the item to add.
	 */
	public abstract void addFront(ItemType value);

	/**
	 * Add value to the back of hte list.
	 * 
	 * @param value - the item to add.
	 */
	public abstract void addBack(ItemType value);

	/**
	 * Remove the item from the list at the given index.
	 * 
	 * @param index - a number from 0 to size (exclusive).
	 * @return the value removed.
	 */
	public abstract ItemType removeIndex(int index);

	/**
	 * Remove the item from the back of the list.
	 * 
	 * @return the value removed.
	 */
	public abstract ItemType removeBack();

	/**
	 * Remove the item from the front of the list.
	 * 
	 * @return the value removed.
	 */
	public abstract ItemType removeFront();

	/**
	 * Java requires this method for it's "for (ItemType x : list) { }" loop.
	 */
	public Iterator<ItemType> iterator() {
		return new ListADTIterator<>(this);
	}
	
	/**
	 * Convert this to a Java data structure; probably useful for unit-test errors.
	 * @return - a Java List object.
	 */
	public List<ItemType> toJava() {
		List<ItemType> output = new ArrayList<>();
		for (ItemType x : this) {
			output.add(x);
		}
		return output;
	}
	
	/**
	 * If this list is empty, throw an error; useful for implementing classes.
	 */
	protected void checkNotEmpty() {
		if (this.isEmpty()) {
			throw new EmptyListError();
		}
	}
	
	/**
	 * Check the index for a get/set/remove method.
	 * Not for LinkedList classes!
	 */
	protected void checkExclusiveIndex(int index) {
		if (index < 0) {
			throw new BadIndexError(index);
		}
		if (index >= size()) {
			throw new BadIndexError(index);
		}
	}
	
	/**
	 * Check the index for an add method.
	 * Not for LinkedList classes!
	 */
	protected void checkInclusiveIndex(int index) {
		if (index < 0) {
			throw new BadIndexError(index);
		}
		if (index > size()) {
			throw new BadIndexError(index);
		}
	}
	
	@Override
	public String toString() {
		StringBuilder out = new StringBuilder();
		out.append('[');
		boolean first = true;
		for (ItemType x : this) {
			if (first) {
				first = false;
			} else {
				out.append(", ");
			}
			out.append(x);
		}
		out.append(']');
		return out.toString();
	}
	
	@SuppressWarnings("rawtypes")
	@Override
	public boolean equals(Object other) {
		if (other instanceof List) {
			return Objects.equals(this.toJava(), other);
		} else if (other instanceof ListADT) {
			return Objects.equals(this.toJava(), ((ListADT) other).toJava());
		} else {
			return false;
		}
	}
	
	/**
	 * Swap items in a list!
	 * @param i swap the item at this index with the one at j.
	 * @param j swap the item at this index with the one at i.
	 */
	public void swap(int i, int j) {
		ItemType obj = this.getIndex(i);
		this.setIndex(i, this.getIndex(j));
		this.setIndex(j, obj);
	}
	
	/**
	 * Fisher-Yates Shuffle!
	 * 
	 * See Wikipedia.
	 * 
	 * @param rand - the random number generator to use.
	 */
	public void shuffle(Random rand) {
		for (int i=this.size()-1; i>=1; i--) {
			int j = rand.nextInt(i+1);
			this.swap(i, j);
		}
	}
	
	/**
	 * Add many things to this list.
	 * @param other - the list to copy from.
	 */
	public void addAll(ListADT<ItemType> other) {
		for (ItemType t : other) {
			this.addBack(t);
		}
	}
}