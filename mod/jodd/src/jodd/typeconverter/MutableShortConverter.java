// Copyright (c) 2003-2009, Jodd Team (jodd.org). All Rights Reserved.

package jodd.typeconverter;

import jodd.mutable.MutableShort;


/**
 * Converts given object to {@link MutableShort}. Given object (if not already instance of
 * MutableShort) is first converted to String and then analyzed.
 */
public class MutableShortConverter implements TypeConverter<MutableShort> {


	public static MutableShort valueOf(Object value) {

		if (value == null) {
			return null;
		}
		if (value instanceof MutableShort) {
			return (MutableShort) value;
		}
		if (value instanceof Number) {
			return new MutableShort(((Number)value).byteValue());
		}
		try {
			return new MutableShort(value.toString());
		} catch (NumberFormatException nfex) {
			throw new TypeConversionException(value, nfex);
		}
	}

	public MutableShort convert(Object value) {
		return valueOf(value);
	}

}
