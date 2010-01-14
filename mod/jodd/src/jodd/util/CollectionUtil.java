// Copyright (c) 2003-2010, Jodd Team (jodd.org). All Rights Reserved.

package jodd.util;

import jodd.util.collection.EnumerationIterator;

import java.util.Enumeration;
import java.util.Iterator;

/**
 * Collection utilities.
 */
public class CollectionUtil {


	/**
	 * Adapts an enumeration to an iterator.
	 */
	public static Iterator toIterator(Enumeration enumeration) {
		return new EnumerationIterator(enumeration);
	}

}
