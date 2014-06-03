// Copyright (c) 2003-2014, Jodd Team (jodd.org). All Rights Reserved.

package jodd.util;

/**
 * Abstract binary search. It is more abstract then {@link jodd.util.BinarySearch}.
 */
public abstract class BinarySearchBase {

	/**
	 * Compares element at <code>index</code> position with the target.
	 */
	protected abstract int compare(int index);

	// ---------------------------------------------------------------- find

	/**
	 * Finds index of given element in inclusive index range. Returns negative
	 * value if element is not found.
	 */
	public int find(int low, int high) {
		while (low <= high) {
			int mid = (low + high) >>> 1;
			int delta = compare(mid);

			if (delta < 0) {
				low = mid + 1;
			} else if (delta > 0) {
				high = mid - 1;
			} else {
				return mid;
			}
		}
		// not found
		return -(low + 1);
	}

	// ---------------------------------------------------------------- first

	/**
	 * Finds very first index of given element in inclusive index range. Returns negative
	 * value if element is not found.
	 */
	public int findFirst(int low, int high) {

		int ndx = -1;
		while (low <= high) {
			int mid = (low + high) >>> 1;
			int delta = compare(mid);

			if (delta < 0) {
				low = mid + 1;
			} else {
				if (delta == 0) {
					ndx = mid;
				}
				high = mid - 1;
			}
		}

		if (ndx == -1) {
			return -(low + 1);
		}

		return ndx;
	}

	// ---------------------------------------------------------------- last

	/**
	 * Finds very last index of given element in inclusive index range. Returns negative
	 * value if element is not found.
	 */
	public int findLast(int low, int high) {
		int ndx = -1;
		while (low <= high) {
			int mid = (low + high) >>> 1;
			int delta = compare(mid);

			if (delta > 0) {
				high = mid - 1;
			} else {
				if (delta == 0) {
					ndx = mid;
				}
				low = mid + 1;
			}
		}

		if (ndx == -1) {
			return -(low + 1);
		}

		return ndx;
	}

}