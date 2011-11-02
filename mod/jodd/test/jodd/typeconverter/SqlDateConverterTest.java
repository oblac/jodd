// Copyright (c) 2003-2011, Jodd Team (jodd.org). All Rights Reserved.

package jodd.typeconverter;

import jodd.datetime.JDateTime;
import jodd.typeconverter.impl.SqlDateConverter;
import junit.framework.TestCase;

import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.Calendar;

public class SqlDateConverterTest extends TestCase {

	private static long time = new JDateTime(2011, 11, 1, 9, 10, 12, 567).getTimeInMillis();

	public void testNull() {
		assertNull(SqlDateConverter.valueOf(null));
	}

	public void testCalendar2SqlDate() {
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(time);
		Date date = SqlDateConverter.valueOf(calendar);
		assertEquals(time, date.getTime());
	}

	public void testDate2SqlDate() {
		java.util.Date date2 = new java.util.Date(time);
		Date date = SqlDateConverter.valueOf(date2);
		assertEquals(time, date.getTime());
	}

	public void testTimestamp2SqlDate() {
		Timestamp timestamp = new Timestamp(time);
		Date date = SqlDateConverter.valueOf(timestamp);
		assertEquals(time, date.getTime());
	}

	public void testSqlDate2SqlDate() {
		Date date2 = new Date(time);
		Date date = SqlDateConverter.valueOf(date2);
		assertEquals(time, date.getTime());
	}

	public void testSqlTime2SqlDate() {
		Time sqltime = new Time(time);
		Date date = SqlDateConverter.valueOf(sqltime);
		assertEquals(time, date.getTime());
	}

	public void testJDateTime2SqlDate() {
		JDateTime jdt = new JDateTime(time);
		Date date = SqlDateConverter.valueOf(jdt);
		assertEquals(time, date.getTime());
	}
	
    public void testConversion() {
        assertNull(SqlDateConverter.valueOf(null));

        assertEquals(Date.valueOf("2011-01-01"), SqlDateConverter.valueOf(Date.valueOf("2011-01-01")));
        assertEquals(new Date(1111111), SqlDateConverter.valueOf(Integer.valueOf(1111111)));
        assertEquals(Date.valueOf("2011-01-01"), SqlDateConverter.valueOf("2011-01-01"));
        assertEquals(Date.valueOf("2011-01-01"), SqlDateConverter.valueOf("      2011-01-01       "));

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
