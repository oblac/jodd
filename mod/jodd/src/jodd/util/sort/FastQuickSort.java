// Copyright (c) 2003-2011, Jodd Team (jodd.org). All Rights Reserved.

package jodd.util.sort;

import java.util.Comparator;

import jodd.util.ComparableComparator;

/**
 * Maybe the fastest implementation of famous Quick-Sort
 * algorithm. It is even faster than Denisa Ahrensa implementation that
 * performs 7.5s for sorting million objects, this implementation
 * sorts for 6.8s. However, {@link FastMergeSort} is much faster.     
 */
public class FastQuickSort implements Sorter {
	
	@SuppressWarnings({"unchecked"})
	public static void qsort(Object[] c, Comparator comparator) {
		int i, j, left = 0, right = c.length - 1, stack_pointer = -1;
		int[] stack = new int[128];
		Object swap, temp;
		while (true) {
			if (right - left <= 7) {
				for (j = left + 1; j <= right; j++) {
					swap = c[j];
					i = j - 1;
					while (i >= left && comparator.compare(c[i], swap) > 0) {
						c[i + 1] = c[i--];
					}
					c[i + 1] = swap;
				}
				if (stack_pointer == -1) {
					break;
				}
				right = stack[stack_pointer--];
				left = stack[stack_pointer--];
			} else {
				int median = (left + right) >> 1;
				i = left + 1;
				j = right;
				swap = c[median]; c[median] = c[i]; c[i] = swap;
				if (comparator.compare(c[left], c[right]) > 0) {
					swap = c[left]; c[left] = c[right]; c[right] = swap;
				}
				if (comparator.compare(c[i], c[right]) > 0) {
					swap = c[i]; c[i] = c[right]; c[right] = swap;
				}
				if (comparator.compare(c[left], c[i]) > 0) {
					swap = c[left]; c[left] = c[i]; c[i] = swap;
				}
				temp = c[i];
				while (true) {
					//noinspection ControlFlowStatementWithoutBraces,StatementWithEmptyBody
					while (comparator.compare(c[++i], temp) < 0);
					//noinspection ControlFlowStatementWithoutBraces,StatementWithEmptyBody
					while (comparator.compare(c[--j], temp) > 0);
					if (j < i) {
						break;
					}
					swap = c[i]; c[i] = c[j]; c[j] = swap;
				}
				c[left + 1] = c[j];
				c[j] = temp;
				if (right - i + 1 >= j - left) {
					stack[++stack_pointer] = i;
					stack[++stack_pointer] = right;
					right = j - 1;
				} else {
					stack[++stack_pointer] = left;
					stack[++stack_pointer] = j - 1;
					left = i;
				}
			}
		}
	}	

	public void sort(Object a[], Comparator comparator) {
		qsort(a, comparator);
	}
	
	public void sort(Comparable a[]) {
		qsort(a, new ComparableComparator());
	}

	// ---------------------------------------------------------------- static

	public static void doSort(Object a[], Comparator comparator) {
		qsort(a, comparator);
	}

	public static void doSort(Comparable a[]) {
		qsort(a, ComparableComparator.INSTANCE);
	}

}
