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
import java.util.HashSet;
import java.util.Iterator;

/**
 * A <code>Set</code> implementation with <em>weak elements</em>.  An entry in
 * a <code>ReferenceSet</code> will automatically be removed when the element is no
 * longer in ordinary use.
 */
public class ReferenceSet extends HashSet {

	protected ReferenceType valueType;  // reference type for values

	protected final ReferenceQueue queue = new ReferenceQueue();      // reference queue used to get object removal notifications

	/**
	 * Creates set with <b>soft</b> reference on values.
	 */
	public ReferenceSet() {
		this(SOFT);
	}

	public ReferenceSet(ReferenceType type) {
		this(type, 16, 0.75f);
	}

	public ReferenceSet(ReferenceType type, int initialCapacity, float loadingFactor) {
		super(initialCapacity, loadingFactor);
		this.valueType = type;
	}

	// ---------------------------------------------------------------- converters

	@SuppressWarnings({"unchecked"})
	protected Object value2ref(Object referent) {
		if (referent == null) {
			return null;
		}
		switch (valueType) {
			case SOFT: return new SoftRef(referent, queue);
			case WEAK: return new WeakRef(referent, queue);
			case PHANTOM: return new PhantomRef(referent, queue);
			default: return referent;
		}
	}

	protected Object ref2value(Object entry) {
		if (entry == null) {
			return null;
		}
		return (valueType == STRONG) ? entry : ((Reference) entry).get();
	}



	// ---------------------------------------------------------------- purge


	/**
	 * Maintain the elements in the set. Removes objects from the set that
	 * have been reclaimed due to GC.
	 */
	protected final void purge() {
		Reference reference;
		while ((reference = queue.poll()) != null) {
			super.remove(reference);
		}
	}

	/**
	 * Return the size of the set.
	 */
	@Override
	public int size() {
		purge();
		return super.size();
	}

	/**
	 * Add an element to the set.
	 */
	@Override
	@SuppressWarnings({"unchecked"})
	public boolean add(Object value) {
		purge();
		if (value == null) {
			return super.add(null);
		}
		return super.add(value2ref(value));
	}

	/**
	 * Returns <code>true</code> if this set contains no elements.
	 */
	@Override
	public boolean isEmpty() {
		purge();
		return super.isEmpty();
	}

	/**
	 * Returns <code>true</code> if this set contains the specified element.
	 */
	@Override
	@SuppressWarnings({"unchecked"})
	public boolean contains(final Object value) {
		purge();
		if (value == null) {
			return super.contains(null);
		}
		return super.contains(value2ref(value));
	}

	/**
	 * Removes the given element from this set if it is present.
	 */
	@Override
	@SuppressWarnings({"unchecked"})
	public boolean remove(final Object value) {
		purge();
		if (value == null) {
			return super.remove(null);
		}
		return super.remove(value2ref(value));
	}

	/**
	 * Returns a shallow copy of this <code>WeakSet</code> instance: the elements
	 * themselves are not cloned.
	 */
	@Override
	public Object clone() {
		purge();
		return super.clone();
	}

	// ---------------------------------------------------------------- iterator

	/**
	 * Return an iteration over the elements in the set.
	 */
	@Override
	public Iterator iterator() {
		final Iterator iterator = super.iterator();

		return new Iterator() {
			public boolean hasNext() {
				return iterator.hasNext();
			}

			public Object next() {
				return ref2value(iterator.next());
			}

			public void remove() {
				iterator.remove();
			}
		};
	}


}