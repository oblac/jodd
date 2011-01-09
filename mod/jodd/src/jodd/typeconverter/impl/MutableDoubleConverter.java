// Copyright (c) 2003-2011, Jodd Team (jodd.org). All Rights Reserved.

package jodd.typeconverter.impl;

import jodd.mutable.MutableDouble;
import jodd.typeconverter.TypeConversionException;
import jodd.typeconverter.TypeConverter;

/**
 * Converts given object to {@link MutableDouble}.
 */
public class MutableDoubleConverter implements TypeConverter<MutableDouble> {

	public static MutableDouble valueOf(Object value) {
		if (value == null) {
			return null;
		}

		if (value.getClass() == MutableDouble.class) {
			return (MutableDouble) value;
		}
		if (value instanceof Number) {
			return new MutableDouble(((Number)value).doubleValue());
		}
		try {
			return new MutableDouble(value.toString());
		} catch (NumberFormatException nfex) {
			throw new TypeConversionException(value, nfex);
		}
	}

	public MutableDouble convert(Object value) {
		return valueOf(value);
	}
	
}
