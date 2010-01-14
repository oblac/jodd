// Copyright (c) 2003-2010, Jodd Team (jodd.org). All Rights Reserved.

package jodd.typeconverter;

import java.math.BigDecimal;

/**
 * Converts given object to BigDecimal.
 */
public class BigDecimalConverter implements TypeConverter<BigDecimal> {

	public static BigDecimal valueOf(Object value) {
		if (value == null) {
			return null;
		}
		if (value instanceof BigDecimal) {
			return (BigDecimal) value;
		}
		try {
			return (new BigDecimal(value.toString()));
		} catch (NumberFormatException nfex) {
			throw new TypeConversionException(value, nfex); 
		}
	}


	public BigDecimal convert(Object value) {
		return valueOf(value);
	}
}
