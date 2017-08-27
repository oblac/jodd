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

package jodd.http;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.TreeSet;

/**
 * General purpose HTTP multi-map. It's optimized Linked-HashMap, designed for
 * small number of items and <code>String</code> non-null keys. It stores keys
 * in case-sensitive way, but, by default, you can read them in case-insensitive
 * way.
 */
public class HttpMultiMap<V> implements Iterable<Map.Entry<String, V>>  {

	private static final int BUCKET_SIZE = 31;
	private final boolean caseSensitive;

	@SuppressWarnings("unchecked")
	private final MapEntry<V>[] entries = new MapEntry[BUCKET_SIZE + 1];
	private final MapEntry<V> head = new MapEntry<>(-1, null, null);

	/**
	 * Creates new case-insensitive multimap.
	 */
	public static <T> HttpMultiMap<T> newCaseInsensitiveMap() {
		return new HttpMultiMap<>(false);
	}
	/**
	 * Creates new case-insensitive map.
	 */
	public static <T> HttpMultiMap<T> newCaseSensitiveMap() {
		return new HttpMultiMap<>(true);
	}

	protected HttpMultiMap(boolean caseSensitive) {
		head.before = head.after = head;
		this.caseSensitive = caseSensitive;
	}

	/**
	 * Calculates hash value of the input string.
	 */
	private int hash(String name) {
		int h = 0;
		for (int i = name.length() - 1; i >= 0; i--) {
			char c = name.charAt(i);
			if (!caseSensitive) {
				if (c >= 'A' && c <= 'Z') {
					c += 32;
				}
			}
			h = 31 * h + c;
		}

		if (h > 0) {
			return h;
		}
		if (h == Integer.MIN_VALUE) {
			return Integer.MAX_VALUE;
		}
		return -h;
	}

	/**
	 * Calculates bucket index from the hash.
	 */
	private static int index(int hash) {
		return hash & BUCKET_SIZE;
	}

	/**
	 * Returns <code>true</code> if two names are the same.
	 */
	private boolean eq(String name1, String name2) {
		int nameLen = name1.length();
		if (nameLen != name2.length()) {
			return false;
		}

		for (int i = nameLen - 1; i >= 0; i--) {
			char c1 = name1.charAt(i);
			char c2 = name2.charAt(i);

			if (c1 != c2) {
				if (caseSensitive) {
					return false;
				}
				if (c1 >= 'A' && c1 <= 'Z') {
					c1 += 32;
				}
				if (c2 >= 'A' && c2 <= 'Z') {
					c2 += 32;
				}
				if (c1 != c2) {
					return false;
				}
			}
		}
		return true;
	}

	// ---------------------------------------------------------------- basic

	/**
	 * Returns the number of keys. This is not the number of all elements.
	 * Not optimized.
	 */
	public int size() {
		return names().size();
	}

	/**
	 * Clears the map.
	 */
	public HttpMultiMap<V> clear() {
		for (int i = 0; i < entries.length; i++) {
			entries[i] = null;
		}
		head.before = head.after = head;
		return this;
	}

	/**
	 * Returns <code>true</code> if name exist.
	 */
	public boolean contains(String name) {
		return getEntry(name) != null;
	}

	/**
	 * Returns <code>true</code> if map is empty.
	 */
	public boolean isEmpty() {
		return head == head.after;
	}

	public String toString() {
		StringBuilder sb = new StringBuilder();
		for (Map.Entry<String, V> entry : this) {
			sb.append(entry).append('\n');
		}
		return sb.toString();
	}

	// ---------------------------------------------------------------- set/add

	private HttpMultiMap<V> _set(Iterable<Map.Entry<String, V>> map) {
		clear();
		for (Map.Entry<String, V> entry : map) {
			add(entry.getKey(), entry.getValue());
		}
		return this;
	}

	public HttpMultiMap<V> setAll(HttpMultiMap<V> multiMap) {
		return _set(multiMap);
	}

	public HttpMultiMap<V> setAll(Map<String, V> map) {
		return _set(map.entrySet());
	}

	public HttpMultiMap<V> set(final String name, final V value) {
		int h = hash(name);
		int i = index(h);
		_remove(h, i, name);
		_add(h, i, name, value);
		return this;
	}

	public HttpMultiMap<V> setAll(final String name, final Iterable<V> values) {
		int h = hash(name);
		int i = index(h);

		_remove(h, i, name);
		for (V v : values) {
			_add(h, i, name, v);
		}

		return this;
	}

	public HttpMultiMap<V> add(final String name, final V value) {
		int h = hash(name);
		int i = index(h);
		_add(h, i, name, value);
		return this;
	}

	public HttpMultiMap<V> addAll(String name, Iterable<V> values) {
		int h = hash(name);
		int i = index(h);
		for (V value : values) {
			_add(h, i, name, value);
		}
		return this;
	}

	public HttpMultiMap<V> addAll(HttpMultiMap<V> map) {
		for (Map.Entry<String, V> entry : map.entries()) {
			add(entry.getKey(), entry.getValue());
		}
		return this;
	}

	public HttpMultiMap<V> addAll(Map<String, V> map) {
		for (Map.Entry<String, V> entry : map.entrySet()) {
			add(entry.getKey(), entry.getValue());
		}
		return this;
	}

	private void _add(final int hash, final int index, final String name, final V value) {
		// update the hash table
		MapEntry<V> e = entries[index];
		MapEntry<V> newEntry;
		entries[index] = newEntry = new MapEntry<>(hash, name, value);
		newEntry.next = e;

		// update the linked list
		newEntry.addBefore(head);
	}

	// ---------------------------------------------------------------- remove

	public HttpMultiMap<V> remove(final String name) {
		int h = hash(name);
		int i = index(h);
		_remove(h, i, name);
		return this;
	}

	private void _remove(final int hash, final int index, String name) {
		MapEntry<V> e = entries[index];
		if (e == null) {
			return;
		}

		for (; ; ) {
			if (e.hash == hash && eq(name, e.key)) {
				e.remove();
				MapEntry<V> next = e.next;
				if (next != null) {
					entries[index] = next;
					e = next;
				}
				else {
					entries[index] = null;
					return;
				}
			}
			else {
				break;
			}
		}

		for (; ; ) {
			MapEntry<V> next = e.next;
			if (next == null) {
				break;
			}
			if (next.hash == hash && eq(name, next.key)) {
				e.next = next.next;
				next.remove();
			}
			else {
				e = next;
			}
		}
	}

	// ---------------------------------------------------------------- get

	/**
	 * Returns the first value from the map associated with the name.
	 * Returns <code>null</code> if name does not exist or
	 * if associated value is <code>null</code>.
	 */
	public V get(final String name) {
		Map.Entry<String, V> entry = getEntry(name);

		if (entry == null) {
			return null;
		}
		return entry.getValue();
	}

	/**
	 * Returns first entry for given name. Returns <code>null</code> if entry
	 * does not exist.
	 */
	public Map.Entry<String, V> getEntry(final String name) {
		int h = hash(name);
		int i = index(h);
		MapEntry<V> e = entries[i];
		while (e != null) {
			if (e.hash == h && eq(name, e.key)) {
				return e;
			}

			e = e.next;
		}
		return null;
	}

	/**
	 * Returns all values associated with the name.
	 */
	public List<V> getAll(final String name) {
		LinkedList<V> values = new LinkedList<>();

		int h = hash(name);
		int i = index(h);
		MapEntry<V> e = entries[i];
		while (e != null) {
			if (e.hash == h && eq(name, e.key)) {
				values.addFirst(e.getValue());
			}
			e = e.next;
		}
		return values;
	}

	// ---------------------------------------------------------------- iterate

	/**
	 * Returns iterator of all entries.
	 */
	public Iterator<Map.Entry<String, V>> iterator() {
		final MapEntry[] e = {head.after};

		return new Iterator<Map.Entry<String, V>>() {
			@Override
			public boolean hasNext() {
				return e[0] != head;
			}

			@Override
			@SuppressWarnings("unchecked")
			public Map.Entry<String, V> next() {
				if (!hasNext()) {
					throw new NoSuchElementException("No next() entry in the iteration");
				}
				MapEntry<V> next = e[0];
				e[0] = e[0].after;
				return next;
			}

			@Override
			public void remove() {
				throw new UnsupportedOperationException();
			}
		};
	}

	public Set<String> names() {
		Set<String> names = new TreeSet<>(caseSensitive ? null : String.CASE_INSENSITIVE_ORDER);

		MapEntry e = head.after;
		while (e != head) {
			names.add(e.getKey());
			e = e.after;
		}
		return names;
	}

	/**
	 * Returns all the entries of this map. Case sensitivity does not influence
	 * the returned list, it always contains all of the values.
	 */
	public List<Map.Entry<String, V>> entries() {
		List<Map.Entry<String, V>> all = new LinkedList<>();

		MapEntry<V> e = head.after;
		while (e != head) {
			all.add(e);
			e = e.after;
		}
		return all;
	}

	private static final class MapEntry<V> implements Map.Entry<String, V> {
		final int hash;
		final String key;
		V value;
		MapEntry<V> next;
		MapEntry<V> before, after;

		private MapEntry(int hash, String key, V value) {
			this.hash = hash;
			this.key = key;
			this.value = value;
		}

		void remove() {
			before.after = after;
			after.before = before;
		}

		void addBefore(MapEntry<V> e) {
			after = e;
			before = e.before;
			before.after = this;
			after.before = this;
		}

		public String getKey() {
			return key;
		}

		public V getValue() {
			return value;
		}

		public V setValue(V value) {
			V oldValue = this.value;
			this.value = value;
			return oldValue;
		}

		public String toString() {
			return getKey() + ": " + getValue();
		}
	}
}
