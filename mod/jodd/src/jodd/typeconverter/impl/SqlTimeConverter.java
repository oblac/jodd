// Copyright (c) 2003-2012, Jodd Team (jodd.org). All Rights Reserved.

package jodd.typeconverter.impl;

import jodd.datetime.JDateTime;
import jodd.typeconverter.TypeConversionException;
import jodd.typeconverter.TypeConverter;
import jodd.util.StringUtil;

import java.sql.Time;
import java.util.Calendar;
import java.util.Date;

/**
 * Converts given object to <code>java.sql.Time</code>.
 * Conversion rules:
 * <li><code>null</code> value is returned as <code>null</code>
 * <li>object of destination type is simply casted
 * <li><code>Calendar</code> object is converted
 * <li><code>Date</code> object is converted
 * <li><code>JDateTime</code> object is converted
 * <li><code>Number</code> is used as number of milliseconds
 * <li>finally, if string value contains only numbers it is parsed as milliseconds;
 * otherwise as JDateTime pattern
 */
public class SqlTimeConverter implements TypeConverter<Time> {

	public Time convert(Object value) {
		if (value == null) {
			return null;
		}
		
		if (value instanceof Time) {
			return (Time) value;
		}
		if (value instanceof Calendar) {
			return new Time(((Calendar) value).getTimeInMillis());
		}
		if (value instanceof Date) {
			return new Time(((Date)value).getTime());
		}
		if (value instanceof JDateTime) {
			return ((JDateTime) value).convertToSqlTime();
		}
		if (value instanceof Number) {
			return new Time(((Number) value).longValue());
		}

		String stringValue = value.toString().trim();

		// try yyyy-mm-dd for valueOf
		if (StringUtil.containsOnlyDigits(stringValue) == false) {
			try {
				return Time.valueOf(stringValue);
			} catch (IllegalArgumentException iaex) {
				throw new TypeConversionException(value, iaex);
			}
		}

		// assume string to be a number
		try {
			long milliseconds = Long.parseLong(stringValue);
			return new Time(milliseconds);
		} catch (NumberFormatException nfex) {
			throw new TypeConversionException(value, nfex);
		}
	}

}
