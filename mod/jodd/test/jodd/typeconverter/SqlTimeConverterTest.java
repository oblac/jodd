// Copyright (c) 2003-2010, Jodd Team (jodd.org). All Rights Reserved.

package jodd.typeconverter;

import jodd.typeconverter.impl.SqlTimeConverter;
import junit.framework.TestCase;

import java.sql.Time;

public class SqlTimeConverterTest extends TestCase {

    public void testConversion() {
        assertNull(SqlTimeConverter.valueOf(null));

        assertEquals(Time.valueOf("00:01:02"), SqlTimeConverter.valueOf(Time.valueOf("00:01:02")));
        assertEquals(new Time(60), SqlTimeConverter.valueOf(Integer.valueOf(60)));
        assertEquals(Time.valueOf("00:01:02"), SqlTimeConverter.valueOf("00:01:02"));

        try {
            SqlTimeConverter.valueOf("00:01");
            fail();
        } catch (TypeConversionException ignore) {
        }

        try {
            SqlTimeConverter.valueOf("a");
            fail();
        } catch (TypeConversionException ignore) {
        }
    }
}
