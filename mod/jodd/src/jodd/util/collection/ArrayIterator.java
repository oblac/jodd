// Copyright (c) 2003-2012, Jodd Team (jodd.org). All Rights Reserved.

package jodd.util.collection;

import java.io.Serializable;
import java.util.Iterator;
import java.util.NoSuchElementException;


/**
 * Iterator over an array.
 */
public class ArrayIterator implements Iterator, Serializable {
	
	private Object array[];
	private int currentIndex;
	private int length;
	private int offset;

	public ArrayIterator(Object aobj[]) {
		array = aobj;
		currentIndex = 0;
		offset = 0;
		length = array.length;
	}

	public ArrayIterator(Object aobj[], int offset, int len) {
		array = aobj;
		currentIndex = offset;
		this.offset = offset;
		length = len;
	}

	public boolean hasNext() {
		return currentIndex < length + offset;
	}

	public Object next() throws NoSuchElementException {
		try {
			currentIndex++;
			return array[currentIndex - 1];
		} catch (ArrayIndexOutOfBoundsException ignored) {
			throw new NoSuchElementException();
		}
	}

	public void remove() throws UnsupportedOperationException {
		throw new UnsupportedOperationException();
	}
}
