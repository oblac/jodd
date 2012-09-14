// Copyright (c) 2003-2012, Jodd Team (jodd.org). All Rights Reserved.

package jodd.typeconverter.impl;

import jodd.datetime.JDateTime;
import jodd.typeconverter.TypeConversionException;
import jodd.typeconverter.TypeConverter;
import jodd.util.StringUtil;

import java.sql.Date;
import java.util.Calendar;


/**
 * Converts given object to <code>java.sql.Date</code>.
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
public class SqlDateConverter implements TypeConverter<Date> {

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
		if (value instanceof java.util.Date) {
			return new Date(((java.util.Date)value).getTime());
		}
		if (value instanceof JDateTime) {
			return ((JDateTime) value).convertToSqlDate();
		}

		if (value instanceof Number) {
			return new Date(((Number) value).longValue());
		}

		String stringValue = value.toString().trim();

		// try yyyy-mm-dd for valueOf
		if (StringUtil.containsOnlyDigits(stringValue) == false) {
			try {
				return Date.valueOf(stringValue);
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

}
