// Copyright (c) 2003-2010, Jodd Team (jodd.org). All Rights Reserved.

package jodd.typeconverter.impl;

import jodd.typeconverter.TypeConversionException;
import jodd.typeconverter.TypeConverter;

/**
 * Converts given object to Float.
 */
public class FloatConverter implements TypeConverter<Float> {

	public static Float valueOf(Object value) {
		if (value == null) {
			return null;
		}

		if (value.getClass() == Float.class) {
			return (Float) value;
		}
		if (value instanceof Number) {
			return Float.valueOf(((Number)value).floatValue());
		}

		try {
			return Float.valueOf(value.toString());
		} catch (NumberFormatException nfex) {
			throw new TypeConversionException(value, nfex);
		}
	}

	public Float convert(Object value) {
		return valueOf(value);
	}

}
