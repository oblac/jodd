// Copyright (c) 2003-2009, Jodd Team (jodd.org). All Rights Reserved.

package jodd.datetime.converter;

import jodd.datetime.JDateTime;
import jodd.datetime.DateTimeStamp;

public class DateTimeStampConverter implements JdtConverter<DateTimeStamp> {

	public void loadFrom(JDateTime jdt, DateTimeStamp source) {
		jdt.setDateTimeStamp(source);
	}

	public DateTimeStamp convertTo(JDateTime jdt) {
		DateTimeStamp dts = new DateTimeStamp();
		storeTo(jdt, dts);
		return dts;
	}

	public void storeTo(JDateTime jdt, DateTimeStamp destination) {
		DateTimeStamp time = jdt.getDateTimeStamp();
		destination.year = time.year;
		destination.month = time.month;
		destination.day = time.day;
		destination.hour = time.hour;
		destination.minute = time.minute;
		destination.second = time.second;
	}
}
