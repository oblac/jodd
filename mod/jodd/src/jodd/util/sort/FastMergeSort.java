// Copyright (c) 2003-2009, Jodd Team (jodd.org). All Rights Reserved.

package jodd.util.sort;

import java.util.Comparator;

import jodd.util.ComparableComparator;

/**
 * Faster merge sort. When original JDK routine runs 5s for sorting
 * 1 million objects this one runs for 3.5s.
 * <p>
 * reference: Arrays.mergeSort (private method).
 */
public class FastMergeSort implements Sorter {

	@SuppressWarnings({"unchecked"})
	private static void mergeSort(Object src[], Object dest[], int low, int high, int off, Comparator c) {
		int length = high - low;

		// use insertion sort on smallest arrays
		if (length < 7) {
			for (int i = low; i < high; i++) {
				for (int j = i; j > low && c.compare(dest[j - 1], dest[j]) > 0; j--) {
					Object temp = dest[j];
					dest[j] = dest[j - 1];
					dest[j - 1] = temp;
				}
			}
			return;
		}

		// recursively sort halves of dest into src
		int destLow = low;
		int destHigh = high;
		low += off;
		high += off;
		int mid = (low + high) >> 1;
		mergeSort(dest, src, low, mid, -off, c);
		mergeSort(dest, src, mid, high, -off, c);

		// is list already sorted?
		if (c.compare(src[mid - 1], src[mid]) <= 0) {
			System.arraycopy(src, low, dest, destLow, length);
			return;
		}

		// merge sorted halves from src into dest
		for (int i = destLow, p = low, q = mid; i < destHigh; i++) {
			if (q >= high || p < mid && c.compare(src[p], src[q]) <= 0) {
				dest[i] = src[p++];
			} else {
				dest[i] = src[q++];
			}
		}
	}

	public void sort(Object[] a, Comparator comparator) {
		Object aux[] = a.clone(); 
		mergeSort(aux, a, 0, a.length, 0, comparator);
	}

	public void sort(Comparable[] a) {
		Object aux[] = a.clone();
		mergeSort(aux, a, 0, a.length, 0, ComparableComparator.INSTANCE);
	}

	// ---------------------------------------------------------------- static

	public static void doSort(Object[] a, Comparator c) {
		Object aux[] = a.clone();
		mergeSort(aux, a, 0, a.length, 0, c);
	}

	public static void doSort(Comparable[] a) {
		Object aux[] = a.clone();
		mergeSort(aux, a, 0, a.length, 0, ComparableComparator.INSTANCE);
	}

}