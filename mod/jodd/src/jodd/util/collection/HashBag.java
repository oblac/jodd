// Copyright (c) 2003-2009, Jodd Team (jodd.org). All Rights Reserved.

package jodd.util.collection;

import jodd.mutable.MutableInteger;

import java.lang.reflect.Array;
import java.util.Collection;
import java.util.ConcurrentModificationException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * HashBag implementation of a {@link Bag}
 */
public class HashBag<E> implements Bag<E> {

	protected transient Map<E, MutableInteger> map; // map for storing data
	protected int size;								// current bag size
	private transient int modCount;					// modification count for fail fast iterators

	public HashBag() {
		super();
		map = new HashMap<E, MutableInteger>();
	}

	public HashBag(Collection<? extends E> coll) {
		this();
		addAll(coll);
	}

	// ---------------------------------------------------------------- size
	/**
	 * Returns the number of elements in the bag.
	 */
	public int size() {
		return size;
	}

	/**
	 * Returns <code>true</code> if bag is empty.
	 */
	public boolean isEmpty() {
		return map.isEmpty();
	}

	/**
	 * Returns the number of occurrence of the given element in bag.
	 */
	@SuppressWarnings({"SuspiciousMethodCalls"})
	public int getCount(Object object) {
		MutableInteger count = map.get(object);
		if (count != null) {
			return count.value;
		}
		return 0;
	}

	// ---------------------------------------------------------------- contains

	/**
	 * Determines if the bag contains the given element.

	 * @return <code>true</code> if the bag contains the given element
	 */
	@SuppressWarnings({"SuspiciousMethodCalls"})
	public boolean contains(Object object) {
		return map.containsKey(object);
	}

	/**
	 * Determines if the bag contains all the collection elements..

	 * @return <code>true</code> if the Bag contains all the collection elements
	 */
	@SuppressWarnings({"unchecked"})
	public boolean containsAll(Collection<?> coll) {
		if (coll instanceof Bag) {
			boolean result = true;
			for (Object current : ((Bag) coll).uniqueSet()) {
				boolean contains = getCount(current) >= ((Bag)coll).getCount(current);
				result = result && contains;
			}
			return result;
		}
		boolean result = true;
		for (Object current : coll) {
			boolean contains = getCount(current) >= 1;
			result = result && contains;
		}
		return result;
	}

	/**
	 * Returns <code>true</code> if the bag contains all elements in
	 * the given bag, respecting cardinality.

	 * @return <code>true</code> if the Bag contains all the bag elements
	 */
	boolean containsAll(Bag other) {
		boolean result = true;
		for (Object current : other.uniqueSet()) {
			boolean contains = getCount(current) >= other.getCount(current);
			result = result && contains;
		}
		return result;
	}

	// ---------------------------------------------------------------- iterator

	/**
	 * Returns an iterator over the bag elements.
	 * Elements present in the Bag more than once will be returned repeatedly.
	 */
	public Iterator<E> iterator() {
		return new BagIterator<E>(this);
	}

	/**
	 * Inner class iterator for the Bag.
	 */
	class BagIterator<E> implements Iterator<E> {
		private HashBag<E> parent;
		private Iterator<java.util.Map.Entry<E, MutableInteger>> entryIterator;
		private Map.Entry<E, MutableInteger> current;
		private int itemCount;
		private final int mods;
		private boolean canRemove;

		BagIterator(HashBag<E> parent) {
			this.parent = parent;
			this.entryIterator = parent.map.entrySet().iterator();
			this.current = null;
			this.mods = parent.modCount;
			this.canRemove = false;
		}

		public boolean hasNext() {
			return (itemCount > 0 || entryIterator.hasNext());
		}

		public E next() {
			if (parent.modCount != mods) {
				throw new ConcurrentModificationException();
			}
			if (itemCount == 0) {
				current = entryIterator.next();
				itemCount = (current.getValue()).value;
			}
			canRemove = true;
			itemCount--;
			return current.getKey();
		}

		public void remove() {
			if (parent.modCount != mods) {
				throw new ConcurrentModificationException();
			}
			if (canRemove == false) {
				throw new IllegalStateException();
			}
			MutableInteger mut = current.getValue();
			if (mut.value > 1) {
				mut.value--;
			} else {
				entryIterator.remove();
			}
			parent.size--;
			canRemove = false;
		}
	}

	// ---------------------------------------------------------------- add
	/**
	 * Adds a new element to the bag, incrementing its count in the underlying map.

	 * @return <code>true</code> if the object was not already in the bag
	 */
	public boolean add(E object) {
		return add(object, 1);
	}

	/**
	 * Adds a new element to the bag, incrementing its count in the map.

	 * @return <code>true</code> if the object was not already in the bag
	 */
	public boolean add(E object, int copies) {
		if (copies <= 0) {
			throw new IllegalArgumentException("Invalid number of bag element copies (" + copies + ')');
		}
		modCount++;
		MutableInteger mut = map.get(object);
		size += copies;
		if (mut == null) {
			map.put(object, new MutableInteger(copies));
			return true;
		} else {
			mut.value += copies;
			return false;
		}
	}

	/**
	 * Invokes {@link #add(Object)} for each element in the given collection.
	 *
	 * @return <code>true</code> if this call changed the bag
	 */
	public boolean addAll(Collection<? extends E> coll) {
		boolean changed = false;
		for (E c : coll) {
			boolean added = add(c);
			changed = changed || added;
		}
		return changed;
	}

	// ---------------------------------------------------------------- remove

	/**
	 * Clears the bag by clearing the underlying map.
	 */
	public void clear() {
		if (size == 0) {
			return;
		}
		modCount++;
		map.clear();
		size = 0;
	}

	/**
	 * Removes all copies of the specified object from the bag.
	 *
	 * @return <code>true</code> if the bag changed
	 */
	@SuppressWarnings({"SuspiciousMethodCalls"})
	public boolean remove(Object object) {
		MutableInteger mut = map.get(object);
		if (mut == null) {
			return false;
		}
		modCount++;
		map.remove(object);
		size -= mut.value;
		return true;
	}

	/**
	 * Removes a specified number of copies of an object from the bag.
	 *
	 * @return <code>true</code> if the bag changed
	 */
	@SuppressWarnings({"SuspiciousMethodCalls"})
	public boolean remove(Object object, int copies) {
		if (copies <= 0) {
			throw new IllegalArgumentException("Invalid number of bag element copies (" + copies + ')');
		}
		MutableInteger mut = map.get(object);
		if (mut == null) {
			return false;
		}
		modCount++;
		if (copies < mut.value) {
			mut.value -= copies;
			size -= copies;
		} else {
			map.remove(object);
			size -= mut.value;
		}
		return true;
	}

	/**
	 * Removes objects from the bag according to their count in the specified collection.
	 * @return <code>true</code> if the bag changed
	 */
	public boolean removeAll(Collection<?> coll) {
		boolean result = false;
		if (coll != null) {
			for (Object c : coll) {
				boolean changed = remove(c, 1);
				result = result || changed;
			}
		}
		return result;
	}


	// ---------------------------------------------------------------- retain

	/**
	 * Remove any members of the bag that are not in the given
	 * bag, respecting cardinality.
	 *
	 * @return <code>true</code> if this call changed the collection
	 */
	@SuppressWarnings({"unchecked"})
	public boolean retainAll(Collection<?> coll) {
		if (coll instanceof Bag) {
			boolean result = false;
			Bag excess = new HashBag();
			for (E current : map.keySet()) {
				int myCount = getCount(current);
				int otherCount = ((Bag) coll).getCount(current);
				if (otherCount >= 1 && otherCount <= myCount) {
					excess.add(current, myCount - otherCount);
				} else {
					excess.add(current, myCount);
				}
			}
			if (!excess.isEmpty()) {
				result = removeAll(excess);
			}
			return result;
		}

		boolean result = false;
		Bag excess = new HashBag();
		for (E current : map.keySet()) {
			int myCount = getCount(current);
			excess.add(current, myCount - 1);
		}
		if (!excess.isEmpty()) {
			result = removeAll(excess);
		}
		return result;
	}

	/**
	 * Remove any members of the bag that are not in the given
	 * bag, respecting cardinality.
	 *
	 * @return <code>true</code> if this call changed the collection
	 */
	@SuppressWarnings({"unchecked"})
	boolean retainAll(Bag other) {
		boolean result = false;
		Bag excess = new HashBag();
		for (E current : map.keySet()) {
			int myCount = getCount(current);
			int otherCount = other.getCount(current);
			if (otherCount >= 1 && otherCount <= myCount) {
				excess.add(current, myCount - otherCount);
			} else {
				excess.add(current, myCount);
			}
		}
		if (!excess.isEmpty()) {
			result = removeAll(excess);
		}
		return result;
	}



	// ---------------------------------------------------------------- convert

	public Set<E> uniqueSet() {
        return map.keySet();
    }

	/**
	 * Returns an array of all of bag's elements.
	 */
	public Object[] toArray() {
		Object[] result = new Object[size()];
		int i = 0;
		for (E current : map.keySet()) {
			for (int index = getCount(current); index > 0; index--) {
				result[i++] = current;
			}
		}
		return result;
	}

	/**
	 * Populates an array with all of this bag's elements.
	 */

	@SuppressWarnings({"unchecked"})
	public <T> T[] toArray(T[] array) {
		int size = size();
		if (array.length < size) {
			array = (T[]) Array.newInstance(array.getClass().getComponentType(), size);
		}
		int i = 0;
		for (E current : map.keySet()) {
			for (int index = getCount(current); index > 0; index--) {
				array[i++] = (T) current;
			}
		}
		while (array.length > size) {
			array[size] = null;
			size++;
		}
		return array;
	}

	//-----------------------------------------------------------------------

	/**
	 * Compares bag to another.
	 * This Bag equals another Bag if it contains the same number of occurrences of
	 * the same elements.

	 * @return <code>true</code> if equal
	 */
	@Override
	public boolean equals(Object object) {
		if (object == this) {
			return true;
		}
		if (object instanceof Bag == false) {
			return false;
		}
		Bag other = (Bag) object;
		if (other.size() != size()) {
			return false;
		}
		for (E element : map.keySet()) {
			if (other.getCount(element) != getCount(element)) {
				return false;
			}
		}
		return true;
	}

	/**
	 * Gets a hash code for the Bag compatible with the definition of equals.
	 * The hash code is defined as the sum total of a hash code for each element.
	 * The per element hash code is defined as
	 * <code>(e==null ? 0 : e.hashCode()) ^ noOccurances)</code>.
	 * This hash code is compatible with the Set interface.
	 */
	@Override
	public int hashCode() {
		int total = 0;
		for (Map.Entry<E, MutableInteger> entry : map.entrySet()) {
			Object element = entry.getKey();
			MutableInteger count = entry.getValue();
			total += (element == null ? 0 : element.hashCode()) ^ count.value;
		}
		return total;
	}

	/**
	 * Implement a toString() method suitable for debugging.
	 */
	@Override
	public String toString() {
		if (size() == 0) {
			return "[]";
		}
		StringBuilder buf = new StringBuilder();
		buf.append('[');
		Iterator<E> it = uniqueSet().iterator();
		while (it.hasNext()) {
			Object current = it.next();
			int count = getCount(current);
			buf.append(count);
			buf.append(':');
			buf.append(current);
			if (it.hasNext()) {
				buf.append(',');
			}
		}
		buf.append(']');
		return buf.toString();
	}

}
