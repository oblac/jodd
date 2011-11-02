// Copyright (c) 2003-2011, Jodd Team (jodd.org). All Rights Reserved.

package jodd.datetime.converter;

import jodd.datetime.JDateTime;

import java.sql.Time;

public class SqlTimeConverter implements JdtConverter<Time> {

	public void loadFrom(JDateTime jdt, Time source) {
		jdt.setTimeInMillis(source.getTime());
	}

	public Time convertTo(JDateTime jdt) {
		return new Time(jdt.getTimeInMillis());
	}

	public void storeTo(JDateTime jdt, Time destination) {
		destination.setTime(jdt.getTimeInMillis());
	}

}
