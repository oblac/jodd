// Copyright (c) 2003-2011, Jodd Team (jodd.org). All Rights Reserved.

package jodd.typeconverter;

import jodd.typeconverter.impl.FloatConverter;

import java.math.BigDecimal;

public class FloatConverterTest extends BaseTestCase {

	public void testConversion() {
		assertNull(FloatConverter.valueOf(null));

		assertEquals(Float.valueOf(1), FloatConverter.valueOf(Integer.valueOf(1)));
		assertEquals(Float.valueOf((float) 1.73), FloatConverter.valueOf(Double.valueOf(1.73D)));
		assertEquals(Float.valueOf((float) 1.73), FloatConverter.valueOf("1.73"));
		assertEquals(Float.valueOf((float) 1.73), FloatConverter.valueOf(new BigDecimal("1.73")));

		try {
			FloatConverter.valueOf("aaaa");
			fail();
		} catch (TypeConversionException ignore) {
		}
	}
}
