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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

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
			@Override
			public boolean hasMoreElements() {
				return iter.hasNext();
			}

			@Override
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
			@Override
			public boolean hasNext() {
				return e.hasMoreElements();
			}

			@Override
			public E next() {
				if (!hasNext()) {
					throw new NoSuchElementException();
				}
				return e.nextElement();
			}

			@Override
			public void remove() {
				throw new UnsupportedOperationException();
			}
		};
	}

	/**
	 * Returns a collection containing all elements of the iterator.
	 */
	public static <T> Collection<T> collectionOf(final Iterator<? extends T> iterator) {
		final List<T> list = new ArrayList<>();
		while (iterator.hasNext()) {
			list.add(iterator.next());
		}
		return list;
	}

	/**
	 * Converts iterator to a stream.
	 */
	public static <T> Stream<T> streamOf(final Iterator<T> iterator) {
		return StreamSupport.stream(((Iterable<T>) () -> iterator).spliterator(), false);
	}

	/**
	 * Converts interable to a non-parallel stream.
	 */
	public static <T> Stream<T> streamOf(final Iterable<T> iterable) {
		return StreamSupport.stream(iterable.spliterator(), false);
	}

	/**
	 * Wraps an iterator as a stream.
	 */
	public static <T> Stream<T> parallelStreamOf(final Iterator<T> iterator) {
		return StreamSupport.stream(((Iterable<T>) () -> iterator).spliterator(), true);
	}

	/**
	 * Wraps an iterator as a stream.
	 */
	public static <T> Stream<T> parallelStreamOf(final Iterable<T> iterable) {
		return StreamSupport.stream(iterable.spliterator(), true);
	}


}