// Copyright (c) 2003-2009, Jodd Team (jodd.org). All Rights Reserved.

package jodd.typeconverter;

import jodd.mutable.MutableFloat;

/**
 * Converts given object to {@link MutableFloat}.
 */
public class MutableFloatConverter implements TypeConverter<MutableFloat> {

	public static MutableFloat valueOf(Object value) {

		if (value == null) {
			return null;
		}
		if (value instanceof MutableFloat) {
			return (MutableFloat) value;
		}
		if (value instanceof Number) {
			return new MutableFloat(((Number)value).floatValue());
		}
		try {
			return new MutableFloat(value.toString());
		} catch (Exception ex) {
			throw new TypeConversionException(value, ex);
		}
	}

	public MutableFloat convert(Object value) {
		return valueOf(value);
	}
	
}
