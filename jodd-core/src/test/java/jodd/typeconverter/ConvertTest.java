// Copyright (c) 2003-2012, Jodd Team (jodd.org). All Rights Reserved.

package jodd.typeconverter;

import jodd.AssertPrimitiveArraysTestHelper;
import jodd.datetime.JDateTime;
import org.junit.Test;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import static org.junit.Assert.*;

@SuppressWarnings("ALL")
public class ConvertTest {

	@Test
	public void testAllConversions() {

		assertEquals(new BigDecimal("11.2"), Convert.toBigDecimal("11.2"));
		assertNull(Convert.toBigDecimal(null));
		assertEquals(new BigInteger("123456789"), Convert.toBigInteger("123456789"));
		assertEquals(Boolean.TRUE, Convert.toBoolean(("true")));
		assertEquals(true, Convert.toBooleanValue("true", false));
		assertEquals(false, Convert.toBooleanValue(null, false));
		assertEquals(true, Convert.toBooleanValue("true"));
		assertEquals(false, Convert.toBooleanValue(null));

		assertNotNull(Convert.toCalendar((new JDateTime())));

		assertEquals(Character.valueOf('A'), Convert.toCharacter("A"));
		assertEquals('A', Convert.toCharValue("A", ' '));
		assertEquals('A', Convert.toCharValue("A"));

		assertEquals(Integer.class, Convert.toClass("java.lang.Integer"));

		assertEquals(Integer.class, Convert.toClassArray("java.lang.Integer")[0]);
		assertEquals(1, Convert.toClassArray("java.lang.Integer").length);

		assertNotNull(Convert.toDate(new JDateTime()));

		assertEquals(173, Convert.toIntValue("173"));
		assertEquals(173, Convert.toLongValue("173"));
		assertEquals(173, Convert.toShortValue("173"));
		assertEquals(17, Convert.toByteValue("17"));

		assertEquals(1.0d, Convert.toDouble("1").doubleValue(), 0.005);
		assertEquals(1.0d, Convert.toDoubleValue("1", 0), 0.005);
		assertEquals(1.0d, Convert.toDoubleValue("1"), 0.005);

		assertEquals(1.0f, Convert.toFloat("1").floatValue(), 0.005);
		assertEquals(1.0f, Convert.toFloatValue("1", 0), 0.005);
		assertEquals(1.0f, Convert.toFloat("1").floatValue(), 0.005);
		assertEquals(1.0f, Convert.toFloatValue("1"), 0.005);

		assertEquals(12, Convert.toInteger("12").intValue());

		assertNotNull(Convert.toJDateTime(new GregorianCalendar()));

		assertEquals(555, Convert.toLong("555").longValue());

		assertEquals(555, Convert.toShort("555").shortValue());
		assertEquals(55, Convert.toByte("55").byteValue());

		assertNotNull(Convert.toString("555"));
	}

	@Test
	public void testArrayConversion() {
		assertEquals(new String[]{"555", "12"}, Convert.toStringArray("555,12"));
		assertEquals(new String[]{"555", " 12"}, Convert.toStringArray("555, 12"));
		AssertPrimitiveArraysTestHelper.assertEquals(new boolean[]{true, false, true}, Convert.toBooleanArray("1, 0, true"));
		AssertPrimitiveArraysTestHelper.assertEquals(new int[]{1, 2, -3}, Convert.toIntegerArray("1, 2, -3"));
		AssertPrimitiveArraysTestHelper.assertEquals(new long[]{-12, 2}, Convert.toLongArray("-12, 2"));
		AssertPrimitiveArraysTestHelper.assertEquals(new float[]{1.1f, 2.2f}, Convert.toFloatArray("1.1, 2.2"), 0.5f);
		AssertPrimitiveArraysTestHelper.assertEquals(new double[]{1.1, 2.2, -3.3}, Convert.toDoubleArray("1.1, 2.2, -3.3"), 0.5);
		AssertPrimitiveArraysTestHelper.assertEquals(new short[]{-1, 2}, Convert.toShortArray("-1,2"));
		AssertPrimitiveArraysTestHelper.assertEquals(new char[]{'a', ',', 'A'}, Convert.toCharacterArray("a,A"));
	}

	@Test
	public void testDefaultConversion() {

		assertEquals(true, Convert.toBooleanValue(null, true));
		assertEquals((byte) 23, Convert.toByteValue(null, (byte) 23));
		assertEquals('A', Convert.toCharValue(null, 'A'));
		assertEquals(1.4d, Convert.toDoubleValue(null, 1.4d), 0.005);
		assertEquals(1.4f, Convert.toFloatValue(null, 1.4f), 0.005);
		assertEquals(23L, Convert.toLongValue(null, 23L));
		assertEquals(7, Convert.toIntValue(null, 7));
		assertEquals(7, Convert.toShortValue(null, (short) 7));

		BigDecimal defaultBigDecimal = new BigDecimal("1.1");
		assertEquals(defaultBigDecimal, Convert.toBigDecimal(null, defaultBigDecimal));

		BigInteger defaultBigInteger = new BigInteger("173");
		assertEquals(defaultBigInteger, Convert.toBigInteger(null, defaultBigInteger));

		String defaultString = "123qweasdzxc";
		assertEquals(defaultString, Convert.toString(null, defaultString));

		JDateTime defaultJDateTime = new JDateTime(2010, 4, 20);
		assertEquals(defaultJDateTime, Convert.toJDateTime(null, defaultJDateTime));

		Date defaultDate = defaultJDateTime.convertToDate();
		assertEquals(defaultDate, Convert.toDate(null, defaultDate));

		Calendar defaultCalendar = defaultJDateTime.convertToCalendar();
		assertEquals(defaultCalendar, Convert.toCalendar(null, defaultCalendar));

		assertEquals(Boolean.TRUE, Convert.toBoolean(null, Boolean.TRUE));
		assertEquals(Byte.valueOf((byte) 123), Convert.toByte(null, Byte.valueOf((byte) 123)));
		assertEquals(Character.valueOf('A'), Convert.toCharacter(null, Character.valueOf('A')));
		assertEquals(Double.valueOf(5), Convert.toDouble(null, Double.valueOf(5)));
		assertEquals(Float.valueOf(5), Convert.toFloat(null, Float.valueOf(5)));
		assertEquals(Long.valueOf(7), Convert.toLong(null, Long.valueOf(7)));
		assertEquals(Integer.valueOf(8), Convert.toInteger(null, Integer.valueOf(8)));
		assertEquals(Short.valueOf((short) 3), Convert.toShort(null, Short.valueOf((short) 3)));

	}

	@Test
	public void testChangeConverter() {
		String[] array = new String[]{"true", "false", "yeah"};

		boolean barr[];

		try {
			barr = Convert.toBooleanArray(array);
			fail();
		} catch (TypeConversionException tcex) {
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

		TypeConverterManager.register(boolean.class, booleanTypeConverter);

		barr = Convert.toBooleanArray(array);

		assertTrue(barr[0]);
		assertFalse(barr[1]);
		assertTrue(barr[2]);

		// return back
		TypeConverter tc = TypeConverterManager.lookup(Boolean.class);
		TypeConverterManager.register(boolean.class, tc);
	}

}
