// Copyright (c) 2003-present, Jodd Team (jodd.org). All Rights Reserved.

package jodd.util.sort;

import java.util.Comparator;

/**
 * Fast sort.
 * <p>
 * In JDK {@literal <} v7, implementation of merge sort (implemented in <code>Arrays.sort</code>) is
 * not the best one: there is redundant object array cloning, many small methods that
 * can be inlined etc.
 * <p>
 * This class brings the best sorting implementations, so they can be used on JDK5 and JDK6, too.
 */
public class FastSort {

	/**
	 * Sorts an array using given comparator.
	 */
	public static <T> void sort(T array[], Comparator<T> comparator) {
		TimSort.sort(array, comparator);
	}

	/**
	 * Sorts an array of comparables.
	 */
	public static void sort(Comparable array[]) {
		ComparableTimSort.sort(array);
	}

}