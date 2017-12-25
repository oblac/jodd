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

import static jodd.util.CharArraySequence.of;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;


class CharSequenceUtilTest {

	@Test
	void testEqualsChars() {
		assertTrue(CharSequenceUtil.equals(of('a', 'b'), of('a', 'b')));
		assertFalse(CharSequenceUtil.equals(of('A', 'B'), of('a', 'b')));
		assertTrue(CharSequenceUtil.equals(of(), of()));
		assertFalse(CharSequenceUtil.equals(of('a'), of('a', 'b')));
		assertFalse(CharSequenceUtil.equals(of('a', 'b'), of('a')));
	}

	@Test
	void testEqualsSeqChars() {
		assertTrue(CharSequenceUtil.equals("ab", of('a', 'b')));
		assertFalse(CharSequenceUtil.equals("AB", of('a', 'b')));
		assertTrue(CharSequenceUtil.equals("", of()));
		assertFalse(CharSequenceUtil.equals("a", of('a', 'b')));
		assertFalse(CharSequenceUtil.equals("ab", of('a')));
	}

	@Test
	void testEqualsSeq() {
		assertTrue(CharSequenceUtil.equals("ab", "ab"));
		assertFalse(CharSequenceUtil.equals("AB", "ab"));
		assertTrue(CharSequenceUtil.equals("", ""));
		assertFalse(CharSequenceUtil.equals("a", "ab"));
		assertFalse(CharSequenceUtil.equals("ab", "a"));
	}

	@Test
	void testEqualsCharsToLowercase() {
		assertTrue(CharSequenceUtil.equalsToLowercase("ab", of('a', 'b')));
		assertTrue(CharSequenceUtil.equalsToLowercase("AB", of('a', 'b')));
		assertTrue(CharSequenceUtil.equalsToLowercase("", of()));
		assertFalse(CharSequenceUtil.equalsToLowercase("a", of('a', 'b')));
		assertFalse(CharSequenceUtil.equalsToLowercase("ab", of('a')));
	}

	@Test
	void testEqualsSeqsToLowercase() {
		assertTrue(CharSequenceUtil.equalsToLowercase("ab", "ab"));
		assertTrue(CharSequenceUtil.equalsToLowercase("AB", "ab"));
		assertTrue(CharSequenceUtil.equalsToLowercase("", ""));
		assertFalse(CharSequenceUtil.equalsToLowercase("a", "ab"));
		assertFalse(CharSequenceUtil.equalsToLowercase("ab", "a"));
	}

	@Test
	void testStartsWithLowercase() {
		assertTrue(CharSequenceUtil.startsWithLowercase("ab", of('a', 'b')));
		assertTrue(CharSequenceUtil.startsWithLowercase("AB", of('a', 'b')));
		assertTrue(CharSequenceUtil.startsWithLowercase("", of()));
		assertFalse(CharSequenceUtil.startsWithLowercase("a", of('a', 'b')));
		assertTrue(CharSequenceUtil.startsWithLowercase("ab", of('a')));
	}

	@Test
	void testEqualsSeqCharsIgnoreCase() {
		assertTrue(CharSequenceUtil.equalsIgnoreCase("ab", of('a', 'b')));
		assertTrue(CharSequenceUtil.equalsIgnoreCase("AB", of('a', 'b')));
		assertTrue(CharSequenceUtil.equalsIgnoreCase("", of()));
		assertFalse(CharSequenceUtil.equalsIgnoreCase("a", of('a', 'b')));
		assertFalse(CharSequenceUtil.equalsIgnoreCase("ab", of('a')));
	}

	@Test
	void testEqualsSeqIgnoreCase() {
		assertTrue(CharSequenceUtil.equalsIgnoreCase("ab", "ab"));
		assertTrue(CharSequenceUtil.equalsIgnoreCase("AB", "ab"));
		assertTrue(CharSequenceUtil.equalsIgnoreCase("", ""));
		assertFalse(CharSequenceUtil.equalsIgnoreCase("a", "ab"));
		assertFalse(CharSequenceUtil.equalsIgnoreCase("ab", "a"));
	}

	@Nested
	@DisplayName("tests for CharSequenceUtil#findFirstEqual with CharSequence")
	class FindFirstEqual_CharSequence {

		@Test
		void no_found() {
			final int actual = CharSequenceUtil.findFirstEqual("Build with common sense to make things simple, but not simpler.", 0, "Jj" );

			// asserts
			assertEquals(-1, actual);
		}

		@Test
		void no_found_index_out_of_source() {
			final int actual = CharSequenceUtil.findFirstEqual("Build with common sense to make things simple, but not simpler.", 65, "Jj" );

			// asserts
			assertEquals(-1, actual);
		}

		@Test
		void with_found() {
			final int actual = CharSequenceUtil.findFirstEqual("Jodd makes fun!", 0, "Da" );

			// asserts
			assertEquals(6, actual);
		}

		@Test
		void with_found_non_nill_index() {
			final int actual = CharSequenceUtil.findFirstEqual("Jodd makes fun!", 5, "Jn" );

			// asserts
			assertEquals(13, actual);
		}
	}

	@Nested
	@DisplayName("tests for CharSequenceUtil#findFirstEqual with char[]")
	class FindFirstEqual_CharArray {

		@Test
		void no_found() {
			final int actual = CharSequenceUtil.findFirstEqual("Build with common sense to make things simple, but not simpler.".toCharArray(), 0, 'J' );

			// asserts
			assertEquals(-1, actual);
		}

		@Test
		void no_found_index_out_of_source() {
			final int actual = CharSequenceUtil.findFirstEqual("Build with common sense to make things simple, but not simpler.".toCharArray(), 65, 'B' );

			// asserts
			assertEquals(-1, actual);
		}

		@Test
		void with_found() {
			final int actual = CharSequenceUtil.findFirstEqual("Jodd makes fun!".toCharArray(), 0, 'n' );

			// asserts
			assertEquals(13, actual);
		}

		@Test
		void with_found_non_nill_index() {
			final int actual = CharSequenceUtil.findFirstEqual("Jodd makes fun!".toCharArray(), 5, 's' );

			// asserts
			assertEquals(9, actual);
		}
	}

	@Nested
	@DisplayName("tests for CharSequenceUtil#findFirstDiff with CharSequence")
	class FindFirstDiff_CharSequence {

		@Test
		void no_found() {
			final int actual = CharSequenceUtil.findFirstDiff("Build with common sense to make things simple, but not simpler.", 0, "Build with common sense to make things simple, but not simpler." );

			// asserts
			assertEquals(-1, actual);
		}

		@Test
		void no_found_index_out_of_source() {
			final int actual = CharSequenceUtil.findFirstDiff("Build with common sense to make things simple, but not simpler.", 65, "Jj" );

			// asserts
			assertEquals(-1, actual);
		}

		@Test
		void with_found() {
			final int actual = CharSequenceUtil.findFirstDiff("Jodd makes fun!", 0, "Joddmakes" );

			// asserts
			assertEquals(4, actual);
		}

		@Test
		void with_found_non_nill_index() {
			final int actual = CharSequenceUtil.findFirstDiff("Jodd makes fun!", 5, "Jodd maK" );

			// asserts
			assertEquals(7, actual);
		}
	}

	@Nested
	@DisplayName("tests for CharSequenceUtil#findFirstDiff with char[]")
	class FindFirstDiff_CharArray {

		@Test
		void no_found() {
			final int actual = CharSequenceUtil.findFirstDiff("JJJJ".toCharArray(), 0, 'J');

			// asserts
			assertEquals(-1, actual);
		}

		@Test
		void no_found_index_out_of_source() {
			final int actual = CharSequenceUtil.findFirstDiff("Build with common sense to make things simple, but not simpler.".toCharArray(), 65, 'Z' );

			// asserts
			assertEquals(-1, actual);
		}

		@Test
		void with_found() {
			final int actual = CharSequenceUtil.findFirstDiff("Jodd makes fun!".toCharArray(), 0, 'o' );

			// asserts
			assertEquals(0, actual);
		}

		@Test
		void with_found_non_nill_index() {
			final int actual = CharSequenceUtil.findFirstDiff("Jodd makes fun!".toCharArray(), 5, 'n' );

			// asserts
			assertEquals(5, actual);
		}
	}

}
