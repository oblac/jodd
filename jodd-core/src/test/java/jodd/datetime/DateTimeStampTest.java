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

package jodd.datetime;

import jodd.util.ArraysUtil;
import jodd.util.collection.IntArrayList;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class DateTimeStampTest {

	@Nested
	@DisplayName("tests for DateTimeStamp#compareTo")
	@TestInstance(TestInstance.Lifecycle.PER_CLASS)
	class CompareTo {

		@ParameterizedTest
		@MethodSource("testData_testCompareTo")
		void testCompareTo(final int expected, final DateTimeStamp input_1, final DateTimeStamp input_2) {
			final int actual = input_1.compareTo(input_2);

			// asserts
			assertEquals(expected, actual);
		}

		private Collection<Arguments> testData_testCompareTo () {
			final List<Arguments> params = new ArrayList<>();

			{
				params.add(Arguments.of(0, new DateTimeStamp(2017,12,12,11,56,23,11),
						new DateTimeStamp(2017,12,12,11,56,23,11)));
			}

			{
				params.add(Arguments.of(-1, new DateTimeStamp(2017,12,12,11,56,23,11),
						new DateTimeStamp(2017,12,12,11,56,23,12)));
			}

			{
				params.add(Arguments.of(1, new DateTimeStamp(2017,12,12,11,56,23,12),
						new DateTimeStamp(2017,12,12,11,56,23,11)));
			}

			return params;
		}
	}

	@Nested
	@DisplayName("tests for DateTimeStamp#equals")
	@TestInstance(TestInstance.Lifecycle.PER_CLASS)
	class Equals {

		@ParameterizedTest
		@MethodSource("testData_testEquals")
		void testEquals(final boolean expected, final DateTimeStamp input_1, final Object input_2) {
			final boolean actual = input_1.equals(input_2);

			// asserts
			assertEquals(expected, actual);
		}

		private Collection<Arguments> testData_testEquals () {
			final List<Arguments> params = new ArrayList<>();

			{
				params.add(Arguments.of(true, new DateTimeStamp(2017,12,12,11,56,23,11),
						new DateTimeStamp(2017,12,12,11,56,23,11)));
			}

			{
				final DateTimeStamp dateTimeStamp = new DateTimeStamp(2017, 12, 12, 11, 56, 23, 11);
				params.add(Arguments.of(true, dateTimeStamp, dateTimeStamp));
			}

			{
				params.add(Arguments.of(false, new DateTimeStamp(2017,12,12,11,56,23,12),
						null));
			}

			{
				params.add(Arguments.of(false, new DateTimeStamp(2017,12,12,11,56,23,12),
						new Date()));
			}

			{
				params.add(Arguments.of(false, new DateTimeStamp(2017,12,12,11,56,23,12),
						new DateTimeStamp(2017,12,12,11,56,23,13)));
			}

			return params;
		}
	}

	@Test
	void testToString() {

		final String expected = "2017-12-12 11:56:23.12";
		final DateTimeStamp input = new DateTimeStamp(2017,12,12,11,56,23,12);

		final String actual = input.toString();

		// asserts
		assertEquals(expected, actual);
	}

	@Test
	void testClone() throws Exception {

		final DateTimeStamp input = new DateTimeStamp(2017,12,12,11,56,23,12);

		final DateTimeStamp clone = input.clone();

		// asserts
		assertEquals(input.getYear(), clone.getYear());
		assertEquals(input.getMonth(), clone.getMonth());
		assertEquals(input.getDay(), clone.getDay());
		assertEquals(input.getHour(), clone.getHour());
		assertEquals(input.getMinute(), clone.getMinute());
		assertEquals(input.getSecond(), clone.getSecond());
		assertEquals(input.getMillisecond(), clone.getMillisecond());
	}

	@Nested
	@DisplayName("tests for DateTimeStamp#hashCode")
	@TestInstance(TestInstance.Lifecycle.PER_CLASS)
	class HashCode {

		@ParameterizedTest
		@MethodSource("testdata_testHashCode")
		void testHashCode(final boolean equals, final DateTimeStamp input_1 , final DateTimeStamp input_2) {

			final int hash_1 = input_1.hashCode();
			final int hash_2 = input_2.hashCode();

			assertTrue(equals == (hash_1 == hash_2));
		}

		private Collection<Arguments> testdata_testHashCode() throws Exception {
			final List<Arguments> params = new ArrayList<>();

			// same hashcode
			{
				final DateTimeStamp input = new DateTimeStamp(2017,12,12,11,56,23,12);
				params.add(Arguments.of(true, input, input.clone()));
			}

			// diff : year
			{
				final DateTimeStamp input_1 = new DateTimeStamp(2017,12,12,11,56,23,12);
				final DateTimeStamp input_2 = new DateTimeStamp(2018,12,12,11,56,23,12);
				params.add(Arguments.of(false, input_1, input_2));
			}

			// diff : month
			{
				final DateTimeStamp input_1 = new DateTimeStamp(2017,12,12,11,56,23,12);
				final DateTimeStamp input_2 = new DateTimeStamp(2017,11,12,11,56,23,12);
				params.add(Arguments.of(false, input_1, input_2));
			}

			// diff : day
			{
				final DateTimeStamp input_1 = new DateTimeStamp(2017,12,12,11,56,23,12);
				final DateTimeStamp input_2 = new DateTimeStamp(2017,12,13,11,56,23,12);
				params.add(Arguments.of(false, input_1, input_2));
			}

			// diff : hour
			{
				final DateTimeStamp input_1 = new DateTimeStamp(2017,12,12,11,56,23,12);
				final DateTimeStamp input_2 = new DateTimeStamp(2017,12,12,10,56,23,12);
				params.add(Arguments.of(false, input_1, input_2));
			}

			// diff : minute
			{
				final DateTimeStamp input_1 = new DateTimeStamp(2017,12,12,11,56,23,12);
				final DateTimeStamp input_2 = new DateTimeStamp(2017,12,12,11,57,23,12);
				params.add(Arguments.of(false, input_1, input_2));
			}

			// diff : second
			{
				final DateTimeStamp input_1 = new DateTimeStamp(2017,12,12,11,56,23,12);
				final DateTimeStamp input_2 = new DateTimeStamp(2017,12,12,11,56,22,12);
				params.add(Arguments.of(false, input_1, input_2));
			}

			// diff : ms
			{
				final DateTimeStamp input_1 = new DateTimeStamp(2017,12,12,11,56,23,12);
				final DateTimeStamp input_2 = new DateTimeStamp(2017,12,12,11,56,23,11);
				params.add(Arguments.of(false, input_1, input_2));
			}

			return params;

		}
	}

}