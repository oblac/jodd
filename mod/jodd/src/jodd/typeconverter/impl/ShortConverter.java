// Copyright (c) 2003-2011, Jodd Team (jodd.org). All Rights Reserved.

package jodd.typeconverter.impl;

import jodd.typeconverter.TypeConversionException;
import jodd.typeconverter.TypeConverter;

/**
 * Converts given object to <code>Short</code>.
 */
public class ShortConverter implements TypeConverter<Short> {

	public static Short valueOf(Object value) {
		if (value == null) {
			return null;
		}

		if (value.getClass() == Short.class) {
			return (Short) value;
		}
		if (value instanceof Number) {
			return Short.valueOf(((Number)value).shortValue());
		}
		try {
			return Short.valueOf(value.toString().trim());
		} catch (NumberFormatException nfex) {
			throw new TypeConversionException(value, nfex);
		}
	}

	public Short convert(Object value) {
		return valueOf(value);
	}
}
