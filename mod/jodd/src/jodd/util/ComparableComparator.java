// Copyright (c) 2003-2010, Jodd Team (jodd.org). All Rights Reserved.

package jodd.util;

import java.util.Comparator;


/**
 * Comparator that adapts <code>Comparables</code> to the <code>Comparator</code> interface.
 */
public class ComparableComparator<T extends Comparable<T>> implements Comparator<T> {

	/**
	 * Cached instance.
	 */
	public static final ComparableComparator INSTANCE = new ComparableComparator();
	
	public int compare(T o1, T o2) {
		return o1.compareTo(o2);
	}	

}
