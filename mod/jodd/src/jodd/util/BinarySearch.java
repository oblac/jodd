// Copyright (c) 2003-2011, Jodd Team (jodd.org). All Rights Reserved.

package jodd.util;

import java.util.Comparator;
import java.util.List;

/**
 * Binary search wrapper over any type of user-defined collection.
 * It provides a finder for given element, but also finder of first
 * and last index in range of equal elements.
 */
public abstract class BinarySearch<E> {

	/**
	 * Creates binary search wrapper over a list of comparable elements.
	 */
	public static <T extends Comparable> BinarySearch<T> forList(final List<T> list) {
		return new BinarySearch<T>() {
			@Override
			@SuppressWarnings( {"unchecked"})
			protected int compare(int index, T element) {
				return list.get(index).compareTo(element);
			}

			@Override
			protected int getLastIndex() {
				return list.size() - 1;
			}
		};
	}

	/**
	 * Creates binary search wrapper over a list with given comparator.
	 */
	public static <T> BinarySearch<T> forList(final List<T> list, final Comparator<T> comparator) {
		return new BinarySearch<T>() {
			@Override
			@SuppressWarnings( {"unchecked"})
			protected int compare(int index, T element) {
				return comparator.compare(list.get(index), element);
			}

			@Override
			protected int getLastIndex() {
				return list.size() - 1;
			}
		};
	}

	// ---------------------------------------------------------------- abstract

	/**
	 * Compares element at <code>index</code> position with given object.
	 */
	protected abstract int compare(int index, E element);

	/**
	 * Returns index of last element in wrapped collection.
	 */
	protected abstract int getLastIndex();

	// ---------------------------------------------------------------- find

	/**
	 * Finds index of given element or negative value if element is not found.
	 */
	public int find(E element) {
		return find(element, 0, getLastIndex());
	}

	public int find(E element, int low) {
		return find(element, low, getLastIndex());
	}

	/**
	 * Finds index of given element in inclusive index range. Returns negative
	 * value if element is not found.
	 */
	public int find(E element, int low, int high) {
		while (low <= high) {
			int mid = (low + high) >>> 1;
			int delta = compare(mid, element);

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
	 * Finds very first index of given element or negative value if element is not found.
	 */
	public int findFirst(E o) {
		return findFirst(o, 0, getLastIndex());
	}

	public int findFirst(E o, int low) {
		return findFirst(o, low, getLastIndex());
	}

	/**
	 * Finds very first index of given element in inclusive index range. Returns negative
	 * value if element is not found.
	 */
	public int findFirst(E o, int low, int high) {

		int ndx = -1;
		while (low <= high) {
			int mid = (low + high) >>> 1;
			int delta = compare(mid, o);

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
	 * Finds very last index of given element or negative value if element is not found.
	 */
	public int findLast(E o) {
		return findLast(o, 0, getLastIndex());
	}

	public int findLast(E o, int low) {
		return findLast(o, low, getLastIndex());
	}

	/**
	 * Finds very last index of given element in inclusive index range. Returns negative
	 * value if element is not found.
	 */
	public int findLast(E o, int low, int high) {
		int ndx = -1;
		while (low <= high) {
			int mid = (low + high) >>> 1;
			int delta = compare(mid, o);

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
