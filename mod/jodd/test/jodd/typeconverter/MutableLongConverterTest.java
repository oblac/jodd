// Copyright (c) 2003-2011, Jodd Team (jodd.org). All Rights Reserved.

package jodd.typeconverter;

import jodd.mutable.MutableLong;
import jodd.typeconverter.impl.MutableLongConverter;
import junit.framework.TestCase;

public class MutableLongConverterTest extends TestCase {

    public void testConversion() {
        assertNull(MutableLongConverter.valueOf(null));

        assertEquals(new MutableLong(173), MutableLongConverter.valueOf(new MutableLong(173)));
        assertEquals(new MutableLong(173), MutableLongConverter.valueOf(Integer.valueOf(173)));
        assertEquals(new MutableLong(173), MutableLongConverter.valueOf(Long.valueOf(173)));
        assertEquals(new MutableLong(173), MutableLongConverter.valueOf(Short.valueOf((short) 173)));
        assertEquals(new MutableLong(173), MutableLongConverter.valueOf(Double.valueOf(173.0D)));
        assertEquals(new MutableLong(173), MutableLongConverter.valueOf(Float.valueOf(173.0F)));
        assertEquals(new MutableLong(173), MutableLongConverter.valueOf("173"));

        try {
            MutableLongConverter.valueOf("a");
            fail();
        } catch (TypeConversionException ignore) {
        }
    }
}

