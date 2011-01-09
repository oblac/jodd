// Copyright (c) 2003-2011, Jodd Team (jodd.org). All Rights Reserved.

package jodd.typeconverter.impl;

import jodd.typeconverter.TypeConversionException;
import jodd.typeconverter.TypeConverter;

import java.sql.Date;


/**
 * Converts given object to java.sql.Date.
 */
public class SqlDateConverter implements TypeConverter<Date> {

	public static Date valueOf(Object value) {
		if (value == null) {
			return null;
		}
	
		if (value instanceof Date) {
			return (Date) value;
		}
		if (value instanceof Number) {
			return new Date(((Number) value).longValue());
		}
	
		try {
			return (Date.valueOf(value.toString()));
		} catch (IllegalArgumentException iaex) {
			throw new TypeConversionException(value, iaex);
		}
	}

	public Date convert(Object value) {
		return valueOf(value);
	}
	
}
