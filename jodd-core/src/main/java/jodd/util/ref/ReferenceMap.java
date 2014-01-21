// Copyright (c) 2003-2014, Jodd Team (jodd.org). All Rights Reserved.

package jodd.util.ref;

import static jodd.util.ref.ReferenceType.STRONG;
import jodd.util.StringPool;

import java.lang.ref.Reference;
import java.lang.ref.SoftReference;
import java.lang.ref.WeakReference;
import java.lang.ref.ReferenceQueue;
import java.util.AbstractMap;
import java.util.AbstractSet;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Concurrent hash map that wraps keys and/or values in SOFT or WEAK references.
 * Does not support <code>null</code> keys or values. Uses identity equality for
 * weak and soft keys.
 *
 * @author crazybob@google.com (Bob Lee)
 * @author fry@google.com (Charles Fry)
 * @author igor.spasic@gmail.com
 */
@SuppressWarnings("unchecked")
public class ReferenceMap<K, V> extends AbstractMap<K, V> implements ConcurrentMap<K, V> {

	protected transient ConcurrentMap<Object, Object> delegate;

	protected final ReferenceType keyReferenceType;
	protected final ReferenceType valueReferenceType;

	/**
	 * Concurrent hash map that wraps keys and/or values based on specified
	 * reference types.
	 *
	 * @param keyReferenceType   key reference type
	 * @param valueReferenceType value reference type
	 */
	public ReferenceMap(ReferenceType keyReferenceType, ReferenceType valueReferenceType) {
		if ((keyReferenceType == null) || (valueReferenceType == null)) {
			throw new IllegalArgumentException("References types can not be null");
		}
		if (keyReferenceType == ReferenceType.PHANTOM || valueReferenceType == ReferenceType.PHANTOM) {
			throw new IllegalArgumentException("Phantom references not supported");
		}
		this.delegate = new ConcurrentHashMap<Object, Object>();
		this.keyReferenceType = keyReferenceType;
		this.valueReferenceType = valueReferenceType;
	}

	// ---------------------------------------------------------------- map implementations

	@Override
	public V get(final Object key) {
		Object referenceAwareKey = makeKeyReferenceAware(key);
		Object valueReference = delegate.get(referenceAwareKey);
		return dereferenceValue(valueReference);
	}

	@Override
	public V put(K key, V value) {
		Object referenceKey = referenceKey(key);
		Object referenceValue = referenceValue(referenceKey, value);
		return dereferenceValue(delegate.put(referenceKey, referenceValue));
		//return (V) PutStrategy.PUT.execute(this, referenceKey, referenceValue);
	}

	@Override
	public V remove(Object key) {
		Object referenceAwareKey = makeKeyReferenceAware(key);
		Object valueReference = delegate.remove(referenceAwareKey);
		return dereferenceValue(valueReference);
	}

	@Override
	public int size() {
		return delegate.size();
	}

	@Override
	public boolean isEmpty() {
		return delegate.isEmpty();
	}

	@Override
	public boolean containsKey(Object key) {
		Object referenceAwareKey = makeKeyReferenceAware(key);
		return delegate.containsKey(referenceAwareKey);
	}

	@Override
	public boolean containsValue(Object value) {
		for (Object valueReference : delegate.values()) {
			if (value.equals(dereferenceValue(valueReference))) {
				return true;
			}
		}
		return false;
	}

	@Override
	public void putAll(Map<? extends K, ? extends V> t) {
		for (Map.Entry<? extends K, ? extends V> entry : t.entrySet()) {
			put(entry.getKey(), entry.getValue());
		}
	}

	@Override
	public void clear() {
		delegate.clear();
	}

	public V putIfAbsent(K key, V value) {
		Object referenceKey = referenceKey(key);
		Object referenceValue = referenceValue(referenceKey, value);

		Object existingValueReference;
		Object existingValue;
		do {
			existingValueReference = delegate.putIfAbsent(referenceKey, referenceValue);
			existingValue = dereferenceValue(existingValueReference);
		} while (isExpired(existingValueReference, existingValue));

		return (V) existingValue;
		//return (V) PutStrategy.PUT_IF_ABSENT.execute(this, referenceKey, referenceValue);
	}

	public boolean remove(Object key, Object value) {
		return delegate.remove(makeKeyReferenceAware(key), makeValueReferenceAware(value));
	}

	public boolean replace(K key, V oldValue, V newValue) {
		Object keyReference = referenceKey(key);
		Object referenceAwareOldValue = makeValueReferenceAware(oldValue);
		return delegate.replace(keyReference, referenceAwareOldValue, referenceValue(keyReference, newValue));
	}

	public V replace(K key, V value) {
		Object referenceKey = referenceKey(key);
		Object referenceValue = referenceValue(referenceKey, value);

		// ensure that the existing value is not collected
		do {
			Object existingValueReference;
			Object existingValue;
			do {
				existingValueReference = delegate.get(referenceKey);
				existingValue = dereferenceValue(existingValueReference);
			} while (isExpired(existingValueReference, existingValue));

			if (existingValueReference == null) {
				return (V) Boolean.valueOf(false);  // nothing to replace
			}

			if (delegate.replace(referenceKey, existingValueReference, referenceValue)) {
				return (V) existingValue;       // existingValue did not expire since we still have a reference to it
			}
		} while (true);
		//return (V) PutStrategy.REPLACE.execute(this, referenceKey, referenceValue);
	}

	// ---------------------------------------------------------------- conversions

	/**
	 * Dereferences an entry. Returns <code>null</code> if the key or value has been gc'ed.
	 */
	Entry dereferenceEntry(Map.Entry<Object, Object> entry) {
		K key = dereferenceKey(entry.getKey());
		V value = dereferenceValue(entry.getValue());
		return (key == null || value == null) ? null : new Entry(key, value);
	}

	/**
	 * Creates a reference for a key.
	 */
	Object referenceKey(K key) {
		switch (keyReferenceType) {
			case STRONG: return key;
			case SOFT: return new SoftKeyReference(key);
			case WEAK: return new WeakKeyReference(key);
			default: throw new AssertionError();
		}
	}

	/**
	 * Converts a reference to a key.
	 */
	K dereferenceKey(Object o) {
		return (K) dereference(keyReferenceType, o);
	}

	/**
	 * Converts a reference to a value.
	 */
	V dereferenceValue(Object o) {
		if (o == null) {
			return null;
		}
		Object value = dereference(valueReferenceType, o);
		if (o instanceof InternalReference) {
			InternalReference reference = (InternalReference) o;
			if (value == null) {
				reference.finalizeReferent();     // old value was garbage collected
			}
		}
		return (V) value;
	}

	/**
	 * Returns the refererent for reference given its reference type.
	 */
	private Object dereference(ReferenceType referenceType, Object reference) {
		return referenceType == STRONG ? reference : ((Reference) reference).get();
	}

	/**
	 * Creates a reference for a value.
	 */
	Object referenceValue(Object keyReference, Object value) {
		switch (valueReferenceType) {
			case STRONG: return value;
			case SOFT: return new SoftValueReference(keyReference, value);
			case WEAK: return new WeakValueReference(keyReference, value);
			default: throw new AssertionError();
		}
	}

	/**
	 * Wraps key so it can be compared to a referenced key for equality.
	 */
	private Object makeKeyReferenceAware(Object o) {
		return keyReferenceType == STRONG ? o : new KeyReferenceAwareWrapper(o);
	}

	/**
	 * Wraps value so it can be compared to a referenced value for equality.
	 */
	private Object makeValueReferenceAware(Object o) {
		return valueReferenceType == STRONG ? o : new ReferenceAwareWrapper(o);
	}

	// ---------------------------------------------------------------- inner classes

	/**
	 * Marker interface to differentiate external and internal references. Also
	 * duplicates FinalizableReference and Reference.get for internal use.
	 */
	interface InternalReference {
		/**
		 * Invoked on a background thread after the referent has been garbage
		 * collected.
		 */
		void finalizeReferent();
		Object get();
	}

	/**
	 * Tests weak and soft references for identity equality. Compares references
	 * to other references and wrappers. If o is a reference, this returns true if
	 * r == o or if r and o reference the same non-null object. If o is a wrapper,
	 * this returns true if r's referent is identical to the wrapped object.
	 */
	private static boolean referenceEquals(Reference r, Object o) {
		if (o instanceof InternalReference) {   // compare reference to reference.
			if (o == r) {       // are they the same reference? used in cleanup.
				return true;
			}
			Object referent = ((Reference) o).get();    // do they reference identical values? used in conditional puts.
			return referent != null && referent == r.get();
		}
		return ((ReferenceAwareWrapper) o).unwrap() == r.get();     // is the wrapped object identical to the referent? used in lookups.
	}

	/**
	 * Returns <code>true</code> if the specified value reference has been garbage
	 * collected. The value behind the reference is also passed in, rather than
	 * queried inside this method, to ensure that the return statement of this
	 * method will still hold true after it has returned (that is, a value
	 * reference exists outside of this method which will prevent that value from
	 * being garbage collected).
	 *
	 * @param valueReference the value reference to be tested
	 * @param value          the object referenced by <code>valueReference</code>
	 * @return <code>true</code> if <code>valueReference</code> is non-null and <code>value</code> is <code>null</code>
	 */
	private static boolean isExpired(Object valueReference, Object value) {
		return (valueReference != null) && (value == null);
	}

	/**
	 * Big hack. Used to compare keys and values to referenced keys and values
	 * without creating more references.
	 */
	static class ReferenceAwareWrapper {
		final Object wrapped;

		ReferenceAwareWrapper(Object wrapped) {
			this.wrapped = wrapped;
		}

		Object unwrap() {
			return wrapped;
		}

		@Override
		public int hashCode() {
			return wrapped.hashCode();
		}

		@Override
		public boolean equals(Object obj) {
			return obj.equals(this);    // defer to references equals() logic.
		}
	}

	/**
	 * Used for keys. Overrides hash code to use identity hash code.
	 */
	static class KeyReferenceAwareWrapper extends ReferenceAwareWrapper {
		KeyReferenceAwareWrapper(Object wrapped) {
			super(wrapped);
		}

		@Override
		public int hashCode() {
			return System.identityHashCode(wrapped);
		}
	}

	class SoftKeyReference extends SoftReference<Object> implements InternalReference {
		final int hashCode;

		SoftKeyReference(Object key) {
			super(key, FinalizableReferenceQueue.getInstance());
			this.hashCode = System.identityHashCode(key);
		}

		public void finalizeReferent() {
			delegate.remove(this);
		}

		@Override
		public int hashCode() {
			return this.hashCode;
		}

		@Override
		public boolean equals(Object o) {
			return referenceEquals(this, o);
		}
	}

	class WeakKeyReference extends WeakReference<Object> implements InternalReference {
		final int hashCode;

		WeakKeyReference(Object key) {
			super(key, FinalizableReferenceQueue.getInstance());
			this.hashCode = System.identityHashCode(key);
		}

		public void finalizeReferent() {
			delegate.remove(this);
		}

		@Override
		public int hashCode() {
			return this.hashCode;
		}

		@Override
		public boolean equals(Object o) {
			return referenceEquals(this, o);
		}
	}

	class SoftValueReference extends SoftReference<Object> implements InternalReference {
		final Object keyReference;

		SoftValueReference(Object keyReference, Object value) {
			super(value, FinalizableReferenceQueue.getInstance());
			this.keyReference = keyReference;
		}

		public void finalizeReferent() {
			delegate.remove(keyReference, this);
		}

		@Override
		public boolean equals(Object obj) {
			return referenceEquals(this, obj);
		}
	}

	class WeakValueReference extends WeakReference<Object> implements InternalReference {
		final Object keyReference;

		WeakValueReference(Object keyReference, Object value) {
			super(value, FinalizableReferenceQueue.getInstance());
			this.keyReference = keyReference;
		}

		public void finalizeReferent() {
			delegate.remove(keyReference, this);
		}

		@Override
		public boolean equals(Object obj) {
			return referenceEquals(this, obj);
		}
	}

	static class FinalizableReferenceQueue extends ReferenceQueue<Object> {

		private FinalizableReferenceQueue() {
		}

		void cleanUp(Reference reference) {
			try {
				((InternalReference) reference).finalizeReferent();
			} catch (Throwable t) {
				throw new IllegalStateException("Unable to clean up after reference", t);
			}
		}


		void start() {
			Thread thread = new Thread("FinalizableReferenceQueue") {
				@Override
				@SuppressWarnings({"InfiniteLoopStatement"})
				public void run() {
					while (true) {
						try {
							cleanUp(remove());
						} catch (InterruptedException iex) { /* ignore */ }
					}
				}
			};
			thread.setDaemon(true);
			thread.start();
		}

		static final ReferenceQueue<Object> instance = createAndStart();

		static FinalizableReferenceQueue createAndStart() {
			FinalizableReferenceQueue queue = new FinalizableReferenceQueue();
			queue.start();
			return queue;
		}

		/**
		 * Gets instance.
		 */
		public static ReferenceQueue<Object> getInstance() {
			return instance;
		}
	}

	// ---------------------------------------------------------------- map entry set

	class Entry implements Map.Entry<K, V> {
		final K key;
		V value;

		Entry(K key, V value) {
			this.key = key;
			this.value = value;
		}

		public K getKey() {
			return this.key;
		}

		public V getValue() {
			return this.value;
		}

		public V setValue(V newValue) {
			value = newValue;
			return put(key, newValue);
		}

		@Override
		public int hashCode() {
			return key.hashCode() * 31 + value.hashCode();
		}

		@Override
		public boolean equals(Object o) {
			if (!(o instanceof ReferenceMap.Entry)) {
				return false;
			}

			Entry entry = (Entry) o;
			return key.equals(entry.key) && value.equals(entry.value);
		}

		@Override
		public String toString() {
			return key + StringPool.EQUALS + value;
		}
	}


	private volatile Set<Map.Entry<K, V>> entrySet;

	@Override
	public Set<Map.Entry<K, V>> entrySet() {
		if (entrySet == null) {
			entrySet = new EntrySet();
		}
		return entrySet;
	}

	private class EntrySet extends AbstractSet<Map.Entry<K, V>> {

		@Override
		public Iterator<Map.Entry<K, V>> iterator() {
			return new ReferenceIterator();
		}

		@Override
		public int size() {
			return delegate.size();
		}

		@Override
		public boolean contains(Object o) {
			if (!(o instanceof Map.Entry)) {
				return false;
			}
			Map.Entry<K, V> e = (Map.Entry<K, V>) o;
			V v = ReferenceMap.this.get(e.getKey());
			return v != null && v.equals(e.getValue());
		}

		@Override
		public boolean remove(Object o) {
			if (!(o instanceof Map.Entry)) {
				return false;
			}
			Map.Entry<K, V> e = (Map.Entry<K, V>) o;
			return ReferenceMap.this.remove(e.getKey(), e.getValue());
		}

		@Override
		public void clear() {
			delegate.clear();
		}
	}

	private class ReferenceIterator implements Iterator<Map.Entry<K, V>> {
		private Iterator<Map.Entry<Object, Object>> i = delegate.entrySet().iterator();
		private Map.Entry<K, V> nextEntry;
		private Map.Entry<K, V> lastReturned;

		private ReferenceIterator() {
			advanceToNext();
		}

		private void advanceToNext() {
			while (i.hasNext()) {
				Map.Entry<K, V> entry = dereferenceEntry(i.next());
				if (entry != null) {
					nextEntry = entry;
					return;
				}
			}
			nextEntry = null;
		}

		public boolean hasNext() {
			return nextEntry != null;
		}

		public Map.Entry<K, V> next() {
			if (nextEntry == null) {
				throw new NoSuchElementException();
			}
			lastReturned = nextEntry;
			advanceToNext();
			return lastReturned;
		}

		public void remove() {
			ReferenceMap.this.remove(lastReturned.getKey());
		}
	}

}
