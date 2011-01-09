// Copyright (c) 2003-2011, Jodd Team (jodd.org). All Rights Reserved.

package	jodd.datetime.converter;

import java.sql.Timestamp;

import jodd.datetime.JDateTime;

public class SqlTimestampConverter implements JdtConverter<Timestamp> {

	public void loadFrom(JDateTime jdt, Timestamp source) {
		jdt.setTimeInMillis(source.getTime());
	}

	public Timestamp convertTo(JDateTime jdt) {
		return new Timestamp(jdt.getTimeInMillis());
	}

	public void storeTo(JDateTime jdt, Timestamp destination) {
		destination.setNanos(0);		
		destination.setTime(jdt.getTimeInMillis());
	}
}
