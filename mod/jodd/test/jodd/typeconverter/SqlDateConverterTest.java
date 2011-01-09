// Copyright (c) 2003-2011, Jodd Team (jodd.org). All Rights Reserved.

package jodd.typeconverter;

import jodd.typeconverter.impl.SqlDateConverter;
import junit.framework.TestCase;

import java.sql.Date;

public class SqlDateConverterTest extends TestCase {

    public void testConversion() {
        assertNull(SqlDateConverter.valueOf(null));

        assertEquals(Date.valueOf("2011-01-01"), SqlDateConverter.valueOf(Date.valueOf("2011-01-01")));
        assertEquals(new Date(1111111), SqlDateConverter.valueOf(Integer.valueOf(1111111)));
        assertEquals(Date.valueOf("2011-01-01"), SqlDateConverter.valueOf("2011-01-01"));

        try {
            SqlDateConverter.valueOf("2011.01.01");
            fail();
        } catch (TypeConversionException ignore) {
        }

        try {
            SqlDateConverter.valueOf("a");
            fail();
        } catch (TypeConversionException ignore) {
        }
    }
}
