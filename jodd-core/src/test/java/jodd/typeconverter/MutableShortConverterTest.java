// Copyright (c) 2003-2012, Jodd Team (jodd.org). All Rights Reserved.

package jodd.typeconverter;

import jodd.mutable.MutableShort;
import jodd.typeconverter.impl.MutableShortConverter;
import junit.framework.TestCase;

public class MutableShortConverterTest extends TestCase {

	public void testConversion() {
		MutableShortConverter mutableShortConverter = (MutableShortConverter) TypeConverterManager.lookup(MutableShort.class);
		
		assertNull(mutableShortConverter.convert(null));

		assertEquals(new MutableShort((short)1), mutableShortConverter.convert(new MutableShort(1)));
		assertEquals(new MutableShort((short)1), mutableShortConverter.convert(Integer.valueOf(1)));
        assertEquals(new MutableShort((short)1), mutableShortConverter.convert(Short.valueOf((short) 1)));
		assertEquals(new MutableShort((short)1), mutableShortConverter.convert(Double.valueOf(1.0D)));
		assertEquals(new MutableShort((short)1), mutableShortConverter.convert("1"));
		assertEquals(new MutableShort((short)1), mutableShortConverter.convert(" 1 "));

		try {
			mutableShortConverter.convert("a");
			fail();
		} catch (TypeConversionException ignore) {
		}
	}
}

