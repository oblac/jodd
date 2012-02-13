// Copyright (c) 2003-2012, Jodd Team (jodd.org). All Rights Reserved.

package jodd.typeconverter.impl;

import jodd.typeconverter.TypeConversionException;
import jodd.typeconverter.TypeConverter;
import jodd.util.StringUtil;

/**
 * Converts given object to <code>Byte</code>.
 * Conversion rules:
 * <li><code>null</code> value is returned as <code>null</code>
 * <li>object of destination type is simply casted
 * <li>object is converted to string, trimmed, and then converted if possible.
 * Number string may start with plus and minus sign.
 */
public class ByteConverter implements TypeConverter<Byte> {

	public Byte convert(Object value) {
		if (value == null) {
			return null;
		}
		if (value.getClass() == Byte.class) {
			return (Byte) value;
		}
		if (value instanceof Number) {
			return Byte.valueOf(((Number)value).byteValue());
		}
		try {
			String stringValue = value.toString().trim();
			if (StringUtil.startsWithChar(stringValue, '+')) {
				stringValue = stringValue.substring(1);
			}
			return Byte.valueOf(stringValue);
		} catch (NumberFormatException nfex) {
			throw new TypeConversionException(value, nfex);
		}
	}

}
