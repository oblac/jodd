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

package jodd.typeconverter;

import jodd.mutable.MutableInteger;
import org.junit.jupiter.api.Test;

import java.math.BigInteger;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class ArraysTest {

	public static final Integer[] INTEGERS = new Integer[] {1, 2, 3};
	public static final int[] INTS = new int[] {1, 2, 3};
	public static final Long[] LONGS = new Long[] {1L, 2L, 3L};
	public static final long[] LONGS1 = new long[] {1L, 2L, 3L};
	public static final Float[] FLOATS = new Float[] {1.1f, 2.2f, 3.3f};
	public static final float[] FLOATS1 = new float[] {1.1f, 2.2f, 3.3f};
	public static final Double[] DOUBLES = new Double[] {1.1, 2.2, 3.3};
	public static final double[] DOUBLES1 = new double[] {1.1, 2.2, 3.3};
	public static final Short[] SHORTS = new Short[] {1,2,3};
	public static final short[] SHORTS1 = new short[] {1,2,3};
	public static final Byte[] BYTES = new Byte[] {1,2,3};
	public static final byte[] BYTES1 = new byte[] {1,2,3};
	public static final Character[] CHARACTERS = new Character[] {'a', 'b', 'c'};
	public static final char[] CHARS = new char[] {'a', 'b', 'c'};
	public static final Boolean[] BOOLEANS = new Boolean[] {true, false, true};
	public static final boolean[] BOOLEANS1 = new boolean[] {true, false, true};

	@Test
	public void testArrayToIntConversion() {
		Integer[] objects = INTEGERS;
		Object result = TypeConverterManager.convertType(objects, int[].class);

		assertNotNull(result);
		assertArrayEquals(INTS, (int[]) result);
	}

	@Test
	public void testIntToArrayConversion() {
		int[] primitives = INTS;
		Object result = TypeConverterManager.convertType(primitives, Integer[].class);

		assertNotNull(result);
		assertArrayEquals(INTEGERS, (Integer[]) result);
	}

	@Test
	public void testArrayToLongConversion() {
		Long[] objects = LONGS;
		Object result = TypeConverterManager.convertType(objects, long[].class);

		assertNotNull(result);
		assertArrayEquals(LONGS1, (long[]) result);
	}

	@Test
	public void testLongToArrayConversion() {
		long[] primitives = LONGS1;
		Object result = TypeConverterManager.convertType(primitives, Long[].class);

		assertNotNull(result);
		assertArrayEquals(LONGS, (Long[]) result);
	}

	@Test
	public void testArrayToFloatConversion() {
		Float[] objects = FLOATS;
		Object result = TypeConverterManager.convertType(objects, float[].class);

		assertNotNull(result);
		assertArrayEquals(FLOATS1, (float[]) result, 0.005f);
	}

	@Test
	public void testFloatToArrayConversion() {
		float[] primitives = FLOATS1;
		Object result = TypeConverterManager.convertType(primitives, Float[].class);

		assertNotNull(result);
		assertArrayEquals(FLOATS, (Float[]) result);
	}

	@Test
	public void testArrayToDoubleConversion() {
		Double[] objects = DOUBLES;
		Object result = TypeConverterManager.convertType(objects, double[].class);

		assertNotNull(result);
		assertArrayEquals(DOUBLES1, (double[]) result, 0.005f);
	}

	@Test
	public void testDoubleToArrayConversion() {
		double[] primitives = DOUBLES1;
		Object result = TypeConverterManager.convertType(primitives, Double[].class);

		assertNotNull(result);
		assertArrayEquals(DOUBLES, (Double[]) result);
	}

	@Test
	public void testArrayToShortConversion() {
		Short[] objects = SHORTS;
		Object result = TypeConverterManager.convertType(objects, short[].class);

		assertNotNull(result);
		assertArrayEquals(SHORTS1, (short[]) result);
	}

	@Test
	public void testShortToArrayConversion() {
		short[] primitives = SHORTS1;
		Object result = TypeConverterManager.convertType(primitives, Short[].class);

		assertNotNull(result);
		assertArrayEquals(SHORTS, (Short[]) result);
	}

	@Test
	public void testArrayToByteConversion() {
		Byte[] objects = BYTES;
		Object result = TypeConverterManager.convertType(objects, byte[].class);

		assertNotNull(result);
		assertArrayEquals(BYTES1, (byte[]) result);
	}

	@Test
	public void testByteToArrayConversion() {
		byte[] primitives = BYTES1;
		Object result = TypeConverterManager.convertType(primitives, Byte[].class);

		assertNotNull(result);
		assertArrayEquals(BYTES, (Byte[]) result);
	}

	@Test
	public void testArrayToCharConversion() {
		Character[] objects = CHARACTERS;
		Object result = TypeConverterManager.convertType(objects, char[].class);

		assertNotNull(result);
		assertArrayEquals(CHARS, (char[]) result);
	}

	@Test
	public void testCharToArrayConversion() {
		char[] primitives = CHARS;
		Object result = TypeConverterManager.convertType(primitives, Character[].class);

		assertNotNull(result);
		assertArrayEquals(CHARACTERS, (Character[]) result);
	}

	@Test
	public void testArrayToBooleanConversion() {
		Boolean[] objects = BOOLEANS;
		Object result = TypeConverterManager.convertType(objects, boolean[].class);

		assertNotNull(result);
		assertArrayEquals(BOOLEANS1, (boolean[]) result);
	}

	@Test
	public void testBooleanToArrayConversion() {
		boolean [] primitives = BOOLEANS1;
		Object result = TypeConverterManager.convertType(primitives, Boolean[].class);

		assertNotNull(result);
		assertArrayEquals(BOOLEANS, (Boolean[]) result);
	}

	// ---------------------------------------------------------------- single value

	@Test
	public void testArrayToIntConversionSingleValue() {
		Object result = TypeConverterManager.convertType(Integer.valueOf(173), int[].class);

		assertNotNull(result);
		assertArrayEquals(new int[] {173}, (int[]) result);
	}

	@Test
	public void testIntToArrayConversionSingleValue() {
		Object result = TypeConverterManager.convertType(173, Integer[].class);

		assertNotNull(result);
		assertArrayEquals(new Integer[] {173}, (Integer[]) result);
	}

	// ---------------------------------------------------------------- string csv

	@Test
	public void testArrayToIntConversionCommaSeparated() {
		Object result = TypeConverterManager.convertType("1,2,3", int[].class);

		assertNotNull(result);
		assertArrayEquals(new int[] {1,2,3}, (int[]) result);
	}

	@Test
	public void testIntToArrayConversionCommaSeparated() {
		Object result = TypeConverterManager.convertType("1,2,3", Integer[].class);

		assertNotNull(result);
		assertArrayEquals(new Integer[] {1,2,3}, (Integer[]) result);
	}

	@Test
	public void testMutableIntToArrayConversionCommaSeparated() {
		Object result = TypeConverterManager.convertType("1,2,3", MutableInteger[].class);

		assertNotNull(result);
		MutableInteger[] mutables = (MutableInteger[]) result;
		assertEquals(1, mutables[0].value);
		assertEquals(2, mutables[1].value);
		assertEquals(3, mutables[2].value);

	}


	// ---------------------------------------------------------------- prim 2 prim

	@Test
	public void testIntToLongArray() {
		Object result = TypeConverterManager.convertType(new int[] {1,2,3}, long[].class);

		assertNotNull(result);
		assertArrayEquals(new long[] {1,2,3}, (long[]) result);
	}

	@Test
	public void testIntToLongArray2() {
		Object result = TypeConverterManager.convertType(new int[][] {{1,2,3},{4,5}}, long[][].class);

		assertNotNull(result);
		assertArrayEquals(new long[][] {{1, 2, 3}, {4, 5}}, (long[][]) result);
	}

	@Test
	public void testIntToLongArray3() {
		Object result = TypeConverterManager.convertType(new int[][] {{1,2,3},{4,5}}, Long[][].class);

		assertNotNull(result);
		assertArrayEquals(new Long[][] {{1l, 2l, 3l}, {4l, 5l}}, (Long[][]) result);
	}

	// ---------------------------------------------------------------- mutables

	@Test
	public void testMutableToInteger() {
		Object mutables = new MutableInteger[] {
				new MutableInteger(7),
				new MutableInteger(3),
				new MutableInteger(1)
		};

		Object result = TypeConverterManager.convertType(mutables, Integer[].class);
		assertNotNull(result);

		assertArrayEquals(new Integer[] {7,3,1}, (Integer[]) result);
	}

	@Test
	public void testIntegerToMutable() {
		Object integers = new Integer[] {
				new Integer(7),
				new Integer(3),
				new Integer(1)
		};

		Object result = TypeConverterManager.convertType(integers, MutableInteger[].class);
		assertNotNull(result);

		MutableInteger[] mutables = (MutableInteger[]) result;
		assertEquals(7, mutables[0].value);
		assertEquals(3, mutables[1].value);
		assertEquals(1, mutables[2].value);
	}

	@Test
	public void testMutableToInt() {
		Object mutables = new MutableInteger[] {
				new MutableInteger(7),
				new MutableInteger(3),
				new MutableInteger(1)
		};

		Object result = TypeConverterManager.convertType(mutables, int[].class);
		assertNotNull(result);

		assertArrayEquals(new int[] {7,3,1}, (int[]) result);
	}

	@Test
	public void testIntToMutable() {
		int[] array = new int[] {7,3,1};

		Object result = TypeConverterManager.convertType(array, MutableInteger[].class);
		assertNotNull(result);

		MutableInteger[] mutables = (MutableInteger[]) result;
		assertEquals(7, mutables[0].value);
		assertEquals(3, mutables[1].value);
		assertEquals(1, mutables[2].value);
	}


	// ---------------------------------------------------------------- bigint

	@Test
	public void testBigIntegerToInteger() {
		Object bigIntegers = new BigInteger[] {
				new BigInteger("7"),
				new BigInteger("3"),
				new BigInteger("1")
		};

		Object result = TypeConverterManager.convertType(bigIntegers, Integer[].class);
		assertNotNull(result);

		assertArrayEquals(new Integer[] {7,3,1}, (Integer[]) result);
	}

	@Test
	public void testIntegerToBigInteger() {
		Object integers = new Integer[] {
				new Integer(7),
				new Integer(3),
				new Integer(1)
		};

		Object result = TypeConverterManager.convertType(integers, BigInteger[].class);
		assertNotNull(result);

		BigInteger[] bigIntegers = (BigInteger[]) result;
		assertEquals(7, bigIntegers[0].intValue());
		assertEquals(3, bigIntegers[1].intValue());
		assertEquals(1, bigIntegers[2].intValue());
	}

	@Test
	public void testBigIntegerToInt() {
		Object bigIntegers = new BigInteger[] {
				new BigInteger("7"),
				new BigInteger("3"),
				new BigInteger("1")
		};

		Object result = TypeConverterManager.convertType(bigIntegers, int[].class);
		assertNotNull(result);

		assertArrayEquals(new int[] {7, 3, 1}, (int[]) result);
	}

	@Test
	public void testIntToBigInteger() {
		int[] array = new int[] {7,3,1};

		Object result = TypeConverterManager.convertType(array, BigInteger[].class);
		assertNotNull(result);

		BigInteger[] bigIntegers = (BigInteger[]) result;
		assertEquals(7, bigIntegers[0].byteValue());
		assertEquals(3, bigIntegers[1].byteValue());
		assertEquals(1, bigIntegers[2].byteValue());
	}


}
