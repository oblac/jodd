// Copyright (c) 2003-2012, Jodd Team (jodd.org). All Rights Reserved.

package jodd.util.collection;

import java.io.Serializable;
import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * Iterator over an array.
 */
public class ArrayIterator<E> implements Iterator<E>, Serializable {
	
	private E array[];
	private int ndx;
	private int endNdx;

	public ArrayIterator(E array[]) {
		this.array = array;
		ndx = 0;
		endNdx = array.length;
	}

	public ArrayIterator(E array[], int offset, int len) {
		this.array = array;
		ndx = offset;
		endNdx = offset + len;
	}

	public boolean hasNext() {
		return ndx < endNdx;
	}

	public E next() throws NoSuchElementException {
		if (ndx < endNdx) {
			ndx++;
			return array[ndx - 1];
		}
		throw new NoSuchElementException();
	}

	public void remove() throws UnsupportedOperationException {
		throw new UnsupportedOperationException();
	}
}