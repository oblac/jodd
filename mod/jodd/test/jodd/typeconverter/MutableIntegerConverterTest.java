// Copyright (c) 2003-2011, Jodd Team (jodd.org). All Rights Reserved.

package jodd.typeconverter;

import jodd.typeconverter.impl.MutableIntegerConverter;
import junit.framework.TestCase;
import jodd.mutable.MutableInteger;

public class MutableIntegerConverterTest extends TestCase {

	public void testConversion() {
		assertNull(MutableIntegerConverter.valueOf(null));
		
		assertEquals(new MutableInteger(1), MutableIntegerConverter.valueOf(new MutableInteger(1)));
		assertEquals(new MutableInteger(1), MutableIntegerConverter.valueOf(Integer.valueOf(1)));
		assertEquals(new MutableInteger(1), MutableIntegerConverter.valueOf(Short.valueOf((short) 1)));
		assertEquals(new MutableInteger(1), MutableIntegerConverter.valueOf(Double.valueOf(1.0D)));
		assertEquals(new MutableInteger(1), MutableIntegerConverter.valueOf("1"));
		assertEquals(new MutableInteger(1), MutableIntegerConverter.valueOf(" 1 "));

		try {
			MutableIntegerConverter.valueOf("a");
			fail();
		} catch (TypeConversionException ignore) {
		}
	}
}

