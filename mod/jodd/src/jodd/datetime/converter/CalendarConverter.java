// Copyright (c) 2003-2011, Jodd Team (jodd.org). All Rights Reserved.

package	jodd.datetime.converter;

import java.util.Calendar;

import jodd.datetime.JDateTime;

public class CalendarConverter implements JdtConverter<Calendar> {

	public void loadFrom(JDateTime jdt, Calendar source) {
		jdt.setTimeInMillis(source.getTimeInMillis());
		jdt.setTimeZone(source.getTimeZone());
	}

	public Calendar convertTo(JDateTime jdt) {
		Calendar calendar = Calendar.getInstance(jdt.getTimeZone());
		calendar.setTimeInMillis(jdt.getTimeInMillis());
		return calendar;
	}

	public void storeTo(JDateTime jdt, Calendar destination) {
		destination.setTimeZone(jdt.getTimeZone());
		destination.setTimeInMillis(jdt.getTimeInMillis());
	}

}
