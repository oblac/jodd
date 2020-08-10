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
import java.util.Enumeration;
import java.util.List;
import java.util.NoSuchElementException;

/**
  * Enumeration that combines multiple enumerations.
  */
public class CompositeEnumeration<T> implements Enumeration<T> {

	protected final List<Enumeration<T>> allEnumerations = new ArrayList<>();

	/**
	 * Creates new composite enumeration.
	 * Enumerations may be added using the {@link #add(Enumeration)} method.
	 */
	public CompositeEnumeration() {
	}

	/**
	 * Creates new composite enumeration with provided enumerations.
	 */
	public CompositeEnumeration(final Enumeration<T>... enumerations) {
		for (final Enumeration<T> enumeration : enumerations) {
			add(enumeration);
		}
	}

	/**
	 * Adds an enumeration to this composite.
	 */
	public void add(final Enumeration<T> enumeration) {
		if (allEnumerations.contains(enumeration)) {
			throw new IllegalArgumentException("Duplicate enumeration");
		}
		allEnumerations.add(enumeration);
	}

	// ---------------------------------------------------------------- interface

	protected int currentEnumeration = -1;

	/**
	 * Returns <code>true</code> if composite has more elements.
	 */
	public boolean hasMoreElements() {
		if (currentEnumeration == -1) {
			currentEnumeration = 0;
		}
		for (int i = currentEnumeration; i < allEnumerations.size(); i++) {
			final Enumeration<T> enumeration = allEnumerations.get(i);
			if (enumeration.hasMoreElements()) {
				currentEnumeration = i;
				return true;
			}
		}
		return false;
	}

	/**
	 * {@inheritDoc}
	 */
	public T nextElement() {
		if (!hasMoreElements()) {
			throw new NoSuchElementException();
		}

		return allEnumerations.get(currentEnumeration).nextElement();
	}

}
