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

import org.apache.commons.lang3.RandomUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;

import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class HashCodeTest {

	@Test
	void testhashCode() {
		int hash = HashCode.hash(0, "Hey");
		hash = HashCode.hash(hash, 1);
		hash = HashCode.hash(hash, 1.4);
		hash = HashCode.hash(hash, 9f);
		hash = HashCode.hash(hash, true);
		hash = HashCode.hash(hash, ArraysUtil.ints(1,2,3,4));
		hash = HashCode.hash(hash, new NameValue<>("A", "B"));

		int hash2 = hash;

		hash = HashCode.hash(0, "Hey");
		hash = HashCode.hash(hash, 1);
		hash = HashCode.hash(hash, 1.4);
		hash = HashCode.hash(hash, 9f);
		hash = HashCode.hash(hash, true);
		hash = HashCode.hash(hash, ArraysUtil.ints(1,2,3,4));
		hash = HashCode.hash(hash, new NameValue<>("A", "B"));

		assertEquals(hash, hash2);
	}

	// ---------------------------------------------------------------- boolean
	@Nested
	@DisplayName(value = "Tests with boolean")
	class Tests_with_Boolean {

		@Test
		void testHash_with_true() {
			final int actual_1 = HashCode.hash(true);
			final int actual_2 = HashCode.hash(true);

			// asserts
			assertTrue(actual_1 == actual_2);
		}

		@Test
		void testHash_with_false() {
			final int actual_1 = HashCode.hash(false);
			final int actual_2 = HashCode.hash(false);

			// asserts
			assertTrue(actual_1 == actual_2);
		}

		@Test
		void testHash_with_variant() {

			final int actual_1 = HashCode.hashBooleanArray(false,true,true,false,false,true,true);
			final int actual_2 = HashCode.hashBooleanArray(false,true,true,false,false,true,true);

			// asserts
			assertTrue(actual_1 == actual_2);
		}

		@RepeatedTest(10)
		void testHash_with_array() {

			final boolean[] input = getBooleanArrayWithRandomValues(MathUtil.randomInt(5, 25));

			final int actual_1 = HashCode.hashBooleanArray(input);
			final int actual_2 = HashCode.hashBooleanArray(input);

			// asserts
			assertTrue(actual_1 == actual_2);
		}

	}

	// ---------------------------------------------------------------- char
	@Nested
	@DisplayName(value = "Tests with char")
	class Tests_with_Char {

		@RepeatedTest(10)
		void testHash_with_random() {
			final char input = getCharArrayWithRandomValues(1)[0];

			final int actual_1 = HashCode.hash(input);
			final int actual_2 = HashCode.hash(input);

			// asserts
			assertTrue(actual_1 == actual_2);
		}

		@Test
		void testHash_with_variant() {
			final int actual_1 = HashCode.hashCharArray('j','o','o','d', '!', 'o', 'r', 'g');
			final int actual_2 = HashCode.hashCharArray('j','o','o','d', '!', 'o', 'r', 'g');

			// asserts
			assertTrue(actual_1 == actual_2);
		}

		@RepeatedTest(10)
		void testHash_with_array() {

			final char[] input = getCharArrayWithRandomValues(MathUtil.randomInt(5, 20));

			final int actual_1 = HashCode.hash(input);
			final int actual_2 = HashCode.hash(input);

			// asserts
			assertTrue(actual_1 == actual_2);
		}

	}

	// ---------------------------------------------------------------- int
	@Nested
	@DisplayName(value = "Tests with int")
	class Tests_with_Int {

		@RepeatedTest(10)
		void testHash_with_random() {
			final int input = getIntArrayWithRandomValues(1)[0];

			final int actual_1 = HashCode.hash(input);
			final int actual_2 = HashCode.hash(input);

			// asserts
			assertTrue(actual_1 == actual_2);
		}

		@Test
		void testHash_with_variant() {
			final int actual_1 = HashCode.hashIntArray(4567,343,8985656,12334,67686,12,5656,0,89654,1212);
			final int actual_2 = HashCode.hashIntArray(4567,343,8985656,12334,67686,12,5656,0,89654,1212);

			// asserts
			assertTrue(actual_1 == actual_2);
		}

		@RepeatedTest(10)
		void testHash_with_array() {

			final int[] input = getIntArrayWithRandomValues(MathUtil.randomInt(5, 20));

			final int actual_1 = HashCode.hash(input);
			final int actual_2 = HashCode.hash(input);

			// asserts
			assertTrue(actual_1 == actual_2);
		}

	}


	// ---------------------------------------------------------------- short
	@Nested
	@DisplayName(value = "Tests with short")
	class Tests_with_Short {

		@RepeatedTest(10)
		void testHash_with_random() {
			final short input = getShortArrayWithRandomValues(1)[0];

			final int actual_1 = HashCode.hash(input);
			final int actual_2 = HashCode.hash(input);

			// asserts
			assertTrue(actual_1 == actual_2);
		}

		@Test
		void testHash_with_variant() {
			final int actual_1 = HashCode.hashShortArray((short)344,(short)4565, (short)5655, (short)32456, (short)-29431, (short)-1);
			final int actual_2 = HashCode.hashShortArray((short)344,(short)4565, (short)5655, (short)32456, (short)-29431, (short)-1);

			// asserts
			assertTrue(actual_1 == actual_2);
		}

		@RepeatedTest(10)
		void testHash_with_array() {
			final short[] input = getShortArrayWithRandomValues(MathUtil.randomInt(5, 20));

			final int actual_1 = HashCode.hash(input);
			final int actual_2 = HashCode.hash(input);

			// asserts
			assertTrue(actual_1 == actual_2);
		}

	}


	// ---------------------------------------------------------------- byte
	@Nested
	@DisplayName(value = "Tests with byte")
	class Tests_with_Byte {

		@RepeatedTest(10)
		void testHash_with_random() {
			final byte input = getByteArrayWithRandomValues(1)[0];

			final int actual_1 = HashCode.hash(input);
			final int actual_2 = HashCode.hash(input);

			// asserts
			assertTrue(actual_1 == actual_2);
		}

		@Test
		void testHash_with_variant() {
			final int actual_1 = HashCode.hashShortArray((byte)-122,(byte)1, (byte)23, (byte)127, (byte)-0, (byte)-1);
			final int actual_2 = HashCode.hashShortArray((byte)-122,(byte)1, (byte)23, (byte)127, (byte)-0, (byte)-1);

			// asserts
			assertTrue(actual_1 == actual_2);
		}

		@RepeatedTest(10)
		void testHash_with_array() {
			final byte[] input = getByteArrayWithRandomValues(MathUtil.randomInt(5, 20));

			final int actual_1 = HashCode.hash(input);
			final int actual_2 = HashCode.hash(input);

			// asserts
			assertTrue(actual_1 == actual_2);
		}

	}

	// ---------------------------------------------------------------- long
	@Nested
	@DisplayName(value = "Tests with long")
	class Tests_with_Long {

		@RepeatedTest(10)
		void testHash_with_random() {
			final long input = getLongArrayWithRandomValues(1)[0];

			final int actual_1 = HashCode.hash(input);
			final int actual_2 = HashCode.hash(input);

			// asserts
			assertTrue(actual_1 == actual_2);
		}

		@Test
		void testHash_with_variant() {
			final int actual_1 = HashCode.hashLongArray(-345342L,123232L, 5465464L, 3678545L, -423430L, -2342341L);
			final int actual_2 = HashCode.hashLongArray(-345342L,123232L, 5465464L, 3678545L, -423430L, -2342341L);

			// asserts
			assertTrue(actual_1 == actual_2);
		}

		@RepeatedTest(10)
		void testHash_with_array() {
			final long[] input = getLongArrayWithRandomValues(MathUtil.randomInt(5, 20));

			final int actual_1 = HashCode.hash(input);
			final int actual_2 = HashCode.hash(input);

			// asserts
			assertTrue(actual_1 == actual_2);
		}

	}


	// ---------------------------------------------------------------- float
	@Nested
	@DisplayName(value = "Tests with float")
	class Tests_with_Float {

		@RepeatedTest(10)
		void testHash_with_random() {
			final float input = getFloatArrayWithRandomValues(1)[0];

			final int actual_1 = HashCode.hash(input);
			final int actual_2 = HashCode.hash(input);

			// asserts
			assertTrue(actual_1 == actual_2);
		}

		@Test
		void testHash_with_variant() {
			final int actual_1 = HashCode.hashFloatArray(-345342.42F,123232.22F, 5465464.8743F, 3678545.123424F, -423430.1235F, -2342341.0975F, 0f);
			final int actual_2 = HashCode.hashFloatArray(-345342.42F,123232.22F, 5465464.8743F, 3678545.123424F, -423430.1235F, -2342341.0975F, 0f);

			// asserts
			assertTrue(actual_1 == actual_2);
		}

		@RepeatedTest(10)
		void testHash_with_array() {
			final float[] input = getFloatArrayWithRandomValues(MathUtil.randomInt(5, 20));

			final int actual_1 = HashCode.hash(input);
			final int actual_2 = HashCode.hash(input);

			// asserts
			assertTrue(actual_1 == actual_2);
		}

	}

	// ---------------------------------------------------------------- double
	@Nested
	@DisplayName(value = "Tests with double")
	class Tests_with_Double {

		@RepeatedTest(10)
		void testHash_with_random() {
			final double input = getDoubleArrayWithRandomValues(1)[0];

			final int actual_1 = HashCode.hash(input);
			final int actual_2 = HashCode.hash(input);

			// asserts
			assertTrue(actual_1 == actual_2);
		}

		@Test
		void testHash_with_variant() {
			final int actual_1 = HashCode.hashDoubleArray(-345342.42D,123232.22D, 5465464.8743D, 3678545.123424D, -423430.1235D, -2342341.0975D, 0D);
			final int actual_2 = HashCode.hashDoubleArray(-345342.42D,123232.22D, 5465464.8743D, 3678545.123424D, -423430.1235D, -2342341.0975D, 0D);

			// asserts
			assertTrue(actual_1 == actual_2);
		}

		@RepeatedTest(10)
		void testHash_with_array() {
			final double[] input = getDoubleArrayWithRandomValues(MathUtil.randomInt(5, 20));

			final int actual_1 = HashCode.hash(input);
			final int actual_2 = HashCode.hash(input);

			// asserts
			assertTrue(actual_1 == actual_2);
		}

	}

	// ---------------------------------------------------------------- Object
	@Nested
	@DisplayName(value = "Tests with Object")
	class Tests_with_Object {

		@Test
		void testHash_with_null() {
			final Object input = null;

			final int actual_1 = HashCode.hash(input);
			final int actual_2 = HashCode.hash(input);

			// asserts
			assertTrue(actual_1 == actual_2);
		}

		@RepeatedTest(10)
		void testHash_with_array() {
			final Object input = getObjectArrayWithRandomValues(MathUtil.randomInt(5, 20));

			final int actual_1 = HashCode.hash(input);
			final int actual_2 = HashCode.hash(input);

			// asserts
			assertTrue(actual_1 == actual_2);
		}

		@RepeatedTest(10)
		void testHash_with_instance() {
			final NameValue<String, String> input = new NameValue<>(RandomString.getInstance().randomBase64(MathUtil.randomInt(5,55)), RandomString.getInstance().randomBase64(MathUtil.randomInt(5,55)));

			final int actual_1 = HashCode.hash(input);
			final int actual_2 = HashCode.hash(input);

			// asserts
			assertTrue(actual_1 == actual_2);
		}

	}


	// ---------------------------------------------------------------- helper methods

	private static boolean[] getBooleanArrayWithRandomValues(final int length) {
		final Random random = new Random(HashCode.SEED);
		boolean[] arr = new boolean[length];
		for(int i = 0; i < length; i++) {
			arr[i] = random.nextBoolean();
		}
		return arr;
	}

	private static char[] getCharArrayWithRandomValues(final int length) {
		char[] arr = new char[length];
		for(int i = 0; i < length; i++) {
			arr[i] = (char) MathUtil.randomInt(0,256);
		}
		return arr;
	}

	private static int[] getIntArrayWithRandomValues(final int length) {
		int[] arr = new int[length];
		for(int i = 0; i < length; i++) {
			arr[i] = MathUtil.randomInt(-54321,54321);
		}
		return arr;
	}

	private static short[] getShortArrayWithRandomValues(final int length) {
		short[] arr = new short[length];
		for(int i = 0; i < length; i++) {
			arr[i] = (short) MathUtil.randomInt(-32768, 32767);
		}
		return arr;
	}

	private static byte[] getByteArrayWithRandomValues(final int length) {
		byte[] arr = new byte[length];
		for(int i = 0; i < length; i++) {
			arr[i] = (byte) MathUtil.randomInt(-128, 127);
		}
		return arr;
	}

	private static long[] getLongArrayWithRandomValues(final int length) {
		long[] arr = new long[length];
		for(int i = 0; i < length; i++) {
			arr[i] = MathUtil.randomLong(-124565L, 125643L);
		}
		return arr;
	}

	private static float[] getFloatArrayWithRandomValues(final int length) {
		float[] arr = new float[length];
		for(int i = 0; i < length; i++) {
			arr[i] = RandomUtils.nextFloat(0F, 125643F);
		}
		return arr;
	}

	private static double[] getDoubleArrayWithRandomValues(final int length) {
		double[] arr = new double[length];
		for(int i = 0; i < length; i++) {
			arr[i] = RandomUtils.nextDouble(0F, 125643F);
		}
		return arr;
	}

	private static Object[] getObjectArrayWithRandomValues(final int length) {
		Object[] arr = new Object[length];
		for(int i = 0; i < length; i++) {
			arr[i] = new Object();
		}
		return arr;
	}

}
