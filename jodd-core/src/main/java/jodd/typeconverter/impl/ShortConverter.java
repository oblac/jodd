// Copyright (c) 2003-2014, Jodd Team (jodd.org). All Rights Reserved.

package jodd.typeconverter.impl;

import jodd.typeconverter.TypeConversionException;
import jodd.typeconverter.TypeConverter;
import jodd.util.StringUtil;

/**
 * Converts given object to <code>Short</code>.
 * Conversion rules:
 * <ul>
 * <li><code>null</code> value is returned as <code>null</code></li>
 * <li>object of destination type is simply casted</li>
 * <li>object is converted to string, trimmed, and then converted if possible.</li>
 * </ul>
 * Number string may start with plus and minus sign.
 */
public class ShortConverter implements TypeConverter<Short> {

	public Short convert(Object value) {
		if (value == null) {
			return null;
		}

		if (value.getClass() == Short.class) {
			return (Short) value;
		}
		if (value instanceof Number) {
			return Short.valueOf(((Number)value).shortValue());
		}
		try {
			String stringValue = value.toString().trim();
			if (StringUtil.startsWithChar(stringValue, '+')) {
				stringValue = stringValue.substring(1);
			}
			return Short.valueOf(stringValue);
		} catch (NumberFormatException nfex) {
			throw new TypeConversionException(value, nfex);
		}
	}

}
