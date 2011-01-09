// Copyright (c) 2003-2011, Jodd Team (jodd.org). All Rights Reserved.

package jodd.typeconverter;

import jodd.mutable.MutableFloat;
import jodd.typeconverter.impl.MutableFloatConverter;
import junit.framework.TestCase;

import java.math.BigDecimal;

public class MutableFloatConverterTest extends TestCase {

    public void testConversion() {
        assertNull(MutableFloatConverter.valueOf(null));

        assertEquals(new MutableFloat(1.73f), MutableFloatConverter.valueOf(new MutableFloat(1.73f)));
        assertEquals(new MutableFloat(1), MutableFloatConverter.valueOf(Integer.valueOf(1)));
        assertEquals(new MutableFloat(1.73f), MutableFloatConverter.valueOf(Double.valueOf(1.73D)));
        assertEquals(new MutableFloat(1.73f), MutableFloatConverter.valueOf("1.73"));
        assertEquals(new MutableFloat(1.73f), MutableFloatConverter.valueOf(new BigDecimal("1.73")));

        try {
            MutableFloatConverter.valueOf("aaaa");
            fail();
        } catch (TypeConversionException ignore) {
        }
    }
}

