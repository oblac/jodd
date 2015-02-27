// Copyright (c) 2003-present, Jodd Team (jodd.org). All Rights Reserved.

package jodd.typeconverter.impl;

import jodd.typeconverter.TypeConversionException;
import jodd.typeconverter.TypeConverter;

import java.math.BigInteger;

/**
 * Converts given object to <code>BigInteger</code>.
 * Conversion rules:
 * <ul>
 * <li><code>null</code> value is returned as <code>null</code></li>
 * <li>object of destination type is simply casted</li>
 * <li>object is converted to string, trimmed, and then converted if possible</li>
 * </ul>
 */
public class BigIntegerConverter implements TypeConverter<BigInteger> {

	public BigInteger convert(Object value) {
		if (value == null) {
			return null;
		}

		if (value instanceof BigInteger) {
			return (BigInteger) value;
		}
		if (value instanceof Number) {
			return new BigInteger(String.valueOf(((Number)value).longValue()));
		}
		try {
			return new BigInteger(value.toString().trim());
		} catch (NumberFormatException nfex) {
			throw new TypeConversionException(value, nfex);
		}
	}

}