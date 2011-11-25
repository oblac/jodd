// Copyright (c) 2003-2011, Jodd Team (jodd.org). All Rights Reserved.

package jodd.typeconverter;

import jodd.typeconverter.impl.ShortConverter;
import junit.framework.TestCase;

public class ShortConverterTest extends TestCase {

    public void testConversion() {
        assertNull(ShortConverter.valueOf(null));

        assertEquals(Short.valueOf((short) 1), ShortConverter.valueOf(Short.valueOf((short) 1)));
        assertEquals(Short.valueOf((short) 1), ShortConverter.valueOf(Integer.valueOf(1)));
        assertEquals(Short.valueOf((short) 1), ShortConverter.valueOf(Double.valueOf(1.0D)));
        assertEquals(Short.valueOf((short) 1), ShortConverter.valueOf("1"));
        assertEquals(Short.valueOf((short) 1), ShortConverter.valueOf(" 1 "));

		assertEquals(Short.valueOf((short) 1), ShortConverter.valueOf(" +1 "));
        assertEquals(Short.valueOf((short) -1), ShortConverter.valueOf(" -1 "));
		assertEquals(Short.valueOf((short) 32767), ShortConverter.valueOf(" +32767 "));
        assertEquals(Short.valueOf((short) -32768), ShortConverter.valueOf(" -32768 "));

        try {
            ShortConverter.valueOf("a");
            fail();
        } catch (TypeConversionException ignore) {
        }
        try {
            ShortConverter.valueOf("+32768");
            fail();
        } catch (TypeConversionException ignore) {
        }
        try {
            ShortConverter.valueOf("-32769");
            fail();
        } catch (TypeConversionException ignore) {
        }
    }
}
