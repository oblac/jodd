// Copyright (c) 2003-2011, Jodd Team (jodd.org). All Rights Reserved.

package jodd.typeconverter.impl;

import jodd.typeconverter.TypeConversionException;
import jodd.typeconverter.TypeConverter;

import java.sql.Timestamp;

/**
 * Converts given object to <code>java.sql.Timestamp</code>.
 */
public class SqlTimestampConverter implements TypeConverter<Timestamp> {

	public static Timestamp valueOf(Object value) {
		if (value == null) {
			return null;
		}
		if (value instanceof Timestamp) {
			return (Timestamp) value;
		}
		if (value instanceof Number) {
			return new Timestamp(((Number)value).longValue()); 
		}
		try {
			return (Timestamp.valueOf(value.toString().trim()));
		} catch (IllegalArgumentException iaex) {
			throw new TypeConversionException(value, iaex);
		}
	}

	public Timestamp convert(Object value) {
		return valueOf(value);
	}
	

}
