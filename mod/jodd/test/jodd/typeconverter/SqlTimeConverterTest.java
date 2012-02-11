// Copyright (c) 2003-2012, Jodd Team (jodd.org). All Rights Reserved.

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
	
	SqlTimeConverter sqlTimeConverter = new SqlTimeConverter();

	public void testNull() {
		assertNull(sqlTimeConverter.convert(null));
	}

	public void testCalendar2Timestamp() {
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(time);
		Time sqltime = sqlTimeConverter.convert(calendar);
		assertEquals(time, sqltime.getTime());
	}

	public void testDate2Timestamp() {
		Date date = new Date(time);
		Time sqltime = sqlTimeConverter.convert(date);
		assertEquals(time, sqltime.getTime());
	}

	public void testTimestamp2Timestamp() {
		Timestamp timestamp2 = new Timestamp(time);
		Time sqltime = sqlTimeConverter.convert(timestamp2);
		assertEquals(time, sqltime.getTime());
	}

	public void testSqlDate2Timestamp() {
		java.sql.Date date = new java.sql.Date(time);
		Time sqltime = sqlTimeConverter.convert(date);
		assertEquals(time, sqltime.getTime());
	}

	public void testSqlTime2Timestamp() {
		Time sqltime2 = new Time(time);
		Time sqltime = sqlTimeConverter.convert(sqltime2);
		assertEquals(time, sqltime.getTime());
	}

	public void testJDateTime2Timestamp() {
		JDateTime jdt = new JDateTime(time);
		Time sqltime = sqlTimeConverter.convert(jdt);
		assertEquals(time, sqltime.getTime());
	}

    public void testConversion() {
        assertNull(sqlTimeConverter.convert(null));

        assertEquals(Time.valueOf("00:01:02"), sqlTimeConverter.convert(Time.valueOf("00:01:02")));
        assertEquals(new Time(60), sqlTimeConverter.convert(Integer.valueOf(60)));
        assertEquals(Time.valueOf("00:01:02"), sqlTimeConverter.convert("00:01:02"));
        assertEquals(Time.valueOf("00:01:02"), sqlTimeConverter.convert("       00:01:02    "));

        try {
            sqlTimeConverter.convert("00:01");
            fail();
        } catch (TypeConversionException ignore) {
        }

        try {
            sqlTimeConverter.convert("a");
            fail();
        } catch (TypeConversionException ignore) {
        }
    }
}
