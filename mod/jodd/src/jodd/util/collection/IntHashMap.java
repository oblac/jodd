// Copyright (c) 2003-2012, Jodd Team (jodd.org). All Rights Reserved.

package jodd.util.collection;

import java.io.IOException;
import java.io.Serializable;
import java.util.AbstractCollection;
import java.util.AbstractMap;
import java.util.AbstractSet;
import java.util.Collection;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;

/**
 * A Map that accepts int or Integer keys only. The implementation is based on
 * <code>java.util.HashMap</code>. IntHashMap is about 25% faster.
 *
 * @see java.util.HashMap
 */

public class IntHashMap extends AbstractMap implements Cloneable, Serializable {

	/**
	 * The hash table data.
	 */
	private transient Entry table[];

	/**
	 * The total number of mappings in the hash table.
	 */
	private transient int count;

	/**
	 * The table is rehashed when its size exceeds this threshold. (The value of
	 * this field is (int)(capacity * loadFactor).)
	 */
	private int threshold;

	/**
	 * The load factor for the hashtable.
	 */
	private float loadFactor;

	/**
	 * The number of times this IntHashMap has been structurally modified
	 * Structural modifications are those that change the number of mappings in
	 * the IntHashMap or otherwise modify its internal structure (e.g., rehash).
	 * This field is used to make iterators on Collection-views of the IntHashMap
	 * fail-fast.
	 */
	private transient int modCount;

	/**
	 * Constructs a new, empty map with the specified initial
	 * capacity and the specified load factor.
	 *
	 * @param initialCapacity
	 *                   the initial capacity of the IntHashMap.
	 * @param loadFactor the load factor of the IntHashMap
	 *
	 * @throws IllegalArgumentException
	 *                if the initial capacity is less
	 *                than zero, or if the load factor is non-positive.
	 */
	public IntHashMap(int initialCapacity, float loadFactor) {
		if (initialCapacity < 0) {
			throw new IllegalArgumentException("Invalid initial capacity: "+ initialCapacity);
		}
		if (loadFactor <= 0) {
			throw new IllegalArgumentException("Invalid load factor: "+ loadFactor);
		}
		if (initialCapacity == 0) {
			initialCapacity = 1;
		}
		this.loadFactor = loadFactor;
		table = new Entry[initialCapacity];
		threshold = (int)(initialCapacity * loadFactor);
	}

	/**
	 * Constructs a new, empty map with the specified initial capacity
	 * and default load factor, which is 0.75.
	 *
	 * @param initialCapacity
	 *               the initial capacity of the IntHashMap.
	 *
	 * @throws IllegalArgumentException
	 *                if the initial capacity is less
	 *                than zero.
	 */
	public IntHashMap(int initialCapacity) {
		this(initialCapacity, 0.75f);
	}

	/**
	 * Constructs a new, empty map with a default capacity and load
	 * factor, which is 0.75.
	 */
	public IntHashMap() {
		this(101, 0.75f);
	}

	/**
	 * Constructs a new map with the same mappings as the given map.  The
	 * map is created with a capacity of twice the number of mappings in
	 * the given map or 11 (whichever is greater), and a default load factor,
	 * which is 0.75.
	 */
	public IntHashMap(Map t) {
		this(Math.max(2 * t.size(), 11), 0.75f);
		putAll(t);
	}

	/**
	 * Returns the number of key-value mappings in this map.
	 *
	 * @return the number of key-value mappings in this map.
	 */
	@Override
	public int size() {
		return count;
	}

	/**
	 * Returns <code>true</code> if this map contains no key-value mappings.
	 *
	 * @return <code>true</code> if this map contains no key-value mappings.
	 */
	@Override
	public boolean isEmpty() {
		return count == 0;
	}

	/**
	 * Returns <code>true</code> if this map maps one or more keys to the
	 * specified value.
	 *
	 * @param value  value whose presence in this map is to be tested.
	 *
	 * @return <code>true</code> if this map maps one or more keys to the
	 *         specified value.
	 */
	@Override
	public boolean containsValue(Object value) {
		Entry tab[] = table;
		if (value == null) {
			for (int i = tab.length; i-- > 0 ;) {
				for (Entry e = tab[i] ; e != null ; e = e.next) {
					if (e.value == null) {
						return true;
					}
				}
			}
		} else {
			for (int i = tab.length; i-- > 0 ;) {
				for (Entry e = tab[i]; e != null; e = e.next) {
					if (value.equals(e.value)) {
						return true;
					}
				}
			}
		}
		return false;
	}

	/**
	 * Returns <code>true</code> if this map contains a mapping for the specified
	 * key.
	 *
	 * @param key    key whose presence in this Map is to be tested.
	 *
	 * @return <code>true</code> if this map contains a mapping for the specified
	 *         key.
	 */
	@Override
	public boolean containsKey(Object key) {
		if (key instanceof Number) {
			return containsKey( ((Number)key).intValue() );
		} else {
			return false;
		}
	}

	/**
	 * Returns <code>true</code> if this map contains a mapping for the specified
	 * key.
	 *
	 * @param key    key whose presence in this Map is to be tested.
	 *
	 * @return <code>true</code> if this map contains a mapping for the specified
	 *         key.
	 */
	public boolean containsKey(int key) {
		Entry tab[] = table;

		int index = (key & 0x7FFFFFFF) % tab.length;
		for (Entry e = tab[index]; e != null; e = e.next) {
			if (e.key == key) {
				return true;
			}
		}

		return false;
	}

	/**
	 * Returns the value to which this map maps the specified key. Returns
	 * <code>null</code> if the map contains no mapping for this key. A return
	 * value of <code>null</code> does not <i>necessarily</i> indicate that the
	 * map contains no mapping for the key; it's also possible that the map
	 * explicitly maps the key to <code>null</code>. The <code>containsKey</code>
	 * operation may be used to distinguish these two cases.
	 *
	 * @param key    key whose associated value is to be returned.
	 *
	 * @return the value to which this map maps the specified key.
	 */
	@Override
	public Object get(Object key) {
		if (key instanceof Number) {
			return get( ((Number)key).intValue() );
		} else {
			return null;
		}
	}

	/**
	 * Returns the value to which this map maps the specified key. Returns
	 * <code>null</code> if the map contains no mapping for this key. A return
	 * value of <code>null</code> does not <i>necessarily</i> indicate that the
	 * map contains no mapping for the key; it's also possible that the map
	 * explicitly maps the key to <code>null</code>. The <code>containsKey</code>
	 * operation may be used to distinguish these two cases.
	 *
	 * @param key    key whose associated value is to be returned.
	 *
	 * @return the value to which this map maps the specified key.
	 */
	public Object get(int key) {
		Entry tab[] = table;

		int index = (key & 0x7FFFFFFF) % tab.length;
		for (Entry e = tab[index]; e != null; e = e.next) {
			if (e.key == key) {
				return e.value;
			}
		}

		return null;
	}

	/**
	 * Rehashes the contents of this map into a new <code>IntHashMap</code>
	 * instance with a larger capacity. This method is called automatically when
	 * the number of keys in this map exceeds its capacity and load factor.
	 */
	private void rehash() {
		int oldCapacity = table.length;
		Entry oldMap[] = table;

		int newCapacity = (oldCapacity << 1) + 1;
		Entry newMap[] = new Entry[newCapacity];

		modCount++;
		threshold = (int)(newCapacity * loadFactor);
		table = newMap;

		for (int i = oldCapacity ; i-- > 0 ;) {
			for (Entry old = oldMap[i] ; old != null ; ) {
				Entry e = old;
				old = old.next;

				int index = (e.key & 0x7FFFFFFF) % newCapacity;
				e.next = newMap[index];
				newMap[index] = e;
			}
		}
	}

	/**
	 * Associates the specified value with the specified key in this map. If the
	 * map previously contained a mapping for this key, the old value is
	 * replaced.
	 *
	 * @param key    key with which the specified value is to be associated.
	 * @param value  value to be associated with the specified key.
	 *
	 * @return previous value associated with specified key, or <code>null</code> if
	 *         there was no mapping for key. A <code>null</code> return can also indicate
	 *         that the IntHashMap previously associated <code>null</code> with the
	 *         specified key.
	 */
	@Override
	public Object put(Object key, Object value) {
		if (key instanceof Number) {
			return put( ((Number)key).intValue(), value );
		} else {
			throw new UnsupportedOperationException
			("IntHashMap key must be a number");
		}
	}

	/**
	 * Associates the specified value with the specified key in this map. If the
	 * map previously contained a mapping for this key, the old value is
	 * replaced.
	 *
	 * @param key    key with which the specified value is to be associated.
	 * @param value  value to be associated with the specified key.
	 *
	 * @return previous value associated with specified key, or <code>null</code> if
	 *         there was no mapping for key. A <code>null</code> return can also indicate
	 *         that the IntHashMap previously associated <code>null</code> with the
	 *         specified key.
	 */
	public Object put(int key, Object value) {
		// makes sure the key is not already in the IntHashMap.
		Entry tab[] = table;

		int index = (key & 0x7FFFFFFF) % tab.length;
		for (Entry e = tab[index] ; e != null ; e = e.next) {
			if (e.key == key) {
				Object old = e.value;
				e.value = value;
				return old;
			}
		}

		modCount++;
		if (count >= threshold) {
			// rehash the table if the threshold is exceeded
			rehash();

			tab = table;
			index = (key & 0x7FFFFFFF) % tab.length;
		}

		// creates the new entry.
		tab[index] = new Entry(key, value, tab[index]);
		count++;
		return null;
	}

	/**
	 * Removes the mapping for this key from this map if present.
	 *
	 * @param key    key whose mapping is to be removed from the map.
	 *
	 * @return previous value associated with specified key, or <code>null</code> if
	 *         there was no mapping for key. A <code>null</code> return can also indicate
	 *         that the map previously associated <code>null</code> with the specified
	 *         key.
	 */
	@Override
	public Object remove(Object key) {
		if (key instanceof Number) {
			return remove( ((Number)key).intValue() );
		} else {
			return null;
		}
	}

	/**
	 * Removes the mapping for this key from this map if present.
	 *
	 * @param key    key whose mapping is to be removed from the map.
	 *
	 * @return previous value associated with specified key, or <code>null</code> if
	 *         there was no mapping for key. A <code>null</code> return can also indicate
	 *         that the map previously associated <code>null</code> with the specified
	 *         key.
	 */
	public Object remove(int key) {
		Entry tab[] = table;

		int index = (key & 0x7FFFFFFF) % tab.length;

		for (Entry e = tab[index], prev = null; e != null;
			prev = e, e = e.next) {

			if (e.key == key) {
				modCount++;
				if (prev != null) {
					prev.next = e.next;
				} else {
					tab[index] = e.next;
				}

				count--;
				Object oldValue = e.value;
				e.value = null;
				return oldValue;
			}
		}

		return null;
	}

	/**
	 * Copies all of the mappings from the specified map to this one.
	 * These mappings replace any mappings that this map had for any of the
	 * keys currently in the specified Map.
	 *
	 * @param t      Mappings to be stored in this map.
	 */
	@Override
	public void putAll(Map t) {
		for (Object o : t.entrySet()) {
			Map.Entry e = (Map.Entry) o;
			put(e.getKey(), e.getValue());
		}
	}

	/**
	 * Removes all mappings from this map.
	 */
	@Override
	public void clear() {
		Entry tab[] = table;
		modCount++;
		for (int index = tab.length; --index >= 0; ) {
			tab[index] = null;
		}
		count = 0;
	}

	/**
	 * Returns a shallow copy of this <code>IntHashMap</code> instance: the keys and
	 * values themselves are not cloned.
	 *
	 * @return a shallow copy of this map.
	 */
	@Override
	public Object clone() {
		try {
			IntHashMap t = (IntHashMap)super.clone();
			t.table = new Entry[table.length];
			for (int i = table.length ; i-- > 0 ; ) {
				t.table[i] = (table[i] != null)
							 ? (Entry)table[i].clone() : null;
			}
			t.keySet = null;
			t.entrySet = null;
			t.values = null;
			t.modCount = 0;
			return t;
		} catch (CloneNotSupportedException e) {
			// this shouldn't happen, since we are Cloneable
			throw new InternalError();
		}
	}

	// views
	private transient Set keySet;
	private transient Set entrySet;
	private transient Collection values;

	/**
	 * Returns a set view of the keys contained in this map. The set is backed by
	 * the map, so changes to the map are reflected in the set, and vice-versa.
	 * The set supports element removal, which removes the corresponding mapping
	 * from this map, via the <code>Iterator.remove</code>,
	 * <code>Set.remove</code>, <code>removeAll</code>, <code>retainAll</code>,
	 * and <code>clear</code> operations. It does not support the
	 * <code>add</code> or <code>addAll</code> operations.
	 *
	 * @return a set view of the keys contained in this map.
	 */
	@Override
	public Set keySet() {
		if (keySet == null) {
			keySet = new AbstractSet() {
				@Override
				public Iterator iterator() {
					return new IntHashIterator(KEYS);
				}
				@Override
				public int size() {
					return count;
				}
				@Override
				public boolean contains(Object o) {
					return containsKey(o);
				}
				@Override
				public boolean remove(Object o) {
					return IntHashMap.this.remove(o) != null;
				}
				@Override
				public void clear() {
					IntHashMap.this.clear();
				}
			};
		}
		return keySet;
	}

	/**
	 * Returns a collection view of the values contained in this map. The
	 * collection is backed by the map, so changes to the map are reflected in
	 * the collection, and vice-versa. The collection supports element removal,
	 * which removes the corresponding mapping from this map, via the
	 * <code>Iterator.remove</code>, <code>Collection.remove</code>,
	 * <code>removeAll</code>, <code>retainAll</code>, and <code>clear</code>
	 * operations. It does not support the <code>add</code> or
	 * <code>addAll</code> operations.
	 *
	 * @return a collection view of the values contained in this map.
	 */
	@Override
	public Collection values() {
		if (values==null) {
			values = new AbstractCollection() {
				@Override
				public Iterator iterator() {
					return new IntHashIterator(VALUES);
				}
				@Override
				public int size() {
					return count;
				}
				@Override
				public boolean contains(Object o) {
					return containsValue(o);
				}
				@Override
				public void clear() {
					IntHashMap.this.clear();
				}
			};
		}
		return values;
	}

	/**
	 * Returns a collection view of the mappings contained in this map. Each
	 * element in the returned collection is a <code>Map.Entry</code>. The
	 * collection is backed by the map, so changes to the map are reflected in
	 * the collection, and vice-versa. The collection supports element removal,
	 * which removes the corresponding mapping from the map, via the
	 * <code>Iterator.remove</code>, <code>Collection.remove</code>,
	 * <code>removeAll</code>, <code>retainAll</code>, and <code>clear</code>
	 * operations. It does not support the <code>add</code> or
	 * <code>addAll</code> operations.
	 *
	 * @return a collection view of the mappings contained in this map.
	 * @see java.util.Map.Entry
	 */
	@Override
	public Set entrySet() {
		if (entrySet==null) {
			entrySet = new AbstractSet() {
				@Override
				public Iterator iterator() {
					return new IntHashIterator(ENTRIES);
				}

				@Override
				public boolean contains(Object o) {
					if (!(o instanceof Map.Entry)) {
						return false;
					}
					Map.Entry entry = (Map.Entry)o;
					Object key = entry.getKey();
					Entry tab[] = table;
					int hash = (key==null ? 0 : key.hashCode());
					int index = (hash & 0x7FFFFFFF) % tab.length;

					for (Entry e = tab[index]; e != null; e = e.next) {
						if (e.key == hash && e.equals(entry)) {
							return true;
						}
					}
					return false;
				}

				@Override
				public boolean remove(Object o) {
					if (!(o instanceof Map.Entry)) {
						return false;
					}
					Map.Entry entry = (Map.Entry)o;
					Object key = entry.getKey();
					Entry tab[] = table;
					int hash = (key==null ? 0 : key.hashCode());
					int index = (hash & 0x7FFFFFFF) % tab.length;

					for (Entry e = tab[index], prev = null; e != null;
						prev = e, e = e.next) {
						if (e.key == hash && e.equals(entry)) {
							modCount++;
							if (prev != null) {
								prev.next = e.next;
							} else {
								tab[index] = e.next;
							}

							count--;
							e.value = null;
							return true;
						}
					}
					return false;
				}

				@Override
				public int size() {
					return count;
				}

				@Override
				public void clear() {
					IntHashMap.this.clear();
				}
			};
		}

		return entrySet;
	}

	/**
	 * IntHashMap collision list entry.
	 */
	private static class Entry implements Map.Entry, Cloneable {
		int key;
		Object value;
		Entry next;
		private Integer objectKey;

		Entry(int key, Object value, Entry next) {
			this.key = key;
			this.value = value;
			this.next = next;
		}

		@Override
		protected Object clone() {
			return new Entry(key, value,
							 (next==null ? null : (Entry)next.clone()));
		}

		// Map.Entry Ops

		public Object getKey() {
			return(objectKey != null) ? objectKey :
			(objectKey = new Integer(key));
		}

		public Object getValue() {
			return value;
		}

		public Object setValue(Object value) {
			Object oldValue = this.value;
			this.value = value;
			return oldValue;
		}

		@Override
		public boolean equals(Object o) {
			if (!(o instanceof Map.Entry)) {
				return false;
			}
			Map.Entry e = (Map.Entry)o;

			return(getKey().equals(e.getKey())) &&
			(value==null ? e.getValue()==null : value.equals(e.getValue()));
		}

		@Override
		public int hashCode() {
			return key ^ (value==null ? 0 : value.hashCode());
		}

		@Override
		public String toString() {
			return  Integer.toString(key) + '=' + value;
		}
	}

	// types of Iterators
	private static final int KEYS = 0;
	private static final int VALUES = 1;
	private static final int ENTRIES = 2;

	private class IntHashIterator implements Iterator {
		Entry[] _table = IntHashMap.this.table;
		int index = _table.length;
		Entry entry;
		Entry lastReturned;
		int type;

		/**
		 * The modCount value that the iterator believes that the backing
		 * List should have.  If this expectation is violated, the iterator
		 * has detected concurrent modification.
		 */
		private int expectedModCount = modCount;

		IntHashIterator(int type) {
			this.type = type;
		}

		public boolean hasNext() {
			while (entry == null && index > 0) {
				entry = _table[--index];
			}

			return entry != null;
		}

		public Object next() {
			if (modCount != expectedModCount) {
				throw new ConcurrentModificationException();
			}

			while (entry == null && index > 0) {
				entry = _table[--index];
			}

			if (entry != null) {
				Entry e = lastReturned = entry;
				entry = e.next;
				return type == KEYS ? e.getKey() :
				(type == VALUES ? e.value : e);
			}
			throw new NoSuchElementException();
		}

		public void remove() {
			if (lastReturned == null) {
				throw new IllegalStateException();
			}
			if (modCount != expectedModCount) {
				throw new ConcurrentModificationException();
			}

			Entry[] tab = IntHashMap.this.table;
			int ndx = (lastReturned.key & 0x7FFFFFFF) % tab.length;

			for (Entry e = tab[ndx], prev = null; e != null;
				prev = e, e = e.next) {
				if (e == lastReturned) {
					modCount++;
					expectedModCount++;
					if (prev == null) {
						tab[ndx] = e.next;
					} else {
						prev.next = e.next;
					}
					count--;
					lastReturned = null;
					return;
				}
			}
			throw new ConcurrentModificationException();
		}
	}

	/**
	 * Save the state of the <code>IntHashMap</code> instance to a stream (i.e.,
	 * serialize it).
	 * <p>
	 * Context The <i>capacity</i> of the IntHashMap (the length of the bucket
	 * array) is emitted (int), followed by the <i>size</i> of the IntHashMap
	 * (the number of key-value mappings), followed by the key (Object) and value
	 * (Object) for each key-value mapping represented by the IntHashMap The
	 * key-value mappings are emitted in no particular order.
	 *
	 * @exception IOException
	 */
	private void writeObject(java.io.ObjectOutputStream s) throws IOException {
		// write out the threshold, loadfactor, and any hidden stuff
		s.defaultWriteObject();

		// write out number of buckets
		s.writeInt(table.length);

		// write out size (number of Mappings)
		s.writeInt(count);

		// write out keys and values (alternating)
		for (int index = table.length-1; index >= 0; index--) {
			Entry entry = table[index];

			while (entry != null) {
				s.writeInt(entry.key);
				s.writeObject(entry.value);
				entry = entry.next;
			}
		}
	}

	/**
	 * Reconstitutes the <code>IntHashMap</code> instance from a stream (i.e.,
	 * deserialize it).
	 *
	 * @exception IOException
	 * @exception ClassNotFoundException
	 */
	private void readObject(java.io.ObjectInputStream s) throws IOException, ClassNotFoundException {
		// read in the threshold, loadfactor, and any hidden stuff
		s.defaultReadObject();

		// read in number of buckets and allocate the bucket array;
		int numBuckets = s.readInt();
		table = new Entry[numBuckets];

		// read in size (number of Mappings)
		int size = s.readInt();

		// read the keys and values, and put the mappings in the IntHashMap
		for (int i=0; i<size; i++) {
			int key = s.readInt();
			Object value = s.readObject();
			put(key, value);
		}
	}

	int capacity() {
		return table.length;
	}

	float loadFactor() {
		return loadFactor;
	}
}
