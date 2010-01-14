// Copyright (c) 2003-2010, Jodd Team (jodd.org). All Rights Reserved.

package	jodd.datetime.converter;

import java.sql.Date;

import jodd.datetime.JDateTime;

public class SqlDateConverter implements JdtConverter<Date> {

	public void loadFrom(JDateTime jdt, Date source) {
		jdt.setTimeInMillis(source.getTime());
	}

	public Date convertTo(JDateTime jdt) {
		return new Date(jdt.getTimeInMillis());
	}

	public void storeTo(JDateTime jdt, Date destination) {
		destination.setTime(jdt.getTimeInMillis());
	}

}
