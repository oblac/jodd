// Copyright (c) 2003-2013, Jodd Team (jodd.org). All Rights Reserved.

package jodd.typeconverter.impl;

import jodd.typeconverter.TypeConverter;

import java.util.TimeZone;

/**
 * Converts given object to Java <code>TimeZone</code>.
 * <ul>
 * <li><code>null</code> value is returned as <code>null</code></li>
 * <li>object of destination type is simply casted</li>
 * <li>finally, string representation of the object is used for getting the time zone</li>
 * </ul>
 */
public class TimeZoneConverter implements TypeConverter<TimeZone> {

	public TimeZone convert(Object value) {
		if (value == null) {
			return null;
		}

		if (value.getClass() == TimeZone.class) {
			return (TimeZone) value;
		}

		return TimeZone.getTimeZone(value.toString());

	}
}
