// Copyright (c) 2003-2012, Jodd Team (jodd.org). All Rights Reserved.

package jodd.typeconverter.impl;

import jodd.typeconverter.TypeConversionException;
import jodd.typeconverter.TypeConverter;
import jodd.util.StringUtil;

/**
 * Converts given object to <code>Short</code>.
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
