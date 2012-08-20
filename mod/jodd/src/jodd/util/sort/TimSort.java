// Copyright (c) 2003-2012, Jodd Team (jodd.org). All Rights Reserved.

package jodd.util.sort;

import java.util.Comparator;

/**
 * <code>TimSort</code> from <b>JDK7</b>.
 * Changes:
 * <ul>
 * <li>reformatted</li>
 * <li>single sort method</li>
 * <li>no range check</li>
 * <li>asserts removed</li>
 * <li>comments removed</li>
 * </ul>
 *
 * @author Josh Bloch
 */
public class TimSort<T> {
	private static final int MIN_MERGE = 32;
	private final T[] a;
	private final Comparator<? super T> c;
	private static final int MIN_GALLOP = 7;
	private int minGallop = MIN_GALLOP;
	private static final int INITIAL_TMP_STORAGE_LENGTH = 256;

	private T[] tmp;

	private int stackSize = 0;
	private final int[] runBase;
	private final int[] runLen;

	/**
	 * Creates a TimSort instance to maintain the state of an ongoing sort.
	 *
	 * @param a the array to be sorted
	 * @param c the comparator to determine the order of the sort
	 */
	private TimSort(T[] a, Comparator<? super T> c) {
		this.a = a;
		this.c = c;

		// Allocate temp storage (which may be increased later if necessary)
		int len = a.length;
		@SuppressWarnings({"unchecked", "UnnecessaryLocalVariable"})
		T[] newArray = (T[]) new Object[len < 2 * INITIAL_TMP_STORAGE_LENGTH ?
				len >>> 1 : INITIAL_TMP_STORAGE_LENGTH];
		tmp = newArray;

		/*
				 * Allocate runs-to-be-merged stack (which cannot be expanded).  The
				 * stack length requirements are described in listsort.txt.  The C
				 * version always uses the same stack length (85), but this was
				 * measured to be too expensive when sorting "mid-sized" arrays (e.g.,
				 * 100 elements) in Java.  Therefore, we use smaller (but sufficiently
				 * large) stack lengths for smaller arrays.  The "magic numbers" in the
				 * computation below must be changed if MIN_MERGE is decreased.  See
				 * the MIN_MERGE declaration above for more information.
				 */
		int stackLen = (len < 120 ? 5 :
				len < 1542 ? 10 :
						len < 119151 ? 19 : 40);
		runBase = new int[stackLen];
		runLen = new int[stackLen];
	}

	public static <T> void sort(T[] a, Comparator<? super T> c) {
		int lo = 0;
		int hi = a.length;

		int nRemaining = hi - lo;
		if (nRemaining < 2)
			return;

		if (nRemaining < MIN_MERGE) {
			int initRunLen = countRunAndMakeAscending(a, lo, hi, c);
			binarySort(a, lo, hi, lo + initRunLen, c);
			return;
		}

		TimSort<T> ts = new TimSort<T>(a, c);
		int minRun = minRunLength(nRemaining);
		do {
			// Identify next run
			int runLen = countRunAndMakeAscending(a, lo, hi, c);

			// If run is short, extend to min(minRun, nRemaining)
			if (runLen < minRun) {
				int force = nRemaining <= minRun ? nRemaining : minRun;
				binarySort(a, lo, lo + force, lo + runLen, c);
				runLen = force;
			}

			// Push run onto pending-run stack, and maybe merge
			ts.pushRun(lo, runLen);
			ts.mergeCollapse();

			// Advance to find next run
			lo += runLen;
			nRemaining -= runLen;
		} while (nRemaining != 0);

		// Merge all remaining runs to complete sort
		ts.mergeForceCollapse();
	}

	@SuppressWarnings("fallthrough")
	private static <T> void binarySort(T[] a, int lo, int hi, int start,
									   Comparator<? super T> c) {
		if (start == lo)
			start++;
		for (; start < hi; start++) {
			T pivot = a[start];

			// Set left (and right) to the index where a[start] (pivot) belongs
			int left = lo;
			int right = start;

			while (left < right) {
				int mid = (left + right) >>> 1;
				if (c.compare(pivot, a[mid]) < 0)
					right = mid;
				else
					left = mid + 1;
			}

			int n = start - left;  // The number of elements to move
			// Switch is just an optimization for arraycopy in default case
			switch (n) {
				case 2:
					a[left + 2] = a[left + 1];
				case 1:
					a[left + 1] = a[left];
					break;
				default:
					System.arraycopy(a, left, a, left + 1, n);
			}
			a[left] = pivot;
		}
	}

	private static <T> int countRunAndMakeAscending(T[] a, int lo, int hi,
													Comparator<? super T> c) {
		int runHi = lo + 1;
		if (runHi == hi)
			return 1;

		// Find end of run, and reverse range if descending
		if (c.compare(a[runHi++], a[lo]) < 0) { // Descending
			while (runHi < hi && c.compare(a[runHi], a[runHi - 1]) < 0)
				runHi++;
			reverseRange(a, lo, runHi);
		} else {                              // Ascending
			while (runHi < hi && c.compare(a[runHi], a[runHi - 1]) >= 0)
				runHi++;
		}

		return runHi - lo;
	}

	private static void reverseRange(Object[] a, int lo, int hi) {
		hi--;
		while (lo < hi) {
			Object t = a[lo];
			a[lo++] = a[hi];
			a[hi--] = t;
		}
	}

	private static int minRunLength(int n) {
		int r = 0;      // Becomes 1 if any 1 bits are shifted off
		while (n >= MIN_MERGE) {
			r |= (n & 1);
			n >>= 1;
		}
		return n + r;
	}

	private void pushRun(int runBase, int runLen) {
		this.runBase[stackSize] = runBase;
		this.runLen[stackSize] = runLen;
		stackSize++;
	}

	private void mergeCollapse() {
		while (stackSize > 1) {
			int n = stackSize - 2;
			if (n > 0 && runLen[n - 1] <= runLen[n] + runLen[n + 1]) {
				if (runLen[n - 1] < runLen[n + 1])
					n--;
				mergeAt(n);
			} else if (runLen[n] <= runLen[n + 1]) {
				mergeAt(n);
			} else {
				break; // Invariant is established
			}
		}
	}

	private void mergeForceCollapse() {
		while (stackSize > 1) {
			int n = stackSize - 2;
			if (n > 0 && runLen[n - 1] < runLen[n + 1])
				n--;
			mergeAt(n);
		}
	}

	private void mergeAt(int i) {

		int base1 = runBase[i];
		int len1 = runLen[i];
		int base2 = runBase[i + 1];
		int len2 = runLen[i + 1];

		runLen[i] = len1 + len2;
		if (i == stackSize - 3) {
			runBase[i + 1] = runBase[i + 2];
			runLen[i + 1] = runLen[i + 2];
		}
		stackSize--;

		int k = gallopRight(a[base2], a, base1, len1, 0, c);
		base1 += k;
		len1 -= k;
		if (len1 == 0)
			return;

		len2 = gallopLeft(a[base1 + len1 - 1], a, base2, len2, len2 - 1, c);
		if (len2 == 0)
			return;

		// Merge remaining runs, using tmp array with min(len1, len2) elements
		if (len1 <= len2)
			mergeLo(base1, len1, base2, len2);
		else
			mergeHi(base1, len1, base2, len2);
	}

	private static <T> int gallopLeft(T key, T[] a, int base, int len, int hint,
									  Comparator<? super T> c) {
		int lastOfs = 0;
		int ofs = 1;
		if (c.compare(key, a[base + hint]) > 0) {
			// Gallop right until a[base+hint+lastOfs] < key <= a[base+hint+ofs]
			int maxOfs = len - hint;
			while (ofs < maxOfs && c.compare(key, a[base + hint + ofs]) > 0) {
				lastOfs = ofs;
				ofs = (ofs << 1) + 1;
				if (ofs <= 0)   // int overflow
					ofs = maxOfs;
			}
			if (ofs > maxOfs)
				ofs = maxOfs;

			// Make offsets relative to base
			lastOfs += hint;
			ofs += hint;
		} else { // key <= a[base + hint]
			// Gallop left until a[base+hint-ofs] < key <= a[base+hint-lastOfs]
			final int maxOfs = hint + 1;
			while (ofs < maxOfs && c.compare(key, a[base + hint - ofs]) <= 0) {
				lastOfs = ofs;
				ofs = (ofs << 1) + 1;
				if (ofs <= 0)   // int overflow
					ofs = maxOfs;
			}
			if (ofs > maxOfs)
				ofs = maxOfs;

			// Make offsets relative to base
			int tmp = lastOfs;
			lastOfs = hint - ofs;
			ofs = hint - tmp;
		}

		lastOfs++;
		while (lastOfs < ofs) {
			int m = lastOfs + ((ofs - lastOfs) >>> 1);

			if (c.compare(key, a[base + m]) > 0)
				lastOfs = m + 1;  // a[base + m] < key
			else
				ofs = m;          // key <= a[base + m]
		}
		return ofs;
	}

	private static <T> int gallopRight(T key, T[] a, int base, int len,
									   int hint, Comparator<? super T> c) {

		int ofs = 1;
		int lastOfs = 0;
		if (c.compare(key, a[base + hint]) < 0) {
			// Gallop left until a[b+hint - ofs] <= key < a[b+hint - lastOfs]
			int maxOfs = hint + 1;
			while (ofs < maxOfs && c.compare(key, a[base + hint - ofs]) < 0) {
				lastOfs = ofs;
				ofs = (ofs << 1) + 1;
				if (ofs <= 0)   // int overflow
					ofs = maxOfs;
			}
			if (ofs > maxOfs)
				ofs = maxOfs;

			// Make offsets relative to b
			int tmp = lastOfs;
			lastOfs = hint - ofs;
			ofs = hint - tmp;
		} else { // a[b + hint] <= key
			// Gallop right until a[b+hint + lastOfs] <= key < a[b+hint + ofs]
			int maxOfs = len - hint;
			while (ofs < maxOfs && c.compare(key, a[base + hint + ofs]) >= 0) {
				lastOfs = ofs;
				ofs = (ofs << 1) + 1;
				if (ofs <= 0)   // int overflow
					ofs = maxOfs;
			}
			if (ofs > maxOfs)
				ofs = maxOfs;

			// Make offsets relative to b
			lastOfs += hint;
			ofs += hint;
		}

		lastOfs++;
		while (lastOfs < ofs) {
			int m = lastOfs + ((ofs - lastOfs) >>> 1);

			if (c.compare(key, a[base + m]) < 0)
				ofs = m;          // key < a[b + m]
			else
				lastOfs = m + 1;  // a[b + m] <= key
		}
		return ofs;
	}

	private void mergeLo(int base1, int len1, int base2, int len2) {

		// Copy first run into temp array
		T[] a = this.a; // For performance
		T[] tmp = ensureCapacity(len1);
		System.arraycopy(a, base1, tmp, 0, len1);

		int cursor1 = 0;       // Indexes into tmp array
		int cursor2 = base2;   // Indexes int a
		int dest = base1;      // Indexes int a

		// Move first element of second run and deal with degenerate cases
		a[dest++] = a[cursor2++];
		if (--len2 == 0) {
			System.arraycopy(tmp, cursor1, a, dest, len1);
			return;
		}
		if (len1 == 1) {
			System.arraycopy(a, cursor2, a, dest, len2);
			a[dest + len2] = tmp[cursor1]; // Last elt of run 1 to end of merge
			return;
		}

		Comparator<? super T> c = this.c;  // Use local variable for performance
		int minGallop = this.minGallop;    //  "    "       "     "      "
		outer:
		while (true) {
			int count1 = 0; // Number of times in a row that first run won
			int count2 = 0; // Number of times in a row that second run won

			do {
				if (c.compare(a[cursor2], tmp[cursor1]) < 0) {
					a[dest++] = a[cursor2++];
					count2++;
					count1 = 0;
					if (--len2 == 0)
						break outer;
				} else {
					a[dest++] = tmp[cursor1++];
					count1++;
					count2 = 0;
					if (--len1 == 1)
						break outer;
				}
			} while ((count1 | count2) < minGallop);

			do {
				count1 = gallopRight(a[cursor2], tmp, cursor1, len1, 0, c);
				if (count1 != 0) {
					System.arraycopy(tmp, cursor1, a, dest, count1);
					dest += count1;
					cursor1 += count1;
					len1 -= count1;
					if (len1 <= 1) // len1 == 1 || len1 == 0
						break outer;
				}
				a[dest++] = a[cursor2++];
				if (--len2 == 0)
					break outer;

				count2 = gallopLeft(tmp[cursor1], a, cursor2, len2, 0, c);
				if (count2 != 0) {
					System.arraycopy(a, cursor2, a, dest, count2);
					dest += count2;
					cursor2 += count2;
					len2 -= count2;
					if (len2 == 0)
						break outer;
				}
				a[dest++] = tmp[cursor1++];
				if (--len1 == 1)
					break outer;
				minGallop--;
			} while (count1 >= MIN_GALLOP | count2 >= MIN_GALLOP);
			if (minGallop < 0)
				minGallop = 0;
			minGallop += 2;  // Penalize for leaving gallop mode
		}  // End of "outer" loop
		this.minGallop = minGallop < 1 ? 1 : minGallop;  // Write back to field

		if (len1 == 1) {
			System.arraycopy(a, cursor2, a, dest, len2);
			a[dest + len2] = tmp[cursor1]; //  Last elt of run 1 to end of merge
		} else if (len1 == 0) {
			throw new IllegalArgumentException(
					"Comparison method violates its general contract!");
		} else {
			System.arraycopy(tmp, cursor1, a, dest, len1);
		}
	}

	private void mergeHi(int base1, int len1, int base2, int len2) {

		// Copy second run into temp array
		T[] a = this.a; // For performance
		T[] tmp = ensureCapacity(len2);
		System.arraycopy(a, base2, tmp, 0, len2);

		int cursor1 = base1 + len1 - 1;  // Indexes into a
		int cursor2 = len2 - 1;          // Indexes into tmp array
		int dest = base2 + len2 - 1;     // Indexes into a

		// Move last element of first run and deal with degenerate cases
		a[dest--] = a[cursor1--];
		if (--len1 == 0) {
			System.arraycopy(tmp, 0, a, dest - (len2 - 1), len2);
			return;
		}
		if (len2 == 1) {
			dest -= len1;
			cursor1 -= len1;
			System.arraycopy(a, cursor1 + 1, a, dest + 1, len1);
			a[dest] = tmp[cursor2];
			return;
		}

		Comparator<? super T> c = this.c;  // Use local variable for performance
		int minGallop = this.minGallop;    //  "    "       "     "      "
		outer:
		while (true) {
			int count1 = 0; // Number of times in a row that first run won
			int count2 = 0; // Number of times in a row that second run won

			do {
				if (c.compare(tmp[cursor2], a[cursor1]) < 0) {
					a[dest--] = a[cursor1--];
					count1++;
					count2 = 0;
					if (--len1 == 0)
						break outer;
				} else {
					a[dest--] = tmp[cursor2--];
					count2++;
					count1 = 0;
					if (--len2 == 1)
						break outer;
				}
			} while ((count1 | count2) < minGallop);

			do {
				count1 = len1 - gallopRight(tmp[cursor2], a, base1, len1, len1 - 1, c);
				if (count1 != 0) {
					dest -= count1;
					cursor1 -= count1;
					len1 -= count1;
					System.arraycopy(a, cursor1 + 1, a, dest + 1, count1);
					if (len1 == 0)
						break outer;
				}
				a[dest--] = tmp[cursor2--];
				if (--len2 == 1)
					break outer;

				count2 = len2 - gallopLeft(a[cursor1], tmp, 0, len2, len2 - 1, c);
				if (count2 != 0) {
					dest -= count2;
					cursor2 -= count2;
					len2 -= count2;
					System.arraycopy(tmp, cursor2 + 1, a, dest + 1, count2);
					if (len2 <= 1)  // len2 == 1 || len2 == 0
						break outer;
				}
				a[dest--] = a[cursor1--];
				if (--len1 == 0)
					break outer;
				minGallop--;
			} while (count1 >= MIN_GALLOP | count2 >= MIN_GALLOP);
			if (minGallop < 0)
				minGallop = 0;
			minGallop += 2;  // Penalize for leaving gallop mode
		}  // End of "outer" loop
		this.minGallop = minGallop < 1 ? 1 : minGallop;  // Write back to field

		if (len2 == 1) {
			dest -= len1;
			cursor1 -= len1;
			System.arraycopy(a, cursor1 + 1, a, dest + 1, len1);
			a[dest] = tmp[cursor2];  // Move first elt of run2 to front of merge
		} else if (len2 == 0) {
			throw new IllegalArgumentException(
					"Comparison method violates its general contract!");
		} else {
			System.arraycopy(tmp, 0, a, dest - (len2 - 1), len2);
		}
	}

	private T[] ensureCapacity(int minCapacity) {
		if (tmp.length < minCapacity) {
			// Compute smallest power of 2 > minCapacity
			int newSize = minCapacity;
			newSize |= newSize >> 1;
			newSize |= newSize >> 2;
			newSize |= newSize >> 4;
			newSize |= newSize >> 8;
			newSize |= newSize >> 16;
			newSize++;

			if (newSize < 0) // Not bloody likely!
				newSize = minCapacity;
			else
				newSize = Math.min(newSize, a.length >>> 1);

			@SuppressWarnings({"unchecked", "UnnecessaryLocalVariable"})
			T[] newArray = (T[]) new Object[newSize];
			tmp = newArray;
		}
		return tmp;
	}

}
