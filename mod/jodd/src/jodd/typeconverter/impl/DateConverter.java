// Copyright (c) 2003-2012, Jodd Team (jodd.org). All Rights Reserved.

package jodd.typeconverter.impl;

import jodd.datetime.JDateTime;
import jodd.typeconverter.TypeConversionException;
import jodd.typeconverter.TypeConverter;
import jodd.util.StringUtil;

import java.util.Calendar;
import java.util.Date;

/**
 * Converts given object to <code>java.util.Date</code>.
 */
public class DateConverter implements TypeConverter<Date> {

	public Date convert(Object value) {
		if (value == null) {
			return null;
		}

		if (value instanceof Date) {
			return (Date) value;
		}
		if (value instanceof Calendar) {
			return new Date(((Calendar)value).getTimeInMillis());
		}
		if (value instanceof JDateTime) {
			return ((JDateTime) value).convertToDate();
		}
		if (value instanceof Number) {
			return new Date(((Number) value).longValue());
		}

		String stringValue = value.toString().trim();

		if (StringUtil.containsOnlyDigits(stringValue) == false) {
			// try to parse default string format
			JDateTime jdt = new JDateTime(stringValue, JDateTime.DEFAULT_FORMAT);
			return jdt.convertToDate();
		}

		try {
			long milliseconds = Long.parseLong(stringValue);
			return new Date(milliseconds);
		} catch (NumberFormatException nfex) {
			throw new TypeConversionException(value, nfex);
		}
	}

}
