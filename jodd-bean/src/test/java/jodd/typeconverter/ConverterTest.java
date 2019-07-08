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

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.math.BigInteger;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

@SuppressWarnings("ALL")
class ConverterTest {

	@Test
	void testAllConversions() {

		assertEquals(new BigDecimal("11.2"), Converter.get().toBigDecimal("11.2"));
		assertNull(Converter.get().toBigDecimal(null));
		assertEquals(new BigInteger("123456789"), Converter.get().toBigInteger("123456789"));
		assertEquals(Boolean.TRUE, Converter.get().toBoolean(("true")));
		assertEquals(true, Converter.get().toBooleanValue("true", false));
		assertEquals(false, Converter.get().toBooleanValue(null, false));
		assertEquals(true, Converter.get().toBooleanValue("true"));
		assertEquals(false, Converter.get().toBooleanValue(null));


		assertEquals(Character.valueOf('A'), Converter.get().toCharacter("A"));
		assertEquals('A', Converter.get().toCharValue("A", ' '));
		assertEquals('A', Converter.get().toCharValue("A"));

		assertEquals(Integer.class, Converter.get().toClass("java.lang.Integer"));

		assertEquals(Integer.class, Converter.get().toClassArray("java.lang.Integer")[0]);
		assertEquals(1, Converter.get().toClassArray("java.lang.Integer").length);


		assertEquals(173, Converter.get().toIntValue("173"));
		assertEquals(173, Converter.get().toLongValue("173"));
		assertEquals(173, Converter.get().toShortValue("173"));
		assertEquals(17, Converter.get().toByteValue("17"));

		assertEquals(1.0d, Converter.get().toDouble("1").doubleValue(), 0.005);
		assertEquals(1.0d, Converter.get().toDoubleValue("1", 0), 0.005);
		assertEquals(1.0d, Converter.get().toDoubleValue("1"), 0.005);

		assertEquals(1.0f, Converter.get().toFloat("1").floatValue(), 0.005);
		assertEquals(1.0f, Converter.get().toFloatValue("1", 0), 0.005);
		assertEquals(1.0f, Converter.get().toFloat("1").floatValue(), 0.005);
		assertEquals(1.0f, Converter.get().toFloatValue("1"), 0.005);

		assertEquals(12, Converter.get().toInteger("12").intValue());


		assertEquals(555, Converter.get().toLong("555").longValue());

		assertEquals(555, Converter.get().toShort("555").shortValue());
		assertEquals(55, Converter.get().toByte("55").byteValue());

		assertNotNull(Converter.get().toString("555"));
	}

	@Test
	void testArrayConversion() {
		assertArrayEquals(new String[]{"555", "12"}, Converter.get().toStringArray("555,12"));
		assertArrayEquals(new String[]{"555", " 12"}, Converter.get().toStringArray("555, 12"));
		assertArrayEquals(new boolean[] {true, false, true}, Converter.get().toBooleanArray("1, 0, true"));
		assertArrayEquals(new int[] {1, 2, -3}, Converter.get().toIntegerArray("1, 2, -3"));
		assertArrayEquals(new long[] {-12, 2}, Converter.get().toLongArray("-12, 2"));
		assertArrayEquals(new float[] {1.1f, 2.2f}, Converter.get().toFloatArray("1.1, 2.2"), 0.5f);
		assertArrayEquals(new double[] {1.1, 2.2, -3.3}, Converter.get().toDoubleArray("1.1, 2.2, -3.3"), 0.5);
		assertArrayEquals(new short[] {-1, 2}, Converter.get().toShortArray("-1,2"));
		assertArrayEquals(new char[] {'a', ',', 'A'}, Converter.get().toCharacterArray("a,A"));
	}

	@Test
	void testDefaultConversion() {

		assertEquals(true, Converter.get().toBooleanValue(null, true));
		assertEquals((byte) 23, Converter.get().toByteValue(null, (byte) 23));
		assertEquals('A', Converter.get().toCharValue(null, 'A'));
		assertEquals(1.4d, Converter.get().toDoubleValue(null, 1.4d), 0.005);
		assertEquals(1.4f, Converter.get().toFloatValue(null, 1.4f), 0.005);
		assertEquals(23L, Converter.get().toLongValue(null, 23L));
		assertEquals(7, Converter.get().toIntValue(null, 7));
		assertEquals(7, Converter.get().toShortValue(null, (short) 7));

		BigDecimal defaultBigDecimal = new BigDecimal("1.1");
		assertEquals(defaultBigDecimal, Converter.get().toBigDecimal(null, defaultBigDecimal));

		BigInteger defaultBigInteger = new BigInteger("173");
		assertEquals(defaultBigInteger, Converter.get().toBigInteger(null, defaultBigInteger));

		String defaultString = "123qweasdzxc";
		assertEquals(defaultString, Converter.get().toString(null, defaultString));

		assertEquals(Boolean.TRUE, Converter.get().toBoolean(null, Boolean.TRUE));
		assertEquals(Byte.valueOf((byte) 123), Converter.get().toByte(null, Byte.valueOf((byte) 123)));
		assertEquals(Character.valueOf('A'), Converter.get().toCharacter(null, Character.valueOf('A')));
		assertEquals(Double.valueOf(5), Converter.get().toDouble(null, Double.valueOf(5)));
		assertEquals(Float.valueOf(5), Converter.get().toFloat(null, Float.valueOf(5)));
		assertEquals(Long.valueOf(7), Converter.get().toLong(null, Long.valueOf(7)));
		assertEquals(Integer.valueOf(8), Converter.get().toInteger(null, Integer.valueOf(8)));
		assertEquals(Short.valueOf((short) 3), Converter.get().toShort(null, Short.valueOf((short) 3)));

	}

	@Test
	void testChangeConverter() {
		String[] array = new String[]{"true", "false", "yeah"};

		boolean[] barr;

		try {
			barr = Converter.get().toBooleanArray(array);
			fail("error");
		} catch (Exception ignore) {
		}

		// create new boolean type converter

		TypeConverter<Boolean> booleanTypeConverter = new TypeConverter<Boolean>() {
			public Boolean convert(Object value) {
				if (value.equals("true")) {
					return Boolean.TRUE;
				}
				if (value.equals("yeah")) {
					return Boolean.TRUE;
				}
				return Boolean.FALSE;
			}
		};

		// change boolean converter! boolean[] depends on it!

		TypeConverterManager typeConverterManager = TypeConverterManager.get();

		typeConverterManager.register(boolean.class, booleanTypeConverter);

		barr = Converter.get().toBooleanArray(array);

		assertTrue(barr[0]);
		assertFalse(barr[1]);
		assertTrue(barr[2]);

		// return back
		TypeConverter tc = typeConverterManager.lookup(Boolean.class);
		typeConverterManager.register(boolean.class, tc);
	}

}
