// Copyright (c) 2003-2011, Jodd Team (jodd.org). All Rights Reserved.

package jodd.util.collection;

import java.io.Serializable;
import java.util.Iterator;
import java.util.NoSuchElementException;


/**
 * Iterator over an array.
 */
public class ArrayIterator implements Iterator, Serializable {
	
	private Object mArray[];
	private int mCurrentElement;
	private int mArrayLength;
	private int mOffset;	

	public ArrayIterator(Object aobj[]) {
		mArray = aobj;
		mCurrentElement = 0;
		mOffset = 0;
		mArrayLength = mArray.length;
	}

	public ArrayIterator(Object aobj[], int i, int j) {
		mArray = aobj;
		mCurrentElement = i;
		mOffset = i;
		mArrayLength = (j - i) + 1;
	}

	public boolean hasNext() {
		return mCurrentElement < mArrayLength + mOffset;
	}

	public Object next() throws NoSuchElementException {
		try {
			mCurrentElement++;
			return mArray[mCurrentElement - 1];
		} catch (ArrayIndexOutOfBoundsException _ex) {
			throw new NoSuchElementException();
		}
	}

	public void remove() throws UnsupportedOperationException {
		throw new UnsupportedOperationException();
	}
}
