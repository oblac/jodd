// Copyright (c) 2003-2009, Jodd Team (jodd.org). All Rights Reserved.

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
		if (value instanceof Short) {
			return (Short) value;
		}
		if (value instanceof Number) {
			return new Short(((Number)value).shortValue());
		}
		try {
			return (new Short(value.toString()));
		} catch (NumberFormatException nfex) {
			throw new TypeConversionException(value, nfex);
		}
	}

	public Short convert(Object value) {
		return valueOf(value);
	}
}
