// Copyright (c) 2003-2009, Jodd Team (jodd.org). All Rights Reserved.

package jodd.typeconverter;

/**
 * Converts given object to Float.
 */
public class FloatConverter implements TypeConverter<Float> {

	public static Float valueOf(Object value) {

		if (value == null) {
			return null;
		}

		if (value instanceof Float) {
			return (Float) value;
		}
		if (value instanceof Number) {
			return new Float(((Number)value).floatValue());
		}

		try {
			return (new Float(value.toString()));
		} catch (NumberFormatException nfex) {
			throw new TypeConversionException(value, nfex);
		}
	}

	public Float convert(Object value) {
		return valueOf(value);
	}

}
