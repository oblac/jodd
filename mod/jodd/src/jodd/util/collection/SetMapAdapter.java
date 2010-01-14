// Copyright (c) 2003-2010, Jodd Team (jodd.org). All Rights Reserved.
package jodd.util.collection;

import java.util.AbstractSet;
import java.util.Iterator;
import java.util.Map;

/**
 * Map adapter for a set provides an easy way to have a Set from various map implementations.
 */
public abstract class SetMapAdapter<E> extends AbstractSet<E> {

	protected Map<E, Object> map;

	// Dummy value to associate with an Object in the backing Map
	private static final Object DUMMY_VALUE = new Object();

	/**
	 * Constructs a new, empty set; the backing <code>HashMap</code> instance has
	 * default initial capacity (16) and load factor (0.75).
	 */
	protected SetMapAdapter(Map<E, Object> mapImplementation) {
		this.map = mapImplementation;
	}


	/**
	 * Returns an iterator over the elements in this set.  The elements
	 * are returned in no particular order.
	 *
	 * @return an Iterator over the elements in this set.
	 */
	@Override
	public Iterator<E> iterator() {
		return map.keySet().iterator();
	}

	/**
	 * Returns the number of elements in this set (its cardinality).
	 */
	@Override
	public int size() {
		return map.size();
	}

	/**
	 * Returns <code>true</code> if this set contains no elements.
	 */
	@Override
	public boolean isEmpty() {
		return map.isEmpty();
	}

	/**
	 * Returns <code>true</code> if this set contains the specified element.
	 *
	 * @param o element whose presence in this set is to be tested.
	 * @return <code>true</code> if this set contains the specified element.
	 */
	@SuppressWarnings({"SuspiciousMethodCalls"})
	@Override
	public boolean contains(Object o) {
		return map.containsKey(o);
	}

	/**
	 * Adds the specified element to this set if it is not already
	 * present.
	 *
	 * @param o element to be added to this set.
	 * @return <code>true</code> if the set did not already contain the specified
	 *         element.
	 */
	@Override
	public boolean add(E o) {
		return map.put(o, DUMMY_VALUE) == null;
	}

	/**
	 * Removes the specified element from this set if it is present.
	 *
	 * @param o object to be removed from this set, if present.
	 * @return <code>true</code> if the set contained the specified element.
	 */
	@Override
	public boolean remove(Object o) {
		return map.remove(o) == DUMMY_VALUE;
	}

	/**
	 * Removes all of the elements from this set.
	 */
	@Override
	public void clear() {
		map.clear();
	}

}