// Copyright (c) 2003-2012, Jodd Team (jodd.org). All Rights Reserved.

package jodd.typeconverter;

import jodd.typeconverter.impl.ByteConverter;
import junit.framework.TestCase;

public class ByteConverterTest extends TestCase {

	public void testConversion() {
		ByteConverter byteConverter = new ByteConverter();
		
		assertNull(byteConverter.convert(null));

		assertEquals(Byte.valueOf((byte) 1), byteConverter.convert(Integer.valueOf(1)));
		assertEquals(Byte.valueOf((byte) 1), byteConverter.convert(Short.valueOf((short) 1)));
		assertEquals(Byte.valueOf((byte) 1), byteConverter.convert(Double.valueOf(1.5D)));
		assertEquals(Byte.valueOf((byte) 1), byteConverter.convert("1"));
		assertEquals(Byte.valueOf((byte) 1), byteConverter.convert("  1  "));
		assertEquals(Byte.valueOf((byte) 1), byteConverter.convert("  +1  "));
		assertEquals(Byte.valueOf((byte) 127), byteConverter.convert("  +127  "));
		assertEquals(Byte.valueOf((byte) -1), byteConverter.convert("  -1  "));
		assertEquals(Byte.valueOf((byte) -128), byteConverter.convert("  -128  "));
		assertEquals(Byte.valueOf((byte) (300-256)), byteConverter.convert(Integer.valueOf(300)));

		try {
			assertEquals(Byte.valueOf((byte) 1), byteConverter.convert("1.5"));
			fail();
		} catch (TypeConversionException ignore) {
		}

		try {
			byteConverter.convert("a");
			fail();
		} catch (TypeConversionException ignore) {
		}

		try {
			byteConverter.convert("128");
			fail();
		} catch (TypeConversionException ignore) {
		}
		try {
			byteConverter.convert("-129");
			fail();
		} catch (TypeConversionException ignore) {
		}
	}
}
