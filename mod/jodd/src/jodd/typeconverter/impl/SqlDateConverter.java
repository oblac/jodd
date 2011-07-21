// Copyright (c) 2003-2011, Jodd Team (jodd.org). All Rights Reserved.

package jodd.typeconverter.impl;

import jodd.typeconverter.TypeConversionException;
import jodd.typeconverter.TypeConverter;

import java.sql.Date;


/**
 * Converts given object to <code>java.sql.Date</code>.
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

		String stringValue = value.toString().trim();

		// try yyyy-mm-dd for valueOf
		if (stringValue.indexOf('-') != -1) {
			try {
				return (Date.valueOf(stringValue));
			} catch (IllegalArgumentException iaex) {
				throw new TypeConversionException(value, iaex);
			}
		}

		// assume string to be a number
		try {
			long milliseconds = Long.parseLong(stringValue);
			return new Date(milliseconds);
		} catch (NumberFormatException nfex) {
			throw new TypeConversionException(value, nfex);
		}
	}

	public Date convert(Object value) {
		return valueOf(value);
	}
	
}
