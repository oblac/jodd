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

import java.util.Arrays;

/**
 * Storage for holding classes keys and values.
 * It is <b>NOT</b> a <code>Map</code> instance. It is very fast
 * on un-synchronized lookups, faster then <code>HashMap</code>.
 * Uses identity for checking if <code>Class</code> keys are equal.
 * <p>
 * The initial version of this class was provided by @zqq90, from
 * <a href="https://github.com/zqq90/webit-script/">WebIt-script</a>
 * project. Thank you!
 */
public final class ClassMap<V> {

	private static final int MAXIMUM_CAPACITY = 1 << 29;

	private Entry<V>[] table;
	private int threshold;
	private int size;

	/**
	 * Creates new map with given initial capacity.
	 */
	@SuppressWarnings("unchecked")
	public ClassMap(int initialCapacity) {
		int initlen;
		if (initialCapacity > MAXIMUM_CAPACITY) {
			initlen = MAXIMUM_CAPACITY;
		}
		else {
			initlen = 16;
			while (initlen < initialCapacity) {
				initlen <<= 1;
			}
		}
		this.table = new Entry[initlen];
		this.threshold = (int) (initlen * 0.75f);
	}

	public ClassMap() {
		this(64);
	}

	/**
	 * Returns total number of stored classes.
	 */
	public int size() {
		return size;
	}

	/**
	 * Returns a value associated to a key in unsafe, but very fast way.
	 */
	public V unsafeGet(final Class key) {
		final Entry<V>[] tab;
		Entry<V> e = (tab = table)[key.hashCode() & (tab.length - 1)];

		while (e != null) {
			if (key == e.key) {
				return e.value;
			}
			e = e.next;
		}
		return null;
	}

	/**
	 * Returns a value associated to a key in thread-safe way.
	 */
	public synchronized V get(Class key) {
		return unsafeGet(key);
	}

	@SuppressWarnings("unchecked")
	private void resize() {
		if (size < threshold) {
			return;
		}
		final Entry<V>[] oldTable = table;
		final int oldCapacity = oldTable.length;

		final int newCapacity = oldCapacity << 1;
		if (newCapacity > MAXIMUM_CAPACITY) {
			if (threshold == MAXIMUM_CAPACITY - 1) {
				throw new IllegalStateException("Capacity exhausted");
			}
			threshold = MAXIMUM_CAPACITY - 1;
			return;
		}

		final int newMark = newCapacity - 1;
		final Entry<V>[] newTable = new Entry[newCapacity];

		for (int i = oldCapacity; i-- > 0; ) {
			int index;
			for (Entry<V> old = oldTable[i], e; old != null; ) {
				e = old;
				old = old.next;

				index = e.id & newMark;
				e.next = newTable[index];
				newTable[index] = e;
			}
		}

		this.threshold = (int) (newCapacity * 0.75f);

		// must be last
		this.table = newTable;
	}

	/**
	 * Associates the specified value with the specified Class in this map.
	 * Returns the previous value associated with key, or <code>null</code>
	 * if there was no mapping for key.
	 */
	@SuppressWarnings("unchecked")
	public synchronized V put(Class key, V value) {
		final int id;
		int index;

		Entry<V>[] tab;
		Entry<V> e = (tab = table)[index = (id = key.hashCode()) & (tab.length - 1)];

		while (e != null) {
			if (key == e.key) {				// identity check
				// key found, replace
				V existing = e.value;
				e.value = value;
				return existing;
			}

			e = e.next;
		}

		if (size >= threshold) {
			resize();
			tab = table;
			index = id & (tab.length - 1);
		}

		// creates the new entry
		tab[index] = new Entry(id, key, value, tab[index]);
		size++;

		return null;
	}

	/**
	 * Clears the class map.
	 */
	public synchronized void clear() {
		size = 0;
		Arrays.fill(table, null);
	}

	/**
	 * Maps entry.
	 */
	private static final class Entry<V> {
		final int id;
		final Class key;
		V value;
		Entry<V> next;

		private Entry(int id, Class key, V value, Entry<V> next) {
			this.value = value;
			this.id = id;
			this.key = key;
			this.next = next;
		}
	}
}