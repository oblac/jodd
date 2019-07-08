// Copyright (c) 2003-present, Jodd Team (http://jodd.org)
// All rights reserved.
//
// Redistribution and use in source and binary forms, with or without
// modification, are permitted provided that the following conditions are met:
//
// 1. Redistributions of source code must retain the above copyright notice,
// this list of conditions and the following disclaimer.
//
// 2. Redistributions in binary form must reproduce the above copyright
// notice, this list of conditions and the following disclaimer in the
// documentation and/or other materials provided with the distribution.
//
// THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
// AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
// IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
// ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE
// LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
// CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
// SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
// INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
// CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
// ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
// POSSIBILITY OF SUCH DAMAGE.
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
	protected SetMapAdapter(final Map<E, Object> mapImplementation) {
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
	public boolean contains(final Object o) {
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
	public boolean add(final E o) {
		return map.put(o, DUMMY_VALUE) == null;
	}

	/**
	 * Removes the specified element from this set if it is present.
	 *
	 * @param o object to be removed from this set, if present.
	 * @return <code>true</code> if the set contained the specified element.
	 */
	@Override
	public boolean remove(final Object o) {
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