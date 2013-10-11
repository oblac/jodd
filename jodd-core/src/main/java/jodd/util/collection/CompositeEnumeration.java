// Copyright (c) 2003-2013, Jodd Team (jodd.org). All Rights Reserved.

package jodd.util.collection;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.NoSuchElementException;

/**
  * Enumeration that combines multiple enumerations.
  */
public class CompositeEnumeration<T> implements Enumeration<T> {

	protected final List<Enumeration<T>> allEnumerations = new ArrayList<Enumeration<T>>();

	/**
	 * Creates new composite enumeration.
	 * Enumerations may be added using the {@link #add(Enumeration)} method.
	 */
	public CompositeEnumeration() {
	}

	/**
	 * Creates new composite enumeration with provided enumerations.
	 */
	public CompositeEnumeration(Enumeration<T>... enumerations) {
		for (Enumeration<T> enumeration : enumerations) {
			add(enumeration);
		}
	}

	/**
	 * Adds an enumeration to this composite.
	 */
	public void add(Enumeration<T> enumeration) {
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
			Enumeration enumeration = allEnumerations.get(i);
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
		if (hasMoreElements() == false) {
			throw new NoSuchElementException();
		}

		return allEnumerations.get(currentEnumeration).nextElement();
	}

}