// Copyright (c) 2003-2012, Jodd Team (jodd.org). All Rights Reserved.

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
	
	SqlDateConverter sqlDateConverter = new SqlDateConverter();

	public void testNull() {
		assertNull(sqlDateConverter.convert(null));
	}

	public void testCalendar2SqlDate() {
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(time);
		Date date = sqlDateConverter.convert(calendar);
		assertEquals(time, date.getTime());
	}

	public void testDate2SqlDate() {
		java.util.Date date2 = new java.util.Date(time);
		Date date = sqlDateConverter.convert(date2);
		assertEquals(time, date.getTime());
	}

	public void testTimestamp2SqlDate() {
		Timestamp timestamp = new Timestamp(time);
		Date date = sqlDateConverter.convert(timestamp);
		assertEquals(time, date.getTime());
	}

	public void testSqlDate2SqlDate() {
		Date date2 = new Date(time);
		Date date = sqlDateConverter.convert(date2);
		assertEquals(time, date.getTime());
	}

	public void testSqlTime2SqlDate() {
		Time sqltime = new Time(time);
		Date date = sqlDateConverter.convert(sqltime);
		assertEquals(time, date.getTime());
	}

	public void testJDateTime2SqlDate() {
		JDateTime jdt = new JDateTime(time);
		Date date = sqlDateConverter.convert(jdt);
		assertEquals(time, date.getTime());
	}
	
    public void testConversion() {
        assertNull(sqlDateConverter.convert(null));

        assertEquals(Date.valueOf("2011-01-01"), sqlDateConverter.convert(Date.valueOf("2011-01-01")));
        assertEquals(new Date(1111111), sqlDateConverter.convert(Integer.valueOf(1111111)));
        assertEquals(Date.valueOf("2011-01-01"), sqlDateConverter.convert("2011-01-01"));
        assertEquals(Date.valueOf("2011-01-01"), sqlDateConverter.convert("      2011-01-01       "));

        try {
            sqlDateConverter.convert("2011.01.01");
            fail();
        } catch (TypeConversionException ignore) {
        }

        try {
            sqlDateConverter.convert("a");
            fail();
        } catch (TypeConversionException ignore) {
        }
    }
}
