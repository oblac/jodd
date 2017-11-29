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
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class NameValueTest {

	@DisplayName("tests for NameValue#equals")
	@Nested
	@TestInstance(TestInstance.Lifecycle.PER_CLASS)
	class Equals {

		@ParameterizedTest
		@MethodSource("testdata_testEquals")
		void testEquals (final boolean expected, final NameValue input, final Object obj) {
			assertEquals(expected, input.equals(obj));
		}

		private Collection<Arguments> testdata_testEquals() {
			final List<Arguments> params = new ArrayList<>();

			// ###  equals -> true ###
			
			// due to same values and names
			{
				final NameValue<Integer, String> input = NameValue.of(1, "one");
				final NameValue<Integer, String> equals_check = new NameValue<>(1, "one");
				params.add(Arguments.of(true, input, equals_check));
			}

			// same instance
			{
				final NameValue<Integer, String> input = NameValue.of(1, "one");
				final NameValue<Integer, String> equals_check = input;
				params.add(Arguments.of(true, input, equals_check));
			}

			// ###  equals -> false ###

			// name different
			{
				final NameValue<Integer, String> input = NameValue.of(2, "one");
				final NameValue<Integer, String> equals_check = new NameValue<>(1, "one");
				params.add(Arguments.of(false, input, equals_check));
			}

			// value different
			{
				final NameValue<Integer, String> input = NameValue.of(1, "one");
				final NameValue<Integer, String> equals_check = new NameValue<>(1, "onee");
				params.add(Arguments.of(false, input, equals_check));
			}

			// different class
			{
				final NameValue<Integer, String> input = NameValue.of(1, "one");
				final String equals_check = "jodd";
				params.add(Arguments.of(false, input, equals_check));
			}

			// null
			{
				final NameValue<Integer, String> input = NameValue.of(1, "one");
				final String equals_check = null;
				params.add(Arguments.of(false, input, equals_check));
			}

			return params;
		}
	}


	@DisplayName("tests for NameValue#hashcode")
	@Nested
	class Hashcode {

		@Test
		void hashcode_with_null_pair() {

			final NameValue<Integer, String> input = new NameValue<>(null, null);

			final int actual = input.hashCode();

			assertEquals(0, actual);
		}

		@Test
		void hashcode_with_non_null_pair_both_integer() {

			final NameValue<Integer, Integer> input = new NameValue<>(1, 1);

			final int actual = input.hashCode();

			assertEquals(0, actual);
		}

		@Test
		void hashcode_with_non_null_pair_both_different() {

			final NameValue<String, Integer> input = new NameValue<>("jodd", 1337);

			final int actual = input.hashCode();

			assertEquals(3267004, actual);
		}

	}

}