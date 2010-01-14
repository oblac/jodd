// Copyright (c) 2003-2010, Jodd Team (jodd.org). All Rights Reserved.

package jodd.typeconverter;

/**
 * Converts given object to Double.
 */
public class DoubleConverter implements TypeConverter<Double> {

	public static Double valueOf(Object value) {

		if (value == null) {
			return null;
		}
		if (value instanceof Double) {
			return (Double) value;
		}
		if (value instanceof Number) {
			return new Double(((Number)value).doubleValue());
		}

		try {
			return (new Double(value.toString()));
		} catch (NumberFormatException nfex) {
			throw new TypeConversionException(value, nfex);
		}
	}

	public Double convert(Object value) {
		return valueOf(value);
	}
	
}
