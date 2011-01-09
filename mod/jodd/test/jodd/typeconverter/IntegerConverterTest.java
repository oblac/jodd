// Copyright (c) 2003-2011, Jodd Team (jodd.org). All Rights Reserved.

package jodd.typeconverter;

import jodd.typeconverter.impl.IntegerConverter;
import junit.framework.TestCase;

public class IntegerConverterTest extends TestCase {

	public void testConversion() {
		assertNull(IntegerConverter.valueOf(null));

		assertEquals(Integer.valueOf(1), IntegerConverter.valueOf(Integer.valueOf(1)));
		assertEquals(Integer.valueOf(1), IntegerConverter.valueOf(Short.valueOf((short) 1)));
		assertEquals(Integer.valueOf(1), IntegerConverter.valueOf(Double.valueOf(1.0D)));
		assertEquals(Integer.valueOf(1), IntegerConverter.valueOf("1"));

		try {
			IntegerConverter.valueOf("a");
			fail();
		} catch (TypeConversionException ignore) {
		}
	}
}
