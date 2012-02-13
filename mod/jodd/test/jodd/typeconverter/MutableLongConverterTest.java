// Copyright (c) 2003-2012, Jodd Team (jodd.org). All Rights Reserved.

package jodd.typeconverter;

import jodd.mutable.MutableLong;
import jodd.typeconverter.impl.MutableLongConverter;
import junit.framework.TestCase;

public class MutableLongConverterTest extends TestCase {

    public void testConversion() {
		MutableLongConverter mutableLongConverter = (MutableLongConverter) TypeConverterManager.lookup(MutableLong.class);
		
        assertNull(mutableLongConverter.convert(null));

        assertEquals(new MutableLong(173), mutableLongConverter.convert(new MutableLong(173)));
        assertEquals(new MutableLong(173), mutableLongConverter.convert(Integer.valueOf(173)));
        assertEquals(new MutableLong(173), mutableLongConverter.convert(Long.valueOf(173)));
        assertEquals(new MutableLong(173), mutableLongConverter.convert(Short.valueOf((short) 173)));
        assertEquals(new MutableLong(173), mutableLongConverter.convert(Double.valueOf(173.0D)));
        assertEquals(new MutableLong(173), mutableLongConverter.convert(Float.valueOf(173.0F)));
        assertEquals(new MutableLong(173), mutableLongConverter.convert("173"));
        assertEquals(new MutableLong(173), mutableLongConverter.convert(" 173 "));

        try {
            mutableLongConverter.convert("a");
            fail();
        } catch (TypeConversionException ignore) {
        }
    }
}

