// Copyright (c) 2003-2009, Jodd Team (jodd.org). All Rights Reserved.

package jodd.typeconverter;

import java.math.BigInteger;

/**
 * Converts given object to BigInteger.
 */
public class BigIntegerConverter implements TypeConverter<BigInteger> {

	public static BigInteger valueOf(Object value) {
		if (value == null) {
			return null;
		}
		if (value instanceof BigInteger) {
			return (BigInteger) value;
		}
		try {
			return (new BigInteger(value.toString()));
		} catch (NumberFormatException nfex) {
			throw new TypeConversionException(value, nfex);
		}
	}

	public BigInteger convert(Object value) {
		return valueOf(value);
	}

}
