// Copyright (c) 2003-2011, Jodd Team (jodd.org). All Rights Reserved.

package jodd.typeconverter;

import jodd.mutable.MutableByte;
import jodd.typeconverter.impl.MutableByteConverter;
import junit.framework.TestCase;

public class MutableByteConverterTest extends TestCase {

    public void testConversion() {
        assertNull(MutableByteConverter.valueOf(null));

        assertEquals(new MutableByte((byte) 1), MutableByteConverter.valueOf(new MutableByte((byte) 1)));
        assertEquals(new MutableByte((byte) 1), MutableByteConverter.valueOf(Integer.valueOf(1)));
        assertEquals(new MutableByte((byte) 1), MutableByteConverter.valueOf(Short.valueOf((short) 1)));
        assertEquals(new MutableByte((byte) 1), MutableByteConverter.valueOf(Double.valueOf(1.0D)));
        assertEquals(new MutableByte((byte) 1), MutableByteConverter.valueOf("1"));

        try {
            MutableByteConverter.valueOf("a");
            fail();
        } catch (TypeConversionException ignore) {
        }
    }
}

