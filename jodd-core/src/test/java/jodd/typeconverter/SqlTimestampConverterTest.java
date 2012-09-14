// Copyright (c) 2003-2012, Jodd Team (jodd.org). All Rights Reserved.

package jodd.typeconverter;

import jodd.datetime.JDateTime;
import jodd.typeconverter.impl.SqlTimestampConverter;
import junit.framework.TestCase;

import java.sql.Time;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Date;

public class SqlTimestampConverterTest extends TestCase {

	private static long time = new JDateTime(2011, 11, 1, 9, 10, 12, 567).getTimeInMillis();
	
	SqlTimestampConverter sqlTimestampConverter = new SqlTimestampConverter();

	public void testNull() {
		assertNull(sqlTimestampConverter.convert(null));
	}

	public void testCalendar2Timestamp() {
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(time);
		Timestamp timestamp = sqlTimestampConverter.convert(calendar);
		assertEquals(time, timestamp.getTime());
	}

	public void testDate2Timestamp() {
		Date date = new Date(time);
		Timestamp timestamp = sqlTimestampConverter.convert(date);
		assertEquals(time, timestamp.getTime());
	}

	public void testTimestamp2Timestamp() {
		Timestamp timestamp2 = new Timestamp(time);
		Timestamp timestamp = sqlTimestampConverter.convert(timestamp2);
		assertEquals(time, timestamp.getTime());
	}

	public void testSqlDate2Timestamp() {
		java.sql.Date date = new java.sql.Date(time);
		Timestamp timestamp = sqlTimestampConverter.convert(date);
		assertEquals(time, timestamp.getTime());
	}

	public void testSqlTime2Timestamp() {
		Time sqltime = new Time(time);
		Timestamp timestamp = sqlTimestampConverter.convert(sqltime);
		assertEquals(time, timestamp.getTime());
	}

	public void testJDateTime2Timestamp() {
		JDateTime jdt = new JDateTime(time);
		Timestamp timestamp = sqlTimestampConverter.convert(jdt);
		assertEquals(time, timestamp.getTime());
	}

    public void testConversion() {
        assertNull(sqlTimestampConverter.convert(null));

        assertEquals(Timestamp.valueOf("2011-01-01 00:01:02"), sqlTimestampConverter.convert(Timestamp.valueOf("2011-01-01 00:01:02")));
        assertEquals(new Timestamp(60), sqlTimestampConverter.convert(Integer.valueOf(60)));
        assertEquals(Timestamp.valueOf("2011-01-01 00:01:02"), sqlTimestampConverter.convert("2011-01-01 00:01:02"));
        assertEquals(Timestamp.valueOf("2011-01-01 00:01:02"), sqlTimestampConverter.convert("     2011-01-01 00:01:02       "));

        try {
            sqlTimestampConverter.convert("00:01");
            fail();
        } catch (TypeConversionException ignore) {
        }

        try {
            sqlTimestampConverter.convert("a");
            fail();
        } catch (TypeConversionException ignore) {
        }
    }
}
