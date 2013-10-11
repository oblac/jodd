//  Copyright (c) 2003-2013, Jodd Team (jodd.org). All Rights Reserved.

package jodd.typeconverter;

import org.junit.Test;

import java.util.List;

import static jodd.typeconverter.TypeConverterTestHelper.arri;
import static jodd.typeconverter.TypeConverterTestHelper.arro;
import static jodd.typeconverter.TypeConverterTestHelper.iterableo;
import static jodd.typeconverter.TypeConverterTestHelper.listo;
import static jodd.typeconverter.TypeConverterTestHelper.seto;
import static org.junit.Assert.assertEquals;

public class MixTest {

	@Test
	public void testStringArrayMix() {
		String[] strings;

		strings = TypeConverterManager.convertType(new Class[]{Long.class, int.class}, String[].class);
		assertEquals("java.lang.Long", strings[0]);
		assertEquals("int", strings[1]);

		strings = TypeConverterManager.convertType("one,two", String[].class);
		assertEquals("one", strings[0]);
		assertEquals("two", strings[1]);

		strings = TypeConverterManager.convertType(arri(1,23), String[].class);
		assertEquals("1", strings[0]);
		assertEquals("23", strings[1]);

		strings = TypeConverterManager.convertType(arro(Integer.valueOf(173), "Foo"), String[].class);
		assertEquals("173", strings[0]);
		assertEquals("Foo", strings[1]);

		strings = TypeConverterManager.convertType(listo(Integer.valueOf(123), "234"), String[].class);
		assertEquals("123", strings[0]);
		assertEquals("234", strings[1]);

		strings = TypeConverterManager.convertType(seto(Integer.valueOf(123), "234"), String[].class);
		assertEquals("123", strings[0]);
		assertEquals("234", strings[1]);

		strings = TypeConverterManager.convertType(iterableo(Integer.valueOf(123), "234"), String[].class);
		assertEquals("123", strings[0]);
		assertEquals("234", strings[1]);
	}

	@Test
	public void testIntArrayMix() {
		int[] ints;

		ints = TypeConverterManager.convertType("123, 234", int[].class);
		assertEquals(123, ints[0]);
		assertEquals(234, ints[1]);

		ints = TypeConverterManager.convertType(arri(1,23), int[].class);
		assertEquals(1, ints[0]);
		assertEquals(23, ints[1]);

		ints = TypeConverterManager.convertType(arro(Integer.valueOf(173), Float.valueOf(2.5f)), int[].class);
		assertEquals(173, ints[0]);
		assertEquals(2, ints[1]);

		ints = TypeConverterManager.convertType(listo(Integer.valueOf(123), "234"), int[].class);
		assertEquals(123, ints[0]);
		assertEquals(234, ints[1]);

		ints = TypeConverterManager.convertType(seto(Integer.valueOf(123), "234"), int[].class);
		assertEquals(123, ints[0]);
		assertEquals(234, ints[1]);

		ints = TypeConverterManager.convertType(iterableo(Integer.valueOf(123), "234"), int[].class);
		assertEquals(123, ints[0]);
		assertEquals(234, ints[1]);
	}

	@Test
	public void testLongArrayMix() {
		long[] longs;

		longs = TypeConverterManager.convertType("123, 234", long[].class);
		assertEquals(123, longs[0]);
		assertEquals(234, longs[1]);

		longs = TypeConverterManager.convertType(arri(1,23), long[].class);
		assertEquals(1, longs[0]);
		assertEquals(23, longs[1]);

		longs = TypeConverterManager.convertType(arro(Integer.valueOf(173), Float.valueOf(2.5f)), long[].class);
		assertEquals(173, longs[0]);
		assertEquals(2, longs[1]);

		longs = TypeConverterManager.convertType(listo(Integer.valueOf(123), "234"), long[].class);
		assertEquals(123, longs[0]);
		assertEquals(234, longs[1]);

		longs = TypeConverterManager.convertType(seto(Integer.valueOf(123), "234"), long[].class);
		assertEquals(123, longs[0]);
		assertEquals(234, longs[1]);

		longs = TypeConverterManager.convertType(iterableo(Integer.valueOf(123), "234"), long[].class);
		assertEquals(123, longs[0]);
		assertEquals(234, longs[1]);
	}

	@Test
	public void testFloatArrayMix() {
		float[] floats;

		floats = TypeConverterManager.convertType("123.1, 234.2", float[].class);
		assertEquals(123.1, floats[0], 0.1);
		assertEquals(234.2, floats[1], 0.1);

		floats = TypeConverterManager.convertType(arri(1,23), float[].class);
		assertEquals(1, floats[0], 0.1);
		assertEquals(23, floats[1], 0.1);

		floats = TypeConverterManager.convertType(arro(Integer.valueOf(173), Float.valueOf(2.5f)), float[].class);
		assertEquals(173, floats[0], 0.1);
		assertEquals(2.5, floats[1], 0.1);

		floats = TypeConverterManager.convertType(listo(Integer.valueOf(123), "234"), float[].class);
		assertEquals(123, floats[0], 0.1);
		assertEquals(234, floats[1], 0.1);

		floats = TypeConverterManager.convertType(seto(Integer.valueOf(123), "234"), float[].class);
		assertEquals(123, floats[0], 0.1);
		assertEquals(234, floats[1], 0.1);

		floats = TypeConverterManager.convertType(iterableo(Integer.valueOf(123), "234"), float[].class);
		assertEquals(123, floats[0], 0.1);
		assertEquals(234, floats[1], 0.1);
	}

	@Test
	public void testDoubleArrayMix() {
		double[] doubles;

		doubles = TypeConverterManager.convertType("123.1, 234.2", double[].class);
		assertEquals(123.1, doubles[0], 0.1);
		assertEquals(234.2, doubles[1], 0.1);

		doubles = TypeConverterManager.convertType(arri(1,23), double[].class);
		assertEquals(1, doubles[0], 0.1);
		assertEquals(23, doubles[1], 0.1);

		doubles = TypeConverterManager.convertType(arro(Integer.valueOf(173), Float.valueOf(2.5f)), double[].class);
		assertEquals(173, doubles[0], 0.1);
		assertEquals(2.5, doubles[1], 0.1);

		doubles = TypeConverterManager.convertType(listo(Integer.valueOf(123), "234"), double[].class);
		assertEquals(123, doubles[0], 0.1);
		assertEquals(234, doubles[1], 0.1);

		doubles = TypeConverterManager.convertType(seto(Integer.valueOf(123), "234"), double[].class);
		assertEquals(123, doubles[0], 0.1);
		assertEquals(234, doubles[1], 0.1);

		doubles = TypeConverterManager.convertType(iterableo(Integer.valueOf(123), "234"), double[].class);
		assertEquals(123, doubles[0], 0.1);
		assertEquals(234, doubles[1], 0.1);
	}

	@Test
	public void testByteArrayMix() {
		byte[] bytes;

		bytes = TypeConverterManager.convertType("123, -12", byte[].class);
		assertEquals(123, bytes[0]);
		assertEquals(-12, bytes[1]);

		bytes = TypeConverterManager.convertType(arri(1,23), byte[].class);
		assertEquals(1, bytes[0]);
		assertEquals(23, bytes[1]);

		bytes = TypeConverterManager.convertType(arro(Integer.valueOf(127), Float.valueOf(2.5f)), byte[].class);
		assertEquals(127, bytes[0]);
		assertEquals(2, bytes[1]);

		bytes = TypeConverterManager.convertType(listo(Integer.valueOf(123), "-12"), byte[].class);
		assertEquals(123, bytes[0]);
		assertEquals(-12, bytes[1]);

		bytes = TypeConverterManager.convertType(seto(Integer.valueOf(123), "-12"), byte[].class);
		assertEquals(123, bytes[0]);
		assertEquals(-12, bytes[1]);

		bytes = TypeConverterManager.convertType(iterableo(Integer.valueOf(123), "-12"), byte[].class);
		assertEquals(123, bytes[0]);
		assertEquals(-12, bytes[1]);
	}

	@Test
	public void testShortArrayMix() {
		short[] shorts;

		shorts = TypeConverterManager.convertType("123, -12", short[].class);
		assertEquals(123, shorts[0]);
		assertEquals(-12, shorts[1]);

		shorts = TypeConverterManager.convertType(arri(1,23), short[].class);
		assertEquals(1, shorts[0]);
		assertEquals(23, shorts[1]);

		shorts = TypeConverterManager.convertType(arro(Integer.valueOf(127), Float.valueOf(2.5f)), short[].class);
		assertEquals(127, shorts[0]);
		assertEquals(2, shorts[1]);

		shorts = TypeConverterManager.convertType(listo(Integer.valueOf(123), "-12"), short[].class);
		assertEquals(123, shorts[0]);
		assertEquals(-12, shorts[1]);

		shorts = TypeConverterManager.convertType(seto(Integer.valueOf(123), "-12"), short[].class);
		assertEquals(123, shorts[0]);
		assertEquals(-12, shorts[1]);

		shorts = TypeConverterManager.convertType(iterableo(Integer.valueOf(123), "-12"), short[].class);
		assertEquals(123, shorts[0]);
		assertEquals(-12, shorts[1]);
	}

	@Test
	public void testCharArrayMix() {
		char[] chars;

		chars = TypeConverterManager.convertType("123, -12", char[].class);
		assertEquals("123, -12", new String(chars));

		chars = TypeConverterManager.convertType(arri(1,23), char[].class);
		assertEquals(1, chars[0]);
		assertEquals(23, chars[1]);

		chars = TypeConverterManager.convertType(arro(Integer.valueOf(127), Float.valueOf(2.5f)), char[].class);
		assertEquals(127, chars[0]);
		assertEquals(2, chars[1]);

		chars = TypeConverterManager.convertType(listo(Integer.valueOf(123), "-12"), char[].class);
		assertEquals(123, chars[0]);
		assertEquals(65524, chars[1]);

		chars = TypeConverterManager.convertType(seto(Integer.valueOf(123), "-12"), char[].class);
		assertEquals(123, chars[0]);
		assertEquals(65524, chars[1]);

		chars = TypeConverterManager.convertType(iterableo(Integer.valueOf(123), "-12"), char[].class);
		assertEquals(123, chars[0]);
		assertEquals(65524, chars[1]);
	}

/*	@Test
	public void testToList() {
		TypeConverterManager.convertType(arri(1,2,3), List.class);
	}
*/

}