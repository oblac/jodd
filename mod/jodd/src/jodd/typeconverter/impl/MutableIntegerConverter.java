// Copyright (c) 2003-2010, Jodd Team (jodd.org). All Rights Reserved.

package jodd.typeconverter.impl;

import jodd.mutable.MutableInteger;
import jodd.typeconverter.TypeConversionException;
import jodd.typeconverter.TypeConverter;

/**
 * Converts given object to an {@link MutableInteger}.
 */
public class MutableIntegerConverter implements TypeConverter<MutableInteger> {

	public static MutableInteger valueOf(Object value) {
		if (value == null) {
			return null;
		}

		if (value.getClass() == MutableInteger.class) {
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
