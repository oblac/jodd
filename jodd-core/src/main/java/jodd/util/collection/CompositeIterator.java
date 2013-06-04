// Copyright (c) 2003-2013, Jodd Team (jodd.org). All Rights Reserved.
package jodd.util.collection;

import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.ArrayList;

/**
 * Iterator that combines multiple other iterators.
 */
public class CompositeIterator<T> implements Iterator<T> {

	protected final List<Iterator<T>> allIterators = new ArrayList<Iterator<T>>();

	/**
	 * Creates new composite iterator.
	 * Iterators may be added using the {@link #add(Iterator)} method.
	 */
	public CompositeIterator() {
	}

	/**
	 * Creates new composite iterator with provided iterators.
	 */
	public CompositeIterator(Iterator<T>... iterators) {
		for (Iterator<T> iterator : iterators) {
			add(iterator);
		}
	}

	/**
	 * Adds an iterator to this composite.
	 */
	public void add(Iterator<T> iterator) {
		if (allIterators.contains(iterator)) {
			throw new IllegalArgumentException("Duplicate iterator");
		}
		allIterators.add(iterator);
	}

	// ---------------------------------------------------------------- interface

	protected int currentIterator = -1;

	/**
	 * Returns <code>true</code> if next element is available.
	 */
	public boolean hasNext() {
		if (currentIterator == -1) {
			currentIterator = 0;
		}
		for (int i = currentIterator; i < allIterators.size(); i++) {
			Iterator iterator = allIterators.get(i);
			if (iterator.hasNext()) {
				currentIterator = i;
				return true;
			}
		}
		return false;
	}

	/**
	 * {@inheritDoc}
	 */
	public T next() {
		if (hasNext() == false) {
			throw new NoSuchElementException();
		}

		return allIterators.get(currentIterator).next();
	}

	/**
	 * {@inheritDoc}
	 */
	public void remove() {
		if (currentIterator == -1) {
			throw new IllegalStateException("The next() has not yet been called");
		}

		allIterators.get(currentIterator).remove();
	}
}