// Copyright (c) 2003-2010, Jodd Team (jodd.org). All Rights Reserved.

package jodd.typeconverter;

import jodd.typeconverter.impl.LongConverter;
import junit.framework.TestCase;

public class LongConverterTest extends TestCase {

    public void testConversion() {
        assertNull(LongConverter.valueOf(null));

        assertEquals(Long.valueOf(173), LongConverter.valueOf(Long.valueOf(173)));

        assertEquals(Long.valueOf(173), LongConverter.valueOf(Integer.valueOf(173)));
        assertEquals(Long.valueOf(173), LongConverter.valueOf(Short.valueOf((short) 173)));
        assertEquals(Long.valueOf(173), LongConverter.valueOf(Double.valueOf(173.0D)));
        assertEquals(Long.valueOf(173), LongConverter.valueOf(Float.valueOf(173.0F)));
        assertEquals(Long.valueOf(173), LongConverter.valueOf("173"));

        try {
            LongConverter.valueOf("a");
            fail();
        } catch (TypeConversionException ignore) {
        }
    }
}
