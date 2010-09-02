// Copyright (c) 2003-2010, Jodd Team (jodd.org). All Rights Reserved.

package jodd.typeconverter;

/**
 * Converts given object to a Long.
 */
public class LongConverter implements TypeConverter<Long> {

	public static Long valueOf(Object value) {
		
		if (value == null) {
			return null;
		}
		if (value instanceof Long) {
			return (Long) value;
		}
		if (value instanceof Number) {
			return new Long(((Number)value).longValue());
		}
		try {
			return (new Long(value.toString()));
		} catch (NumberFormatException nfex) {
			throw new TypeConversionException(value, nfex);
		}
	}

	public Long convert(Object value) {
		return valueOf(value);
	}
	
}
