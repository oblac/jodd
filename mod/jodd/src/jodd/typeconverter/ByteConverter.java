// Copyright (c) 2003-2010, Jodd Team (jodd.org). All Rights Reserved.

package jodd.typeconverter;

/**
 * Converts given object to Byte. Given object (if not already instance of
 * Byte) is first converted to String and then analyzed.
 */
public class ByteConverter implements TypeConverter<Byte> {

	public static Byte valueOf(Object value) {
		if (value == null) {
			return null;
		}
		if (value instanceof Byte) {
			return (Byte) value;
		}
		if (value instanceof Number) {
			return new Byte(((Number)value).byteValue());
		}
		try {
			return (new Byte(value.toString()));
		} catch (NumberFormatException nfex) {
			throw new TypeConversionException(value, nfex);
		}
	}

	public Byte convert(Object value) {
		return valueOf(value);
	}
}
