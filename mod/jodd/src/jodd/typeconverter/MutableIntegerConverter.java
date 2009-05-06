// Copyright (c) 2003-2009, Jodd Team (jodd.org). All Rights Reserved.

package jodd.typeconverter;

import jodd.mutable.MutableInteger;

/**
 * Converts given object to an {@link MutableInteger}.
 */
public class MutableIntegerConverter implements TypeConverter<MutableInteger> {

	public static MutableInteger valueOf(Object value) {

		if (value == null) {
			return null;
		}
		if (value instanceof MutableInteger) {
			return (MutableInteger) value;
		}
		if (value instanceof Number) {
			return new MutableInteger(((Number)value).intValue());
		}
		try {
			return new MutableInteger(value.toString());
		} catch (NumberFormatException nfex) {
			throw new TypeConversionException(value, nfex);
		}
	}

	public MutableInteger convert(Object value) {
		return valueOf(value);
	}
	
}
