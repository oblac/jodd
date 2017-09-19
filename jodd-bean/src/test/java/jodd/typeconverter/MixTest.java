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
import jodd.typeconverter.impl.CollectionConverter;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import static jodd.typeconverter.TypeConverterTestHelper.arri;
import static jodd.typeconverter.TypeConverterTestHelper.arrl;
import static jodd.typeconverter.TypeConverterTestHelper.arro;
import static jodd.typeconverter.TypeConverterTestHelper.iterableo;
import static jodd.typeconverter.TypeConverterTestHelper.listo;
import static jodd.typeconverter.TypeConverterTestHelper.seto;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

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

	@Test
	public void testMultipleArrays() {
		MutableInteger[] mutableIntegers = TypeConverterManager.convertType(arri(1,2,3,4), MutableInteger[].class);

		assertEquals(4, mutableIntegers.length);
		assertEquals(1, mutableIntegers[0].intValue());
		assertEquals(2, mutableIntegers[1].intValue());
		assertEquals(3, mutableIntegers[2].intValue());
		assertEquals(4, mutableIntegers[3].intValue());
	}

	@Test
	public void testCollections() {
		List list1 = TypeConverterManager.convertType(arri(1,2,3), List.class);
		assertEquals(listo(1,2,3), list1);

		list1 = TypeConverterManager.convertType("1,2,3", List.class);
		assertEquals(listo("1","2","3"), list1);

		Set set1 = TypeConverterManager.convertType(arrl(1, 2, 3), LinkedHashSet.class);
		assertTrue(set1 instanceof LinkedHashSet);
		Iterator i = set1.iterator();
		assertEquals(Long.valueOf(1), i.next());
		assertEquals(Long.valueOf(2), i.next());
		assertEquals(Long.valueOf(3), i.next());

		list1 = TypeConverterManager.convertType("hello", List.class);
		assertEquals(listo("hello"), list1);

		list1 = TypeConverterManager.convertType(Long.valueOf(4), List.class);
		assertEquals(listo(Long.valueOf(4)), list1);
	}

	@Test
	public void testCollectionsWithComponentType() {
		TypeConverterManagerBean tcm = TypeConverterManager.getDefaultTypeConverterManager();

		CollectionConverter cc = new CollectionConverter(tcm, List.class, String.class);
		List<String> list1 = (List<String>) cc.convert(arri(1, 2, 3));
		assertNotEquals(listo(1, 2, 3), list1);
		assertEquals(listo("1", "2", "3"), list1);

		list1 = (List<String>) cc.convert("1,2,3");
		assertEquals(listo("1","2","3"), list1);

		cc = new CollectionConverter(tcm, List.class, Integer.class);
		list1 = (List<String>) cc.convert("1,2,3");
		assertEquals(listo(1, 2, 3), list1);
	}

	@Test
	public void testListToArray() {
		List<Long> list = new ArrayList<>();
		list.add(1L);
		list.add(9L);
		list.add(2L);

		Long[] array = TypeConverterManager.convertType(list, Long[].class);

		assertEquals(3, array.length);
		assertEquals(1, array[0].longValue());
		assertEquals(9, array[1].longValue());
		assertEquals(2, array[2].longValue());
	}

}
