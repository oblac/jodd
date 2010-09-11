// Copyright (c) 2003-2010, Jodd Team (jodd.org). All Rights Reserved.

package jodd.typeconverter;

/**
 * Converts given object to Short. Given object (if not already instance of
 * Short) is first converted to String and then analyzed.
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
			return Short.valueOf(value.toString());
		} catch (NumberFormatException nfex) {
			throw new TypeConversionException(value, nfex);
		}
	}

	public Short convert(Object value) {
		return valueOf(value);
	}
}
