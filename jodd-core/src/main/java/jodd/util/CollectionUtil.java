// Copyright (c) 2003-present, Jodd Team (jodd.org). All Rights Reserved.

package jodd.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;

/**
 * Some collection utilities.
 */
public class CollectionUtil {

	/**
	 * Adapt the specified <code>Iterator</code> to the
	 * <code>Enumeration</code> interface.
	 */
	public static <E> Enumeration<E> asEnumeration(final Iterator<E> iter) {
		return new Enumeration<E>() {
			public boolean hasMoreElements() {
				return iter.hasNext();
			}

			public E nextElement() {
				return iter.next();
			}
		};
	}

	/**
	 * Adapt the specified <code>Enumeration</code> to the <code>Iterator</code> interface.
	 */
	public static <E> Iterator<E> asIterator(final Enumeration<E> e) {
		return new Iterator<E>() {
			public boolean hasNext() {
				return e.hasMoreElements();
			}

			public E next() {
				return e.nextElement();
			}

			public void remove() {
				throw new UnsupportedOperationException();
			}
		};
	}

	/**
	 * Returns a collection containing all elements of the iterator.
	 */
	public static <T> Collection<T> asCollection(final Iterator<? extends T> iterator) {
		List<T> list = new ArrayList<T>();
		while (iterator.hasNext()) {
			list.add(iterator.next());
		}
		return list;
	}

}