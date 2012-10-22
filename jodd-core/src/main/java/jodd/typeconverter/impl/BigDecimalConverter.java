// Copyright (c) 2003-2012, Jodd Team (jodd.org). All Rights Reserved.

package jodd.typeconverter.impl;

import jodd.typeconverter.TypeConversionException;
import jodd.typeconverter.TypeConverter;

import java.math.BigDecimal;

/**
 * Converts given object to <code>BigDecimal</code>.
 * Conversion rules:
 * <ul>
 * <li><code>null</code> value is returned as <code>null</code></li>
 * <li>object of destination type is simply casted</li>
 * <li>object is converted to string, trimmed, and then converted if possible</li>
 * </ul>
 */
public class BigDecimalConverter implements TypeConverter<BigDecimal> {

	public BigDecimal convert(Object value) {
		if (value == null) {
			return null;
		}
		
		if (value instanceof BigDecimal) {
			return (BigDecimal) value;
		}
		try {
			return new BigDecimal(value.toString().trim());
		} catch (NumberFormatException nfex) {
			throw new TypeConversionException(value, nfex);
		}
	}

}