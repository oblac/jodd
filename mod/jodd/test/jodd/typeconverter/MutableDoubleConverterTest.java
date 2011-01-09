// Copyright (c) 2003-2011, Jodd Team (jodd.org). All Rights Reserved.

package jodd.typeconverter;

import jodd.mutable.MutableDouble;
import jodd.typeconverter.impl.MutableDoubleConverter;
import junit.framework.TestCase;

import java.math.BigDecimal;

public class MutableDoubleConverterTest extends TestCase {

    public void testConversion() {
        assertNull(MutableDoubleConverter.valueOf(null));

        assertEquals(new MutableDouble(1.73), MutableDoubleConverter.valueOf(new MutableDouble(1.73)));
        assertEquals(new MutableDouble(1), MutableDoubleConverter.valueOf(Integer.valueOf(1)));
        assertEquals(new MutableDouble(1.73), MutableDoubleConverter.valueOf(Double.valueOf(1.73D)));
        assertEquals(new MutableDouble(1.73), MutableDoubleConverter.valueOf("1.73"));
        assertEquals(new MutableDouble(1.73), MutableDoubleConverter.valueOf(new BigDecimal("1.73")));

        try {
            MutableDoubleConverter.valueOf("aaaa");
            fail();
        } catch (TypeConversionException ignore) {
        }
    }
}

