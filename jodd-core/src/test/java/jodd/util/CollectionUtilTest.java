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

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests for class {@link CollectionUtil}.
 */
class CollectionUtilTest {

	@Nested
	@DisplayName("tests for CollectionUtil#asCollection - method")
	class AsCollection {

		@Test
		void testAsCollection_with_null() throws Exception {
			assertThrows(NullPointerException.class, () -> {CollectionUtil.collectionOf(null);});
		}

		@Test
		void testAsCollection_with_empty_iterator() throws Exception {

			final Iterator<Object> input = Arrays.asList().iterator();

			final Collection<Object> actual = CollectionUtil.collectionOf(input);

			// asserts
			assertNotNull(actual);
			assertEquals(0, actual.size());
		}

		@Test
		void testAsCollection_with_data() throws Exception {
			final Collection<Integer> expected = Arrays.asList(1,2,3,4,5,6,7);
			final Iterator<Integer> input = expected.iterator();

			final Collection<Integer> actual = CollectionUtil.collectionOf(input);

			// asserts
			assertNotNull(actual);
			assertEquals(expected.size(), actual.size());
			assertArrayEquals(expected.toArray(), actual.toArray());
		}

	}

	@Nested
	@DisplayName("tests for CollectionUtil#asIterator - method")
	class AsIterator {

		@Test
		void testAsIterator_with_empty_enumration() throws Exception {

			final Hashtable<String, String> input = new Hashtable<>();

			final Iterator<String> actual = CollectionUtil.asIterator(input.keys());

			// asserts
			assertNotNull(actual);
			assertFalse(actual.hasNext());
			assertThrows(NoSuchElementException.class, () -> {actual.next();});
			assertThrows(UnsupportedOperationException.class, () -> {actual.remove();});
		}

		@Test
		void testAsIterator_with_data() throws Exception {

			final Hashtable<String, String> input = new Hashtable<>();
			input.put("jodd", "makes fun!");
			input.put("headline", "The Unbearable Lightness of Java");
			input.put("aim", "And enjoy the coding");


			final Iterator<String> actual = CollectionUtil.asIterator(input.keys());

			// asserts
			assertNotNull(actual);
			// next #1
			assertTrue(actual.hasNext());
			String key = actual.next();
			assertTrue(input.containsKey(key));
			// next #2
			assertTrue(actual.hasNext());
			key = actual.next();
			assertTrue(input.containsKey(key));
			// next #3
			assertTrue(actual.hasNext());
			key = actual.next();
			assertTrue(input.containsKey(key));

			// no more elements
			assertFalse(actual.hasNext());
			assertThrows(NoSuchElementException.class, () -> {actual.next();});
		}

	}

	@Nested
	@DisplayName("tests for CollectionUtil#asEnumeration - method")
	class AsEnumeration {

		@Test
		void testAsEnumeration_with_empty_iterator() throws Exception {

			final Iterator<Integer> input = new ArrayList<Integer>().iterator();

			final Enumeration<Integer> actual = CollectionUtil.asEnumeration(input);

			// asserts
			assertFalse(actual.hasMoreElements());
			assertThrows(NoSuchElementException.class, () -> {actual.nextElement();});
		}

		@Test
		void testAsEnumeration_with_data() throws Exception {

			final Iterator<Integer> input = Arrays.asList(1,2,3).iterator();

			final Enumeration<Integer> actual = CollectionUtil.asEnumeration(input);

			// asserts
			assertNotNull(actual);
			// next #1
			assertTrue(actual.hasMoreElements());
			assertEquals(Integer.valueOf(1), actual.nextElement());
			// next #2
			assertTrue(actual.hasMoreElements());
			assertEquals(Integer.valueOf(2), actual.nextElement());
			// next #3
			assertTrue(actual.hasMoreElements());
			assertEquals(Integer.valueOf(3), actual.nextElement());
			// no more elements
			assertFalse(actual.hasMoreElements());
			assertThrows(NoSuchElementException.class, () -> {actual.nextElement();});
		}

	}

	@Nested
	@DisplayName("tests for CollectionUtil#asStream(Iterator<T> sourceIterator)")
	class AsStream {

		@Test
		void testAsStream_with_empty_iterator() throws Exception {
			final Iterator input = Collections.EMPTY_LIST.iterator();

			final Stream actual = CollectionUtil.streamOf(input);

			// asserts
			assertNotNull(actual);
			assertEquals(0L, actual.count());
		}

		@Test
		void testAsStream_with_non_empty_iterator() throws Exception {
			final Iterator<Integer> input = Arrays.asList(1,2,3).iterator();

			final Stream<Integer> actual = CollectionUtil.streamOf(input);

			// asserts
			assertNotNull(actual);
			assertEquals(3L, actual.count());
			assertThrows(IllegalStateException.class, () -> {actual.filter(p -> 1 == p);});
		}
	}

}