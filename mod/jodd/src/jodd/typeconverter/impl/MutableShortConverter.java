// Copyright (c) 2003-2012, Jodd Team (jodd.org). All Rights Reserved.

package jodd.typeconverter.impl;

import jodd.mutable.MutableShort;
import jodd.typeconverter.TypeConversionException;
import jodd.typeconverter.TypeConverter;


/**
 * Converts given object to {@link MutableShort}.
 */
public class MutableShortConverter implements TypeConverter<MutableShort> {

	public static MutableShort valueOf(Object value) {
		if (value == null) {
			return null;
		}

		if (value.getClass() == MutableShort.class) {
			return (MutableShort) value;
		}
		if (value instanceof Number) {
			return new MutableShort(((Number)value).byteValue());
		}
		try {
			return new MutableShort(value.toString().trim());
		} catch (NumberFormatException nfex) {
			throw new TypeConversionException(value, nfex);
		}
	}

	public MutableShort convert(Object value) {
		return valueOf(value);
	}

}
