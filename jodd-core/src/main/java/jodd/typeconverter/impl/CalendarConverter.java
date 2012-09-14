// Copyright (c) 2003-2012, Jodd Team (jodd.org). All Rights Reserved.

package jodd.typeconverter.impl;

import jodd.datetime.JDateTime;
import jodd.typeconverter.TypeConversionException;
import jodd.typeconverter.TypeConverter;
import jodd.util.StringUtil;

import java.util.Calendar;
import java.util.Date;

/**
 * Converts given object to <code>Byte</code>.
 * Conversion rules:
 * <li><code>null</code> value is returned as <code>null</code>
 * <li>object of destination type is simply casted
 * <li><code>Date</code> object is converted
 * <li><code>JDateTime</code> object is converted
 * <li><code>Number</code> is used as number of milliseconds
 * <li>finally, if string value contains only numbers it is parsed as milliseconds;
 * otherwise as JDateTime pattern
 */
public class CalendarConverter implements TypeConverter<Calendar> {

	public Calendar convert(Object value) {
		if (value == null) {
			return null;
		}

		if (value instanceof Calendar) {
			return (Calendar) value;
		}
		if (value instanceof Date) {
			Calendar calendar = Calendar.getInstance();
			calendar.setTime((Date) value);
			return calendar;
		}
		if (value instanceof JDateTime) {
			return ((JDateTime)value).convertToCalendar();
		}

		if (value instanceof Number) {
			Calendar calendar = Calendar.getInstance();
			calendar.setTimeInMillis(((Number) value).longValue());
			return calendar;
		}

		String stringValue = value.toString().trim();

		if (StringUtil.containsOnlyDigits(stringValue) == false) {
			// try to parse default string format
			JDateTime jdt = new JDateTime(stringValue, JDateTime.DEFAULT_FORMAT);
			return jdt.convertToCalendar();
		}

		try {
			long milliseconds = Long.parseLong(stringValue);
			Calendar calendar = Calendar.getInstance();
			calendar.setTimeInMillis(milliseconds);
			return calendar;
		} catch (NumberFormatException nfex) {
			throw new TypeConversionException(value, nfex);
		}
	}

}
