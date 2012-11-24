package jodd.typeconverter;

import org.junit.Test;

import static jodd.AssertPrimitiveArraysTestHelper.*;
import static org.junit.Assert.assertNotNull;

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
		assertEquals(INTS, (int[]) result);
	}

	@Test
	public void testIntToArrayConversion() {
		int[] primitives = INTS;
		Object result = TypeConverterManager.convertType(primitives, Integer[].class);

		assertNotNull(result);
		assertEquals(INTEGERS, (Integer[]) result);
	}

	@Test
	public void testArrayToLongConversion() {
		Long[] objects = LONGS;
		Object result = TypeConverterManager.convertType(objects, long[].class);

		assertNotNull(result);
		assertEquals(LONGS1, (long[]) result);
	}

	@Test
	public void testLongToArrayConversion() {
		long[] primitives = LONGS1;
		Object result = TypeConverterManager.convertType(primitives, Long[].class);

		assertNotNull(result);
		assertEquals(LONGS, (Long[]) result);
	}

	@Test
	public void testArrayToFloatConversion() {
		Float[] objects = FLOATS;
		Object result = TypeConverterManager.convertType(objects, float[].class);

		assertNotNull(result);
		assertEquals(FLOATS1, (float[]) result, 0.005f);
	}

	@Test
	public void testFloatToArrayConversion() {
		float[] primitives = FLOATS1;
		Object result = TypeConverterManager.convertType(primitives, Float[].class);

		assertNotNull(result);
		assertEquals(FLOATS, (Float[]) result);
	}

	@Test
	public void testArrayToDoubleConversion() {
		Double[] objects = DOUBLES;
		Object result = TypeConverterManager.convertType(objects, double[].class);

		assertNotNull(result);
		assertEquals(DOUBLES1, (double[]) result, 0.005f);
	}

	@Test
	public void testDoubleToArrayConversion() {
		double[] primitives = DOUBLES1;
		Object result = TypeConverterManager.convertType(primitives, Double[].class);

		assertNotNull(result);
		assertEquals(DOUBLES, (Double[]) result);
	}

	@Test
	public void testArrayToShortConversion() {
		Short[] objects = SHORTS;
		Object result = TypeConverterManager.convertType(objects, short[].class);

		assertNotNull(result);
		assertEquals(SHORTS1, (short[]) result);
	}

	@Test
	public void testShortToArrayConversion() {
		short[] primitives = SHORTS1;
		Object result = TypeConverterManager.convertType(primitives, Short[].class);

		assertNotNull(result);
		assertEquals(SHORTS, (Short[]) result);
	}

	@Test
	public void testArrayToByteConversion() {
		Byte[] objects = BYTES;
		Object result = TypeConverterManager.convertType(objects, byte[].class);

		assertNotNull(result);
		assertEquals(BYTES1, (byte[]) result);
	}

	@Test
	public void testByteToArrayConversion() {
		byte[] primitives = BYTES1;
		Object result = TypeConverterManager.convertType(primitives, Byte[].class);

		assertNotNull(result);
		assertEquals(BYTES, (Byte[]) result);
	}

	@Test
	public void testArrayToCharConversion() {
		Character[] objects = CHARACTERS;
		Object result = TypeConverterManager.convertType(objects, char[].class);

		assertNotNull(result);
		assertEquals(CHARS, (char[]) result);
	}

	@Test
	public void testCharToArrayConversion() {
		char[] primitives = CHARS;
		Object result = TypeConverterManager.convertType(primitives, Character[].class);

		assertNotNull(result);
		assertEquals(CHARACTERS, (Character[]) result);
	}

	@Test
	public void testArrayToBooleanConversion() {
		Boolean[] objects = BOOLEANS;
		Object result = TypeConverterManager.convertType(objects, boolean[].class);

		assertNotNull(result);
		assertEquals(BOOLEANS1, (boolean[]) result);
	}

	@Test
	public void testBooleanToArrayConversion() {
		boolean [] primitives = BOOLEANS1;
		Object result = TypeConverterManager.convertType(primitives, Boolean[].class);

		assertNotNull(result);
		assertEquals(BOOLEANS, (Boolean[]) result);
	}

	// ---------------------------------------------------------------- single value

	@Test
	public void testArrayToIntConversionSingleValue() {
		Object result = TypeConverterManager.convertType(Integer.valueOf(173), int[].class);

		assertNotNull(result);
		assertEquals(new int[] {173}, (int[]) result);
	}

	@Test
	public void testIntToArrayConversionSingleValue() {
		Object result = TypeConverterManager.convertType(173, Integer[].class);

		assertNotNull(result);
		assertEquals(new Integer[] {173}, (Integer[]) result);
	}

	// ---------------------------------------------------------------- string csv

	@Test
	public void testArrayToIntConversionCommaSeparated() {
		Object result = TypeConverterManager.convertType("1,2,3", int[].class);

		assertNotNull(result);
		assertEquals(new int[] {1,2,3}, (int[]) result);
	}

	@Test
	public void testIntToArrayConversionCommaSeparated() {
		Object result = TypeConverterManager.convertType("1,2,3", Integer[].class);

		assertNotNull(result);
		assertEquals(new Integer[] {1,2,3}, (Integer[]) result);
	}


}
