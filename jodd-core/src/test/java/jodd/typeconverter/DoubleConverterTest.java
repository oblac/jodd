// Copyright (c) 2003-2012, Jodd Team (jodd.org). All Rights Reserved.

package jodd.typeconverter;

import jodd.typeconverter.impl.DoubleConverter;

import java.math.BigDecimal;

public class DoubleConverterTest extends BaseTestCase {

	public void testConversion() {
		DoubleConverter doubleConverter = new DoubleConverter();
		
		assertNull(doubleConverter.convert(null));

		assertEquals(Double.valueOf(1), doubleConverter.convert(Integer.valueOf(1)));
		assertEquals(Double.valueOf(1.73), doubleConverter.convert(Double.valueOf(1.73D)));
		assertEquals(Double.valueOf(1.73), doubleConverter.convert("1.73"));
		assertEquals(Double.valueOf(1.73), doubleConverter.convert(" 1.73 "));
		assertEquals(Double.valueOf(1.73), doubleConverter.convert(" +1.73 "));
		assertEquals(Double.valueOf(-1.73), doubleConverter.convert(" -1.73 "));
		assertEquals(Double.valueOf(1.73), doubleConverter.convert(new BigDecimal("1.73")));

		try {
			doubleConverter.convert("aaaa");
			fail();
		} catch (TypeConversionException ignore) {
		}
	}
}
