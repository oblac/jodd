// Copyright (c) 2003-2010, Jodd Team (jodd.org). All Rights Reserved.

package jodd.typeconverter;

import jodd.mutable.MutableLong;

/**
 * Converts given object to a {@link MutableLong}.
 */
public class MutableLongConverter implements TypeConverter<MutableLong> {

	public static MutableLong valueOf(Object value) {

		if (value == null) {
			return null;
		}
		if (value instanceof MutableLong) {
			return (MutableLong) value;
		}
		if (value instanceof Number) {
			return new MutableLong(((Number)value).longValue());
		}
		try {
			return (new MutableLong(value.toString()));
		} catch (NumberFormatException nfex) {
			throw new TypeConversionException(value, nfex);
		}
	}

	public MutableLong convert(Object value) {
		return valueOf(value);
	}
	
}
