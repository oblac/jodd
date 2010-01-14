// Copyright (c) 2003-2010, Jodd Team (jodd.org). All Rights Reserved.

package jodd.util.collection;

import java.util.Enumeration;
import java.util.Iterator;

/**
 * Iterator adapter for enumeration.
 */
public class EnumerationIterator implements Iterator {

	private final Enumeration enumeration;

	public EnumerationIterator(Enumeration enumeration) {
		this.enumeration = enumeration;
	}

	public boolean hasNext() {
		return enumeration.hasMoreElements();
	}

	public Object next() {
		return enumeration.nextElement();
	}

	public void remove() throws UnsupportedOperationException {
		throw new UnsupportedOperationException("Removing from enumeration not supported.");
	}
}
