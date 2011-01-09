// Copyright (c) 2003-2011, Jodd Team (jodd.org). All Rights Reserved.

package jodd.util.collection;

import java.util.Collection;
import java.util.Iterator;
import java.util.NoSuchElementException;

public class JoinedIterator implements Iterator {

	private Iterator mIterators[];
	private int mCurrentIterator;

	public JoinedIterator(Collection<Iterator> collection) {
		mIterators = new Iterator[collection.size()];
		mIterators = collection.toArray(mIterators);
	}

	public JoinedIterator(Iterator iterator, Iterator iterator1) {
		mIterators = (new Iterator[] {iterator, iterator1});
	}

	public JoinedIterator(Iterator aiterator[]) {
		mIterators = aiterator;
	}

	public boolean hasNext() {
		if (mCurrentIterator >= mIterators.length) {
			return false;
		}
		if (mIterators[mCurrentIterator].hasNext()) {
			return true;
		}
		mCurrentIterator++;
		if (mCurrentIterator >= mIterators.length) {
			return false;
		}
		else {
			return mIterators[mCurrentIterator].hasNext();
		}
	}

	public Object next() {
		if (!hasNext()) {
			throw new NoSuchElementException();
		} else {
			return mIterators[mCurrentIterator].next();
		}
	}

	public void remove() {
		if (mCurrentIterator >= mIterators.length) {
			throw new NoSuchElementException();
		} else {
			mIterators[mCurrentIterator].remove();
		}
	}
}
