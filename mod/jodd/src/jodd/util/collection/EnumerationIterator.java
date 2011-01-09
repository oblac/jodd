// Copyright (c) 2003-2011, Jodd Team (jodd.org). All Rights Reserved.

package jodd.util.collection;

import java.util.Enumeration;
import java.util.Iterator;

/**
 * Iterator adapter for enumeration.
 */
public class EnumerationIterator<E> implements Iterator<E> {

	private final Enumeration<E> enumeration;

	public EnumerationIterator(Enumeration<E> enumeration) {
		this.enumeration = enumeration;
	}

	public boolean hasNext() {
		return enumeration.hasMoreElements();
	}

	public E next() {
		return enumeration.nextElement();
	}

	public void remove() throws UnsupportedOperationException {
		throw new UnsupportedOperationException("Removing from enumeration not supported.");
	}
}
