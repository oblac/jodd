// Copyright (c) 2003-2009, Jodd Team (jodd.org). All Rights Reserved.

package jodd.util.collection;

import java.io.Serializable;
import java.util.Enumeration;
import java.util.NoSuchElementException;

/**
 * Enumeration over an array. 
 */
public class ArrayEnumeration implements Enumeration, Serializable {

	private Object mArray[];
	private int mCurrentElement;
	private int mArrayLength;
	private int mOffset;	
	
	public ArrayEnumeration(Object aobj[]) {
		mArray = aobj;
		mCurrentElement = 0;
		mOffset = 0;
		mArrayLength = mArray.length;
	}

	public ArrayEnumeration(Object aobj[], int i, int j) {
		mArray = aobj;
		mCurrentElement = i;
		mOffset = i;
		mArrayLength = (j - i) + 1;
	}

	public boolean hasMoreElements() {
		return mCurrentElement < mArrayLength + mOffset;
	}

	public Object nextElement()	throws NoSuchElementException {
		try {
			mCurrentElement++;
			return mArray[mCurrentElement - 1];
		} catch (ArrayIndexOutOfBoundsException aiofbex) {
			throw new NoSuchElementException();
		}
	}
}