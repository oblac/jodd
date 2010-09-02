// Copyright (c) 2003-2010, Jodd Team (jodd.org). All Rights Reserved.

package jodd.util;

import jodd.util.collection.EnumerationIterator;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * Collection utilities.
 */
public class CollectionUtil {

	/**
	 * Adapts an enumeration to an iterator.
	 */
	public static <E> Iterator<E> toIterator(Enumeration<E> enumeration) {
		return new EnumerationIterator<E>(enumeration);
	}

	/**
	 * Iterate elements to a list.
	 * Returns an empty list if there are no elements to iterate.
	 */
	public static <E> List<E> iterateToList(Iterator<E> iterator) {
		List<E> list = new ArrayList<E>();
		while (iterator.hasNext()) {
			list.add(iterator.next());
		}
		return list;
	}

	/**
	 * Iterate elements to a set.
	 * Returns an empty set if there are no elements to iterate.
	 */
	public static <E> Set<E> iterateToSet(Iterator<E> iterator) {
		Set<E> set = new HashSet<E>();
		while (iterator.hasNext()) {
			set.add(iterator.next());
		}
		return set;
	}

}
