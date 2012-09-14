// Copyright (c) 2003-2012, Jodd Team (jodd.org). All Rights Reserved.

package jodd.typeconverter.impl;

import jodd.datetime.JDateTime;
import jodd.typeconverter.TypeConversionException;
import jodd.typeconverter.TypeConverter;
import jodd.util.StringUtil;

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Date;

/**
 * Converts given object to <code>java.sql.Timestamp</code>.
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
public class SqlTimestampConverter implements TypeConverter<Timestamp> {

	public Timestamp convert(Object value) {
		if (value == null) {
			return null;
		}
		if (value instanceof Timestamp) {
			return (Timestamp) value;
		}
		if (value instanceof Calendar) {
			Calendar calendar = (Calendar) value;
			return new Timestamp(calendar.getTimeInMillis());
		}
		if (value instanceof Date) {
			Date date = (Date) value;
			return new Timestamp(date.getTime());
		}
		if (value instanceof JDateTime) {
			return ((JDateTime) value).convertToSqlTimestamp();
		}

		if (value instanceof Number) {
			return new Timestamp(((Number)value).longValue()); 
		}

		String stringValue = value.toString().trim();

		// try yyyy-mm-dd for valueOf
		if (StringUtil.containsOnlyDigits(stringValue) == false) {
			try {
				return Timestamp.valueOf(stringValue);
			} catch (IllegalArgumentException iaex) {
				throw new TypeConversionException(value, iaex);
			}
		}

		// assume string to be a number
		try {
			long milliseconds = Long.parseLong(stringValue);
			return new Timestamp(milliseconds);
		} catch (NumberFormatException nfex) {
			throw new TypeConversionException(value, nfex);
		}
	}

}
