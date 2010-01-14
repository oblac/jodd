// Copyright (c) 2003-2010, Jodd Team (jodd.org). All Rights Reserved.

package	jodd.datetime.converter;

import java.util.GregorianCalendar;

import jodd.datetime.JDateTime;

public class GregorianCalendarConverter implements JdtConverter<GregorianCalendar> {

	public void loadFrom(JDateTime jdt, GregorianCalendar source) {
		jdt.setTimeZone(source.getTimeZone());
		jdt.setTimeInMillis(source.getTimeInMillis());
	}

	public GregorianCalendar convertTo(JDateTime jdt) {
		GregorianCalendar gc = new GregorianCalendar(jdt.getTimeZone());
		gc.setTimeInMillis(jdt.getTimeInMillis());
		return gc;
	}

	public void storeTo(JDateTime jdt, GregorianCalendar destination) {
		destination.setTimeZone(jdt.getTimeZone());
		destination.setTimeInMillis(jdt.getTimeInMillis());
	}
}
