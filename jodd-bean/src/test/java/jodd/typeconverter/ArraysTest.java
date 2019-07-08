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

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class ArraysTest {

	private TypeConverterManager typeConverterManager = TypeConverterManager.get();

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
	void testArrayToIntConversion() {
		Integer[] objects = INTEGERS;
		Object result = typeConverterManager.convertType(objects, int[].class);

		assertNotNull(result);
		assertArrayEquals(INTS, (int[]) result);
	}

	@Test
	void testIntToArrayConversion() {
		int[] primitives = INTS;
		Object result = typeConverterManager.convertType(primitives, Integer[].class);

		assertNotNull(result);
		assertArrayEquals(INTEGERS, (Integer[]) result);
	}

	@Test
	void testArrayToLongConversion() {
		Long[] objects = LONGS;
		Object result = typeConverterManager.convertType(objects, long[].class);

		assertNotNull(result);
		assertArrayEquals(LONGS1, (long[]) result);
	}

	@Test
	void testLongToArrayConversion() {
		long[] primitives = LONGS1;
		Object result = typeConverterManager.convertType(primitives, Long[].class);

		assertNotNull(result);
		assertArrayEquals(LONGS, (Long[]) result);
	}

	@Test
	void testArrayToFloatConversion() {
		Float[] objects = FLOATS;
		Object result = typeConverterManager.convertType(objects, float[].class);

		assertNotNull(result);
		assertArrayEquals(FLOATS1, (float[]) result, 0.005f);
	}

	@Test
	void testFloatToArrayConversion() {
		float[] primitives = FLOATS1;
		Object result = typeConverterManager.convertType(primitives, Float[].class);

		assertNotNull(result);
		assertArrayEquals(FLOATS, (Float[]) result);
	}

	@Test
	void testArrayToDoubleConversion() {
		Double[] objects = DOUBLES;
		Object result = typeConverterManager.convertType(objects, double[].class);

		assertNotNull(result);
		assertArrayEquals(DOUBLES1, (double[]) result, 0.005f);
	}

	@Test
	void testDoubleToArrayConversion() {
		double[] primitives = DOUBLES1;
		Object result = typeConverterManager.convertType(primitives, Double[].class);

		assertNotNull(result);
		assertArrayEquals(DOUBLES, (Double[]) result);
	}

	@Test
	void testArrayToShortConversion() {
		Short[] objects = SHORTS;
		Object result = typeConverterManager.convertType(objects, short[].class);

		assertNotNull(result);
		assertArrayEquals(SHORTS1, (short[]) result);
	}

	@Test
	void testShortToArrayConversion() {
		short[] primitives = SHORTS1;
		Object result = typeConverterManager.convertType(primitives, Short[].class);

		assertNotNull(result);
		assertArrayEquals(SHORTS, (Short[]) result);
	}

	@Test
	void testArrayToByteConversion() {
		Byte[] objects = BYTES;
		Object result = typeConverterManager.convertType(objects, byte[].class);

		assertNotNull(result);
		assertArrayEquals(BYTES1, (byte[]) result);
	}

	@Test
	void testByteToArrayConversion() {
		byte[] primitives = BYTES1;
		Object result = typeConverterManager.convertType(primitives, Byte[].class);

		assertNotNull(result);
		assertArrayEquals(BYTES, (Byte[]) result);
	}

	@Test
	void testArrayToCharConversion() {
		Character[] objects = CHARACTERS;
		Object result = typeConverterManager.convertType(objects, char[].class);

		assertNotNull(result);
		assertArrayEquals(CHARS, (char[]) result);
	}

	@Test
	void testCharToArrayConversion() {
		char[] primitives = CHARS;
		Object result = typeConverterManager.convertType(primitives, Character[].class);

		assertNotNull(result);
		assertArrayEquals(CHARACTERS, (Character[]) result);
	}

	@Test
	void testArrayToBooleanConversion() {
		Boolean[] objects = BOOLEANS;
		Object result = typeConverterManager.convertType(objects, boolean[].class);

		assertNotNull(result);
		assertArrayEquals(BOOLEANS1, (boolean[]) result);
	}

	@Test
	void testBooleanToArrayConversion() {
		boolean [] primitives = BOOLEANS1;
		Object result = typeConverterManager.convertType(primitives, Boolean[].class);

		assertNotNull(result);
		assertArrayEquals(BOOLEANS, (Boolean[]) result);
	}

	// ---------------------------------------------------------------- single value

	@Test
	void testArrayToIntConversionSingleValue() {
		Object result = typeConverterManager.convertType(Integer.valueOf(173), int[].class);

		assertNotNull(result);
		assertArrayEquals(new int[] {173}, (int[]) result);
	}

	@Test
	void testIntToArrayConversionSingleValue() {
		Object result = typeConverterManager.convertType(173, Integer[].class);

		assertNotNull(result);
		assertArrayEquals(new Integer[] {173}, (Integer[]) result);
	}

	// ---------------------------------------------------------------- string csv

	@Test
	void testArrayToIntConversionCommaSeparated() {
		Object result = typeConverterManager.convertType("1,2,3", int[].class);

		assertNotNull(result);
		assertArrayEquals(new int[] {1,2,3}, (int[]) result);
	}

	@Test
	void testIntToArrayConversionCommaSeparated() {
		Object result = typeConverterManager.convertType("1,2,3", Integer[].class);

		assertNotNull(result);
		assertArrayEquals(new Integer[] {1,2,3}, (Integer[]) result);
	}

	@Test
	void testMutableIntToArrayConversionCommaSeparated() {
		Object result = typeConverterManager.convertType("1,2,3", MutableInteger[].class);

		assertNotNull(result);
		MutableInteger[] mutables = (MutableInteger[]) result;
		assertEquals(1, mutables[0].value);
		assertEquals(2, mutables[1].value);
		assertEquals(3, mutables[2].value);

	}


	// ---------------------------------------------------------------- prim 2 prim

	@Test
	void testIntToLongArray() {
		Object result = typeConverterManager.convertType(new int[] {1,2,3}, long[].class);

		assertNotNull(result);
		assertArrayEquals(new long[] {1,2,3}, (long[]) result);
	}

	@Test
	void testIntToLongArray2() {
		Object result = typeConverterManager.convertType(new int[][] {{1,2,3},{4,5}}, long[][].class);

		assertNotNull(result);
		assertArrayEquals(new long[][] {{1, 2, 3}, {4, 5}}, (long[][]) result);
	}

	@Test
	void testIntToLongArray3() {
		Object result = typeConverterManager.convertType(new int[][] {{1,2,3},{4,5}}, Long[][].class);

		assertNotNull(result);
		assertArrayEquals(new Long[][] {{1l, 2l, 3l}, {4l, 5l}}, (Long[][]) result);
	}

	// ---------------------------------------------------------------- mutables

	@Test
	void testMutableToInteger() {
		Object mutables = new MutableInteger[] {
				new MutableInteger(7),
				new MutableInteger(3),
				new MutableInteger(1)
		};

		Object result = typeConverterManager.convertType(mutables, Integer[].class);
		assertNotNull(result);

		assertArrayEquals(new Integer[] {7,3,1}, (Integer[]) result);
	}

	@Test
	void testIntegerToMutable() {
		Object integers = new Integer[] {
				new Integer(7),
				new Integer(3),
				new Integer(1)
		};

		Object result = typeConverterManager.convertType(integers, MutableInteger[].class);
		assertNotNull(result);

		MutableInteger[] mutables = (MutableInteger[]) result;
		assertEquals(7, mutables[0].value);
		assertEquals(3, mutables[1].value);
		assertEquals(1, mutables[2].value);
	}

	@Test
	void testMutableToInt() {
		Object mutables = new MutableInteger[] {
				new MutableInteger(7),
				new MutableInteger(3),
				new MutableInteger(1)
		};

		Object result = typeConverterManager.convertType(mutables, int[].class);
		assertNotNull(result);

		assertArrayEquals(new int[] {7,3,1}, (int[]) result);
	}

	@Test
	void testIntToMutable() {
		int[] array = new int[] {7,3,1};

		Object result = typeConverterManager.convertType(array, MutableInteger[].class);
		assertNotNull(result);

		MutableInteger[] mutables = (MutableInteger[]) result;
		assertEquals(7, mutables[0].value);
		assertEquals(3, mutables[1].value);
		assertEquals(1, mutables[2].value);
	}


	// ---------------------------------------------------------------- bigint

	@Test
	void testBigIntegerToInteger() {
		Object bigIntegers = new BigInteger[] {
				new BigInteger("7"),
				new BigInteger("3"),
				new BigInteger("1")
		};

		Object result = typeConverterManager.convertType(bigIntegers, Integer[].class);
		assertNotNull(result);

		assertArrayEquals(new Integer[] {7,3,1}, (Integer[]) result);
	}

	@Test
	void testIntegerToBigInteger() {
		Object integers = new Integer[] {
				new Integer(7),
				new Integer(3),
				new Integer(1)
		};

		Object result = typeConverterManager.convertType(integers, BigInteger[].class);
		assertNotNull(result);

		BigInteger[] bigIntegers = (BigInteger[]) result;
		assertEquals(7, bigIntegers[0].intValue());
		assertEquals(3, bigIntegers[1].intValue());
		assertEquals(1, bigIntegers[2].intValue());
	}

	@Test
	void testBigIntegerToInt() {
		Object bigIntegers = new BigInteger[] {
				new BigInteger("7"),
				new BigInteger("3"),
				new BigInteger("1")
		};

		Object result = typeConverterManager.convertType(bigIntegers, int[].class);
		assertNotNull(result);

		assertArrayEquals(new int[] {7, 3, 1}, (int[]) result);
	}

	@Test
	void testIntToBigInteger() {
		int[] array = new int[] {7,3,1};

		Object result = typeConverterManager.convertType(array, BigInteger[].class);
		assertNotNull(result);

		BigInteger[] bigIntegers = (BigInteger[]) result;
		assertEquals(7, bigIntegers[0].byteValue());
		assertEquals(3, bigIntegers[1].byteValue());
		assertEquals(1, bigIntegers[2].byteValue());
	}


}
