// Copyright (c) 2003-2012, Jodd Team (jodd.org). All Rights Reserved.

package jodd.typeconverter;

import jodd.typeconverter.impl.ShortConverter;
import junit.framework.TestCase;

public class ShortConverterTest extends TestCase {

    public void testConversion() {
		ShortConverter shortConverter = new ShortConverter();

        assertNull(shortConverter.convert(null));

        assertEquals(Short.valueOf((short) 1), shortConverter.convert(Short.valueOf((short) 1)));
        assertEquals(Short.valueOf((short) 1), shortConverter.convert(Integer.valueOf(1)));
        assertEquals(Short.valueOf((short) 1), shortConverter.convert(Double.valueOf(1.0D)));
        assertEquals(Short.valueOf((short) 1), shortConverter.convert("1"));
        assertEquals(Short.valueOf((short) 1), shortConverter.convert(" 1 "));

		assertEquals(Short.valueOf((short) 1), shortConverter.convert(" +1 "));
        assertEquals(Short.valueOf((short) -1), shortConverter.convert(" -1 "));
		assertEquals(Short.valueOf((short) 32767), shortConverter.convert(" +32767 "));
        assertEquals(Short.valueOf((short) -32768), shortConverter.convert(" -32768 "));

        try {
            shortConverter.convert("a");
            fail();
        } catch (TypeConversionException ignore) {
        }
        try {
            shortConverter.convert("+32768");
            fail();
        } catch (TypeConversionException ignore) {
        }
        try {
            shortConverter.convert("-32769");
            fail();
        } catch (TypeConversionException ignore) {
        }
    }
}
