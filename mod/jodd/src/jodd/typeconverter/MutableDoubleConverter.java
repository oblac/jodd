// Copyright (c) 2003-2010, Jodd Team (jodd.org). All Rights Reserved.

package jodd.typeconverter;

import jodd.mutable.MutableDouble;

/**
 * Converts given object to {@link MutableDouble}.
 */
public class MutableDoubleConverter implements TypeConverter<MutableDouble> {

	public static MutableDouble valueOf(Object value) {

		if (value == null) {
			return null;
		}
		if (value instanceof MutableDouble) {
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
