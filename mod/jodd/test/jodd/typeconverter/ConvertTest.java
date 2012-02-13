// Copyright (c) 2003-2012, Jodd Team (jodd.org). All Rights Reserved.

package jodd.typeconverter;

import jodd.datetime.JDateTime;
import junit.framework.TestCase;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.GregorianCalendar;

public class ConvertTest extends TestCase {
	
	public void testAllConversions() {
		
		assertEquals(new BigDecimal("11.2"), Convert.toBigDecimal("11.2"));
		assertNull(Convert.toBigDecimal(null));
		assertEquals(new BigInteger("123456789"), Convert.toBigInteger("123456789"));
		assertEquals(Boolean.TRUE, Convert.toBoolean(("true")));
		assertEquals(true, Convert.toBoolean("true", false));
		assertEquals(false, Convert.toBoolean(null, false));
		assertEquals(true, Convert.toBooleanValue("true"));
		assertEquals(false, Convert.toBooleanValue(null));

		assertNotNull(Convert.toCalendar((new JDateTime())));
		
		assertEquals(Character.valueOf('A'), Convert.toCharacter("A"));
		assertEquals('A', Convert.toCharacter("A", ' '));
		assertEquals('A', Convert.toCharacterValue("A"));
		
		assertEquals(Integer.class, Convert.toClass("java.lang.Integer"));

		assertEquals(Integer.class, Convert.toClassArray("java.lang.Integer")[0]);
		assertEquals(1, Convert.toClassArray("java.lang.Integer").length);

		assertNotNull(Convert.toDate(new JDateTime()));

		assertEquals(1.0d, Convert.toDouble("1"));
		assertEquals(1.0d, Convert.toDouble("1", 0));
		assertEquals(1.0d, Convert.toDoubleValue("1"));

		assertEquals(1.0f, Convert.toFloat("1"));
		assertEquals(1.0f, Convert.toFloat("1", 0));
		assertEquals(1.0f, Convert.toFloat("1"));
		assertEquals(1.0f, Convert.toFloatValue("1"));

		assertEquals(12, Convert.toInteger("12").intValue());

		assertNotNull(Convert.toJDateTime(new GregorianCalendar()));

		assertEquals(555, Convert.toLong("555").longValue());

		assertEquals(555, Convert.toShort("555").shortValue());

		assertNotNull(Convert.toString("555"));
		assertNotNull(Convert.toBooleanArray("1"));
		assertNotNull(Convert.toIntegerArray("1"));
		assertNotNull(Convert.toLongArray("1"));
		assertNotNull(Convert.toFloatArray("1"));
		assertNotNull(Convert.toDoubleArray("1"));
		assertNotNull(Convert.toShortArray("1"));
//		assertNotNull(Convert.toCharacterArray("A"));
	}
	
	public void testChangeConverter() {
		String[] array = new String[] {"true", "false", "yeah"};

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
	}
	
}
