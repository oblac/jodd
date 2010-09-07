// Copyright (c) 2003-2010, Jodd Team (jodd.org). All Rights Reserved.

package jodd.typeconverter;

import junit.framework.TestCase;

public class ByteConverterTest extends TestCase {

	public void testConversion() {
		assertNull(ByteConverter.valueOf(null));

		assertEquals(Byte.valueOf((byte) 1), ByteConverter.valueOf(Integer.valueOf(1)));
		assertEquals(Byte.valueOf((byte) 1), ByteConverter.valueOf(Short.valueOf((short) 1)));
		assertEquals(Byte.valueOf((byte) 1), ByteConverter.valueOf(Double.valueOf(1.0D)));
		assertEquals(Byte.valueOf((byte) 1), ByteConverter.valueOf("1"));

		try {
			ByteConverter.valueOf("a");
			fail();
		} catch (TypeConversionException ignore) {
		}
	}
}
