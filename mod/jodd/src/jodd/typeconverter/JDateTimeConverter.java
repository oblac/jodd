// Copyright (c) 2003-2009, Jodd Team (jodd.org). All Rights Reserved.

package jodd.typeconverter;

import jodd.datetime.JDateTime;
import jodd.datetime.DateTimeStamp;
import jodd.datetime.JulianDateStamp;

/**
 * Converts object to {@link JDateTime}.
 */
public class JDateTimeConverter implements TypeConverter<JDateTime> {

	public static JDateTime valueOf(Object value) {

		if (value == null) {
			return null;
		}

		if (value instanceof JDateTime) {
			return (JDateTime) value;
		}
		if (value instanceof Number) {
			return new JDateTime(((Number) value).longValue());
		}
		if (value instanceof DateTimeStamp) {
			return new JDateTime((DateTimeStamp) value);
		}
		if (value instanceof JulianDateStamp) {
			return new JDateTime((JulianDateStamp) value);
		}

		try {
			return new JDateTime(value);
		} catch (IllegalArgumentException iaex) {
			throw new TypeConversionException(value, iaex);
		}
	}

	public JDateTime convert(Object value) {
		return valueOf(value);
	}
}
