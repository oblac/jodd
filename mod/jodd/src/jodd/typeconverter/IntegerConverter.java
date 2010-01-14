// Copyright (c) 2003-2010, Jodd Team (jodd.org). All Rights Reserved.

package jodd.typeconverter;

/**
 * Converts given object to an Integer.
 */
public class IntegerConverter implements TypeConverter<Integer> {

	public static Integer valueOf(Object value) {

		if (value == null) {
			return null;
		}

		if (value instanceof Integer) {
			return (Integer) value;
		}
		if (value instanceof Number) {
			return new Integer(((Number)value).intValue());
		}
		try {
			return new Integer(value.toString());
		} catch (NumberFormatException nfex) {
			throw new TypeConversionException(value, nfex);
		}
	}

	public Integer convert(Object value) {
		return valueOf(value);
	}
	
}
