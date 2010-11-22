// Copyright (c) 2003-2010, Jodd Team (jodd.org). All Rights Reserved.

package jodd.typeconverter.impl;

import jodd.typeconverter.TypeConversionException;
import jodd.typeconverter.TypeConverter;

import java.sql.Time;

/**
 * Converts given object to java.sql.Time.
 */
public class SqlTimeConverter implements TypeConverter<Time> {

	public static Time valueOf(Object value) {
		if (value == null) {
			return null;
		}
		
		if (value instanceof Time) {
			return (Time) value;
		}
		if (value instanceof Number) {
			return new Time(((Number) value).longValue());
		}

		try {
			return (Time.valueOf(value.toString()));
		} catch (IllegalArgumentException iaex) {
			throw new TypeConversionException(value, iaex);
		}
	}

	public Time convert(Object value) {
		return valueOf(value);
	}
}
