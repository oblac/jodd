// Copyright (c) 2003-2011, Jodd Team (jodd.org). All Rights Reserved.

package jodd.util.sort;

import java.util.Arrays;
import java.util.Comparator;

/**
 * Default JDK sort. Uses {@link Arrays#sort(Object[], java.util.Comparator)}.
 */
public class DefaultSort implements Sorter {
	
	@SuppressWarnings({"unchecked"})
	public void sort(Object a[], Comparator comparator) {
		Arrays.sort(a, comparator);
	}
	
	public void sort(Comparable a[]) {
		Arrays.sort(a);
	}

	// ---------------------------------------------------------------- static

	@SuppressWarnings({"unchecked"})
	public static void doSort(Object[] a, Comparator comparator) {
		Arrays.sort(a, comparator);
	}

	public static void doSort(Comparable[] a) {
		Arrays.sort(a);
	}

}
