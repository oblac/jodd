// Copyright (c) 2003-present, Jodd Team (jodd.org). All Rights Reserved.

package jodd.typeconverter;

import jodd.mutable.MutableInteger;
import jodd.typeconverter.impl.MutableIntegerConverter;
import org.junit.Test;

import static org.junit.Assert.*;

public class MutableIntegerConverterTest {

	@Test
	public void testConversion() {
		MutableIntegerConverter mutableIntegerConverter = (MutableIntegerConverter) TypeConverterManager.lookup(MutableInteger.class);

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

