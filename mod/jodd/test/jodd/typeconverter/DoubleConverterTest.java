// Copyright (c) 2003-2011, Jodd Team (jodd.org). All Rights Reserved.

package jodd.typeconverter;

import jodd.typeconverter.impl.DoubleConverter;

import java.math.BigDecimal;

public class DoubleConverterTest extends BaseTestCase {

	public void testConversion() {
		assertNull(DoubleConverter.valueOf(null));

		assertEquals(Double.valueOf(1), DoubleConverter.valueOf(Integer.valueOf(1)));
		assertEquals(Double.valueOf(1.73), DoubleConverter.valueOf(Double.valueOf(1.73D)));
		assertEquals(Double.valueOf(1.73), DoubleConverter.valueOf("1.73"));
		assertEquals(Double.valueOf(1.73), DoubleConverter.valueOf(new BigDecimal("1.73")));

		try {
			DoubleConverter.valueOf("aaaa");
			fail();
		} catch (TypeConversionException ignore) {
		}
	}
}
