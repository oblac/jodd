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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

/**
 * Iterator that combines multiple iterators.
 */
public class CompositeIterator<T> implements Iterator<T> {

	protected final List<Iterator<T>> allIterators = new ArrayList<>();

	/**
	 * Creates new composite iterator.
	 * Iterators may be added using the {@link #add(Iterator)} method.
	 */
	public CompositeIterator() {
	}

	/**
	 * Creates new composite iterator with provided iterators.
	 */
	public CompositeIterator(final Iterator<T>... iterators) {
		for (Iterator<T> iterator : iterators) {
			add(iterator);
		}
	}

	/**
	 * Adds an iterator to this composite.
	 */
	public void add(final Iterator<T> iterator) {
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
	@Override
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
	@Override
	public T next() {
		if (!hasNext()) {
			throw new NoSuchElementException();
		}

		return allIterators.get(currentIterator).next();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void remove() {
		if (currentIterator == -1) {
			throw new IllegalStateException("next() has not yet been called");
		}

		allIterators.get(currentIterator).remove();
	}

}