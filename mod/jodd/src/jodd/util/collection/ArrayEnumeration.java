// Copyright (c) 2003-2012, Jodd Team (jodd.org). All Rights Reserved.

package jodd.util.collection;

import java.io.Serializable;
import java.util.Enumeration;
import java.util.NoSuchElementException;

/**
 * Enumeration over an array. 
 */
public class ArrayEnumeration<E> implements Enumeration<E>, Serializable {

	private E array[];
	private int ndx;
	private int endNdx;

	public ArrayEnumeration(E arr[]) {
		this(arr, 0, arr.length);
	}

	public ArrayEnumeration(E arr[], int offset, int length) {
		array = arr;
		ndx = offset;
		this.endNdx = offset + length;
	}

	public boolean hasMoreElements() {
		return ndx < endNdx;
	}

	public E nextElement()	throws NoSuchElementException {
		if (ndx < endNdx) {
			return array[ndx++];
		}
		throw new NoSuchElementException();
	}
}