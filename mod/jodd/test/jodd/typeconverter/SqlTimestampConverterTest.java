// Copyright (c) 2003-2011, Jodd Team (jodd.org). All Rights Reserved.

package jodd.typeconverter;

import jodd.typeconverter.impl.SqlTimestampConverter;
import junit.framework.TestCase;

import java.sql.Timestamp;

public class SqlTimestampConverterTest extends TestCase {

    public void testConversion() {
        assertNull(SqlTimestampConverter.valueOf(null));

        assertEquals(Timestamp.valueOf("2011-01-01 00:01:02"), SqlTimestampConverter.valueOf(Timestamp.valueOf("2011-01-01 00:01:02")));
        assertEquals(new Timestamp(60), SqlTimestampConverter.valueOf(Integer.valueOf(60)));
        assertEquals(Timestamp.valueOf("2011-01-01 00:01:02"), SqlTimestampConverter.valueOf("2011-01-01 00:01:02"));

        try {
            SqlTimestampConverter.valueOf("00:01");
            fail();
        } catch (TypeConversionException ignore) {
        }

        try {
            SqlTimestampConverter.valueOf("a");
            fail();
        } catch (TypeConversionException ignore) {
        }
    }
}
