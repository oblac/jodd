// Copyright (c) 2003-2011, Jodd Team (jodd.org). All Rights Reserved.
package jodd.util.collection;

import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.ArrayList;

/**
 * Iterator that combines multiple other iterators.
 */
public class CompositeIterator implements Iterator {

	protected final List<Iterator> allIterators = new ArrayList<Iterator>();

	/**
	 * Creates new composite iterator.
	 * Iterators may be added using the {@link #add(Iterator)} method.
	 */
	public CompositeIterator() {
	}

	/**
	 * Creates new composite iterator with provided iterators.
	 */
	public CompositeIterator(Iterator... iterators) {
		for (Iterator iterator : iterators) {
			add(iterator);
		}
	}


	/**
	 * Adds an iterator to this composite.
	 */
	public void add(Iterator iterator) {
		if (allIterators.contains(iterator)) {
			throw new IllegalArgumentException("Same iterator already added to this composite.");
		}
		allIterators.add(iterator);
	}

	// ---------------------------------------------------------------- interface


	/**
	 * {@inheritDoc}
	 */
	public boolean hasNext() {
		for (Iterator iterator : allIterators) {
			if (iterator.hasNext()) {
				return true;
			}
		}
		return false;
	}

	protected int currentIterator = -1;

	/**
	 * {@inheritDoc}
	 */
	public Object next() {
		for (int i = 0; i < allIterators.size(); i++) {
			Iterator iterator = allIterators.get(i);
			if (iterator.hasNext()) {
				currentIterator = i;
				return iterator.next();
			}
		}
		throw new NoSuchElementException("All iterators exhausted");
	}

	/**
	 * {@inheritDoc}
	 */
	public void remove() {
		if (currentIterator != -1) {
			allIterators.get(currentIterator).remove();
		} else {
			throw new IllegalStateException("The next() method has not yet been called.");
		}
	}
}