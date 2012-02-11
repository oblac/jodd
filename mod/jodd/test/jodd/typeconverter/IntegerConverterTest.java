// Copyright (c) 2003-2012, Jodd Team (jodd.org). All Rights Reserved.

package jodd.typeconverter;

import jodd.typeconverter.impl.IntegerConverter;
import junit.framework.TestCase;

public class IntegerConverterTest extends TestCase {

	public void testConversion() {
		IntegerConverter integerConverter = new IntegerConverter();
		
		assertNull(integerConverter.convert(null));

		assertEquals(Integer.valueOf(1), integerConverter.convert(Integer.valueOf(1)));
		assertEquals(Integer.valueOf(1), integerConverter.convert(Short.valueOf((short) 1)));
		assertEquals(Integer.valueOf(1), integerConverter.convert(Double.valueOf(1.0D)));
		assertEquals(Integer.valueOf(1), integerConverter.convert("1"));
		assertEquals(Integer.valueOf(1), integerConverter.convert(" 1 "));

		assertEquals(Integer.valueOf(1), integerConverter.convert(" +1 "));
		assertEquals(Integer.valueOf(-1), integerConverter.convert(" -1 "));
		assertEquals(Integer.valueOf(2147483647), integerConverter.convert(" +2147483647 "));
		assertEquals(Integer.valueOf(-2147483648), integerConverter.convert(" -2147483648 "));

		try {
			assertEquals(Integer.valueOf(1), integerConverter.convert(" 2147483648 "));
			fail();
		} catch (TypeConversionException ignore) {
		}
		try {
			assertEquals(Integer.valueOf(1), integerConverter.convert(" -2147483649 "));
			fail();
		} catch (TypeConversionException ignore) {
		}

		try {
			integerConverter.convert("a");
			fail();
		} catch (TypeConversionException ignore) {
		}
	}
}
