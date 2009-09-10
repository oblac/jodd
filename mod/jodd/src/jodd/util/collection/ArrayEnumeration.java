// Copyright (c) 2003-2009, Jodd Team (jodd.org). All Rights Reserved.

package jodd.util.collection;

import java.io.Serializable;
import java.util.Enumeration;
import java.util.NoSuchElementException;

/**
 * Enumeration over an array. 
 */
public class ArrayEnumeration implements Enumeration, Serializable {

	private Object array[];
	private int ndx;
	private int to;

	public ArrayEnumeration(Object arr[]) {
		this(arr, 0, arr.length);
	}

	public ArrayEnumeration(Object arr[], int from) {
		this(arr, from, arr.length);
	}

	public ArrayEnumeration(Object arr[], int from, int to) {
		array = arr;
		ndx = from;
		this.to = to;
	}

	public boolean hasMoreElements() {
		return ndx < to;
	}

	public Object nextElement()	throws NoSuchElementException {
		if (ndx < to) {
			return array[ndx++];
		}
		throw new NoSuchElementException();
	}
}