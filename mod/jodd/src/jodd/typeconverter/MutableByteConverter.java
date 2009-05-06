// Copyright (c) 2003-2009, Jodd Team (jodd.org). All Rights Reserved.

package jodd.typeconverter;

import jodd.mutable.MutableByte;

/**
 * Converts given object to {@link MutableByte}. Given object (if not already instance of
 * MutableByte) is first converted to String and then analyzed.
 */
public class MutableByteConverter implements TypeConverter<MutableByte> {


	public static MutableByte valueOf(Object value) {

		if (value == null) {
			return null;
		}
		if (value instanceof MutableByte) {
			return (MutableByte) value;
		}
		if (value instanceof Number) {
			return new MutableByte(((Number)value).byteValue());
		}
		try {
			return new MutableByte(value.toString());
		} catch (NumberFormatException nfex) {
			throw new TypeConversionException(value, nfex);
		}
	}

	public MutableByte convert(Object value) {
		return valueOf(value);
	}
	

}
