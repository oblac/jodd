// Copyright (c) 2003-2011, Jodd Team (jodd.org). All Rights Reserved.

package jodd.typeconverter.impl;

import jodd.typeconverter.TypeConversionException;
import jodd.typeconverter.TypeConverter;

import static jodd.util.StringPool.*;

/**
 * Converts given object to <code>Boolean</code>. Given object (if not already instance of
 * <code>Boolean</code>) is first converted to <code>String</code> and then analyzed.
 */
public class BooleanConverter implements TypeConverter<Boolean> {

	public static Boolean valueOf(Object value) {
		if (value == null) {
			return null;
		}
		if (value.getClass() == Boolean.class) {
			return (Boolean) value;
		}
		String stringValue = value.toString();
		if (stringValue.equalsIgnoreCase(YES) ||
				stringValue.equalsIgnoreCase(Y) ||
				stringValue.equalsIgnoreCase(TRUE) ||
				stringValue.equalsIgnoreCase(ON) ||
				stringValue.equalsIgnoreCase(ONE)) {
			return Boolean.TRUE;
		}
		if (stringValue.equalsIgnoreCase(NO) ||
				stringValue.equalsIgnoreCase(N) ||
				stringValue.equalsIgnoreCase(FALSE) ||
				stringValue.equalsIgnoreCase(OFF) ||
				stringValue.equalsIgnoreCase(ZERO)) {
			return Boolean.FALSE;
		}
		throw new TypeConversionException(value);
	}

	public Boolean convert(Object value) {
		return valueOf(value);
	}
	
}
