// Copyright (c) 2003-2011, Jodd Team (jodd.org). All Rights Reserved.

package jodd.typeconverter;

import jodd.datetime.JDateTime;
import jodd.typeconverter.impl.SqlTimeConverter;
import junit.framework.TestCase;

import java.sql.Time;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Date;

public class SqlTimeConverterTest extends TestCase {
	
	private static long time = new JDateTime(2011, 11, 1, 9, 10, 12, 567).getTimeInMillis();

	public void testNull() {
		assertNull(SqlTimeConverter.valueOf(null));
	}

	public void testCalendar2Timestamp() {
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(time);
		Time sqltime = SqlTimeConverter.valueOf(calendar);
		assertEquals(time, sqltime.getTime());
	}

	public void testDate2Timestamp() {
		Date date = new Date(time);
		Time sqltime = SqlTimeConverter.valueOf(date);
		assertEquals(time, sqltime.getTime());
	}

	public void testTimestamp2Timestamp() {
		Timestamp timestamp2 = new Timestamp(time);
		Time sqltime = SqlTimeConverter.valueOf(timestamp2);
		assertEquals(time, sqltime.getTime());
	}

	public void testSqlDate2Timestamp() {
		java.sql.Date date = new java.sql.Date(time);
		Time sqltime = SqlTimeConverter.valueOf(date);
		assertEquals(time, sqltime.getTime());
	}

	public void testSqlTime2Timestamp() {
		Time sqltime2 = new Time(time);
		Time sqltime = SqlTimeConverter.valueOf(sqltime2);
		assertEquals(time, sqltime.getTime());
	}

	public void testJDateTime2Timestamp() {
		JDateTime jdt = new JDateTime(time);
		Time sqltime = SqlTimeConverter.valueOf(jdt);
		assertEquals(time, sqltime.getTime());
	}

    public void testConversion() {
        assertNull(SqlTimeConverter.valueOf(null));

        assertEquals(Time.valueOf("00:01:02"), SqlTimeConverter.valueOf(Time.valueOf("00:01:02")));
        assertEquals(new Time(60), SqlTimeConverter.valueOf(Integer.valueOf(60)));
        assertEquals(Time.valueOf("00:01:02"), SqlTimeConverter.valueOf("00:01:02"));
        assertEquals(Time.valueOf("00:01:02"), SqlTimeConverter.valueOf("       00:01:02    "));

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
