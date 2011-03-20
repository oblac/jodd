// Copyright (c) 2003-2011, Jodd Team (jodd.org). All Rights Reserved.

package jodd.typeconverter.impl;

import jodd.typeconverter.TypeConversionException;
import jodd.typeconverter.TypeConverter;

/**
 * Converts given object to <code>Double</code>.
 */
public class DoubleConverter implements TypeConverter<Double> {

	public static Double valueOf(Object value) {
		if (value == null) {
			return null;
		}

		if (value.getClass() == Double.class) {
			return (Double) value;
		}
		if (value instanceof Number) {
			return Double.valueOf(((Number)value).doubleValue());
		}

		try {
			return Double.valueOf(value.toString().trim());
		} catch (NumberFormatException nfex) {
			throw new TypeConversionException(value, nfex);
		}
	}

	public Double convert(Object value) {
		return valueOf(value);
	}
	
}
