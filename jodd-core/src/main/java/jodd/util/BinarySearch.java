// Copyright (c) 2003-present, Jodd Team (http://jodd.org)
// All rights reserved.
//
// Redistribution and use in source and binary forms, with or without
// modification, are permitted provided that the following conditions are met:
//
// 1. Redistributions of source code must retain the above copyright notice,
// this list of conditions and the following disclaimer.
//
// 2. Redistributions in binary form must reproduce the above copyright
// notice, this list of conditions and the following disclaimer in the
// documentation and/or other materials provided with the distribution.
//
// THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
// AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
// IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
// ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE
// LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
// CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
// SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
// INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
// CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
// ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
// POSSIBILITY OF SUCH DAMAGE.

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

	/**
	 * Creates binary search wrapper over an array.
	 */
	public static <T extends Comparable> BinarySearch<T> forArray(final T[] array) {
		return new BinarySearch<T>() {
			@Override
			@SuppressWarnings( {"unchecked"})
			protected int compare(int index, T element) {
				return array[index].compareTo(element);
			}

			@Override
			protected int getLastIndex() {
				return array.length - 1;
			}
		};
	}

	/**
	 * Creates binary search wrapper over an array with given comparator.
	 */
	public static <T> BinarySearch<T> forArray(final T[] array, final Comparator<T> comparator) {
		return new BinarySearch<T>() {
			@Override
			@SuppressWarnings( {"unchecked"})
			protected int compare(int index, T element) {
				return comparator.compare(array[index], element);
			}

			@Override
			protected int getLastIndex() {
				return array.length - 1;
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