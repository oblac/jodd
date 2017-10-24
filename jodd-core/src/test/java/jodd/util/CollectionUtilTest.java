package jodd.util;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.NoSuchElementException;

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
			assertThrows(NullPointerException.class, () -> {CollectionUtil.asCollection(null);});
		}

		@Test
		void testAsCollection_with_empty_iterator() throws Exception {

			final Iterator<Object> input = Arrays.asList().iterator();

			final Collection<Object> actual = CollectionUtil.asCollection(input);

			// asserts
			assertNotNull(actual);
			assertEquals(0, actual.size());
		}

		@Test
		void testAsCollection_with_data() throws Exception {
			final Collection<Integer> expected = Arrays.asList(1,2,3,4,5,6,7);
			final Iterator<Integer> input = expected.iterator();

			final Collection<Integer> actual = CollectionUtil.asCollection(input);

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

			Hashtable<String, String> input = new Hashtable<>();

			Iterator<String> actual = CollectionUtil.asIterator(input.keys());

			// asserts
			assertNotNull(actual);
			assertFalse(actual.hasNext());
			assertThrows(NoSuchElementException.class, () -> {actual.next();});
			assertThrows(UnsupportedOperationException.class, () -> {actual.remove();});
		}

		@Test
		void testAsIterator_with_data() throws Exception {

			Hashtable<String, String> input = new Hashtable<>();
			input.put("jodd", "makes fun!");
			input.put("headline", "The Unbearable Lightness of Java");
			input.put("aim", "And enjoy the coding");


			Iterator<String> actual = CollectionUtil.asIterator(input.keys());

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

			Iterator<Integer> input = new ArrayList<Integer>().iterator();

			Enumeration<Integer> actual = CollectionUtil.asEnumeration(input);

			// asserts
			assertFalse(actual.hasMoreElements());
			assertThrows(NoSuchElementException.class, () -> {actual.nextElement();});
		}

		@Test
		void testAsEnumeration_with_data() throws Exception {

			Iterator<Integer> input = Arrays.asList(1,2,3).iterator();

			Enumeration<Integer> actual = CollectionUtil.asEnumeration(input);

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

}