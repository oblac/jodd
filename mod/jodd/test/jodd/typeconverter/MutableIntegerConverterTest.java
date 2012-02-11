// Copyright (c) 2003-2012, Jodd Team (jodd.org). All Rights Reserved.

package jodd.typeconverter;

import jodd.typeconverter.impl.MutableIntegerConverter;
import junit.framework.TestCase;
import jodd.mutable.MutableInteger;

public class MutableIntegerConverterTest extends TestCase {

	public void testConversion() {
		MutableIntegerConverter mutableIntegerConverter = new MutableIntegerConverter();
		
		assertNull(mutableIntegerConverter.convert(null));
		
		assertEquals(new MutableInteger(1), mutableIntegerConverter.convert(new MutableInteger(1)));
		assertEquals(new MutableInteger(1), mutableIntegerConverter.convert(Integer.valueOf(1)));
		assertEquals(new MutableInteger(1), mutableIntegerConverter.convert(Short.valueOf((short) 1)));
		assertEquals(new MutableInteger(1), mutableIntegerConverter.convert(Double.valueOf(1.0D)));
		assertEquals(new MutableInteger(1), mutableIntegerConverter.convert("1"));
		assertEquals(new MutableInteger(1), mutableIntegerConverter.convert(" 1 "));

		try {
			mutableIntegerConverter.convert("a");
			fail();
		} catch (TypeConversionException ignore) {
		}
	}
}

