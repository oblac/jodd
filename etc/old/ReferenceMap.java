// Copyright (c) 2003-2007, Jodd Team (jodd.sf.net). All Rights Reserved.

package jodd.util.collection;

import jodd.util.ref.PhantomRef;
import jodd.util.ref.ReferenceType;
import static jodd.util.ref.ReferenceType.SOFT;
import static jodd.util.ref.ReferenceType.STRONG;
import jodd.util.ref.SoftRef;
import jodd.util.ref.WeakRef;

import java.lang.ref.Reference;
import java.lang.ref.ReferenceQueue;
import java.util.AbstractCollection;
import java.util.AbstractSet;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * An abstract implementation of a hash-based map that allows the entries to
 * be removed by the garbage collector.
 */
public class ReferenceMap extends HashMap {

	protected ReferenceType keyType;    // reference type for keys
	protected ReferenceType valueType;  // reference type for values

	protected ReferenceQueue queue = new ReferenceQueue();  // reference queue for purging

	/**
	 * Constructs a new <code>ReferenceMap</code> that will
	 * use <b>strong</b> references to keys and <b>soft</b> references to values.
	 */
	public ReferenceMap() {
		this(STRONG, SOFT);
	}


	/**
	 * Constructs a new <code>ReferenceMap</code> that will use the specified types of references.
	 */
	public ReferenceMap(ReferenceType keyType, ReferenceType valueType) {
		this(keyType, valueType, 16, 0.75f);
	}

	/**
	 * Constructs a new <code>ReferenceMap</code> with the specified reference types,
	 * load factor and initial capacity.
	 */
	public ReferenceMap(ReferenceType keyType, ReferenceType valueType, int capacity, float loadFactor) {
		super(capacity, loadFactor);
		this.keyType = keyType;
		this.valueType = valueType;
	}


	// ---------------------------------------------------------------- conversion

	@SuppressWarnings({"unchecked"})
	protected Object key2ref(Object referent) {
		if (referent == null) {
			return null;
		}
		switch (keyType) {
			case SOFT: return new SoftRef(referent, queue);
			case WEAK: return new WeakRef(referent, queue);
			case PHANTOM: return new PhantomRef(referent, queue);
			default: return referent;
		}
	}

	@SuppressWarnings({"unchecked"})
	protected Object value2ref(Object referent) {
		if (referent == null) {
			return null;
		}
		switch (valueType) {
			case SOFT: return new SoftRef(referent, queue);
			case WEAK: return new WeakRef(referent, queue);
			case PHANTOM:return new PhantomRef(referent, queue);
			default: return referent;
		}
	}


	protected Object ref2key(Object entry) {
		if (entry == null) {
			return null;
		}
		return (keyType == STRONG) ? entry : ((Reference) entry).get();
	}

	@SuppressWarnings({"unchecked"})
	protected Object ref2value(Object entry) {
		if (entry == null) {
			return null;
		}
		return (valueType == STRONG) ? entry : ((Reference) entry).get();
	}


	protected Object value2ref(Object referent, Object key) {
		if (referent == null) {
			return null;
		}
		switch (valueType) {
			case SOFT: return new KeyValueSoftReference(referent, queue, key);
			case WEAK: return new KeyValueWeakReference(referent, queue, key);
			case PHANTOM: return new KeyValuePhantomReference(referent, queue, key);
			default: return referent;
		}
	}

	protected static class KeyValueWeakReference extends WeakRef {
        Object key;
        @SuppressWarnings({"unchecked"})
        protected KeyValueWeakReference(Object value, ReferenceQueue queue, Object key) {
            super(value, queue);
            this.key = key;
        }
        public Object getKey() { return this.key; }
		public Object getValue() { return super.get(); }
    }

	protected static class KeyValueSoftReference extends SoftRef {
        Object key;
        @SuppressWarnings({"unchecked"})
        protected KeyValueSoftReference(Object value, ReferenceQueue queue, Object key) {
            super(value, queue);
            this.key = key;
        }
        public Object getKey() { return this.key; }
		public Object getValue() { return super.get(); }
    }

	protected static class KeyValuePhantomReference extends PhantomRef {
        Object key;
        @SuppressWarnings({"unchecked"})
        protected KeyValuePhantomReference(Object value, ReferenceQueue queue, Object key) {
            super(value, queue);
            this.key = key;
        }
        public Object getKey() { return this.key; }
		public Object getValue() { return super.get(); }
    }



	// ---------------------------------------------------------------- purge

	/**
	 * Purges stale mappings from this map.
	 */
	protected void purge() {
		Reference ref;
		while ((ref = queue.poll()) != null) {
			purge(ref);
		}
	}

	/**
	 * Purges single reference.
	 */
	protected void purge(Reference ref) {
		if (ref instanceof KeyValueWeakReference) {
			this.remove(((KeyValueWeakReference) ref).getKey());
		} else {
			super.remove(ref);
		}
	}

	// ---------------------------------------------------------------- impl

	/**
	 * Returns the size of this map.
	 */
	@Override
	public int size() {
		purge();
		return super.size();
	}


	/**
	 * Returns <code>true</code> if this map is empty.
	 */
	@Override
	public boolean isEmpty() {
		purge();
		return super.isEmpty();
	}


	/**
	 * Returns <code>true</code> if this map contains the given key.
	 */
	@Override
	public boolean containsKey(Object key) {
		purge();
		return super.containsKey(key2ref(key));
	}
	/**
	 * Returns <code>true</code> if this map contains the given value.
	 */
	@Override
	public boolean containsValue(Object value) {
		purge();
		return super.containsValue(value2ref(value));
	}

	/**
	 * Returns the value associated with the given key, if any.
	 * Returns <code>null</code> if the key maps to no value.
	 */
	@Override
	public Object get(Object key) {
		purge();
		Object entry = super.get(key2ref(key));
		//noinspection unchecked
		return ref2value(entry);
	}


	/**
	 * Associates the given key with the given value.
	 */
	@Override
	@SuppressWarnings({"unchecked"})
	public Object put(Object key, Object value) {
		purge();
		Object key2 = key2ref(key);
		Object value2 = value2ref(value, key2);
		return ref2value(super.put(key2, value2));
	}

	/**
	 * Removes the key and its associated value from this map.
	 */
	@Override
	public Object remove(Object key) {
		purge();
		Object entity = super.remove(key2ref(key));
		//noinspection unchecked
		return ref2value(entity);
	}

	@Override
	public Object clone() {
		purge();
		return super.clone();
	}

	// ---------------------------------------------------------------- collections

	/**
	 * Returns a set view of this map's entries.
	 */
	@Override
	@SuppressWarnings({"unchecked"})
	public Set<Map.Entry> entrySet() {
		final Set<Map.Entry> es = super.entrySet();
		return new AbstractSet<Map.Entry>() {
			
			@Override
			public int size() {
				return es.size();
			}

			@Override
			public void clear() {
				es.clear();
			}

			@Override
			public boolean contains(Object o) {
				return es.contains(o);
			}

			@Override
			public boolean remove(Object o) {
				return es.remove(o);
			}

			@Override
			public Iterator<Map.Entry> iterator() {
				final Iterator<Map.Entry> esi = es.iterator();
				return new Iterator<Map.Entry>() {
					public boolean hasNext() {
						return esi.hasNext();
					}

					public Map.Entry next() {
						final Map.Entry mapEntry = esi.next();
						return new Map.Entry() {
							public Object getKey() {return ref2key(mapEntry.getKey());}
							public Object getValue() {return ref2value(mapEntry.getValue());}
							@SuppressWarnings({"unchecked"})
							public Object setValue(Object value) {
								Object v = mapEntry.setValue(value2ref(value, mapEntry.getKey()));
								return ref2value(v);
							}
						};
					}
					public void remove() {
						esi.remove();
					}
				};
			}

			@Override
			public Map.Entry[] toArray() {
				return toArray(new Map.Entry[0]);
			}

			@Override
			@SuppressWarnings({"unchecked", "SuspiciousToArrayCall"})
			public Map.Entry[] toArray(Object[] arr) {
				Map.Entry[] entries = (Map.Entry[]) es.toArray(arr);
				Map.Entry[] result = new Map.Entry[entries.length];
				for (int i = 0; i < entries.length; i++) {
					final Map.Entry me = entries[i];
					result[i] = new Map.Entry() {
							public Object getKey() {return ref2key(me.getKey());}
							public Object getValue() {return ref2value(me.getValue());}
							@SuppressWarnings({"unchecked"})
							public Object setValue(Object value) {
								Object v = me.setValue(value2ref(value, me.getKey()));
								return ref2value(v);
							}
					};
				}
				return result;
			}
		};
	}


	/**
	 * Returns a set view of this map's keys.
	 */
	@Override
	public Set keySet() {
		final Set kes = super.keySet();

		return new AbstractSet() {

			@Override
			public int size() {
				return kes.size();
			}

			@Override
			public void clear() {
				kes.clear();
			}

			@Override
			public boolean contains(Object o) {
				return kes.contains(o);
			}

			@Override
			public boolean remove(Object o) {
				return kes.remove(o);
			}

			@Override
			public Iterator iterator() {
				final Iterator kesI = kes.iterator();
				return new Iterator() {

					public boolean hasNext() {
						return kesI.hasNext();
					}

					public Object next() {
						Object entry = kesI.next();
						return ref2key(entry);
					}

					public void remove() {
						kesI.remove();
					}
				};
			}

			@Override
			public Object[] toArray() {
				return toArray(new Object[0]);
			}

			@Override
			public Object[] toArray(Object[] arr) {
				//noinspection unchecked
				Object[] entries = kes.toArray(arr);
				for (int i = 0; i < entries.length; i++) {
					entries[i] = ref2key(entries[i]);
				}
				return entries;
			}
		};
	}


	/**
	 * Returns a collection view of this map's values.
	 */
	@Override
	public Collection values() {
		final Collection vals = super.values();

		return new AbstractCollection() {
			
			@Override
			public int size() {
				return vals.size();
			}

			@Override
			public void clear() {
				vals.clear();
			}

			@Override
			public Iterator iterator() {
				final Iterator valsI = vals.iterator();
				return new Iterator() {
					public boolean hasNext() {
						return valsI.hasNext();
					}

					public Object next() {
						Object entry = valsI.next();
						return ref2value(entry);
					}

					public void remove() {
						valsI.remove();
					}
				};
			}

			@Override
			public Object[] toArray() {
				return toArray(new Object[0]);
			}

			@Override
			@SuppressWarnings({"unchecked"})
			public Object[] toArray(Object[] arr) {
				Object[] entries = vals.toArray(arr);
				for (int i = 0; i < entries.length; i++) {
					entries[i] = ref2value(entries[i]);
				}
				return entries;
			}
		};
	}

}
