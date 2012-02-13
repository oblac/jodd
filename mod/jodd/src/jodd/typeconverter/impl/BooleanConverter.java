// Copyright (c) 2003-2012, Jodd Team (jodd.org). All Rights Reserved.

package jodd.typeconverter.impl;

import jodd.typeconverter.TypeConversionException;
import jodd.typeconverter.TypeConverter;

import static jodd.util.StringPool.*;

/**
 * Converts given object to <code>Boolean</code>. Given object (if not already instance of
 * <code>Boolean</code>) is first converted to <code>String</code> and then analyzed.
 */
public class BooleanConverter implements TypeConverter<Boolean> {

	public Boolean convert(Object value) {
		if (value == null) {
			return null;
		}

		if (value.getClass() == Boolean.class) {
			return (Boolean) value;
		}

		String stringValue = value.toString().trim().toLowerCase();
		if (stringValue.equals(YES) ||
				stringValue.equals(Y) ||
				stringValue.equals(TRUE) ||
				stringValue.equals(ON) ||
				stringValue.equals(ONE)) {
			return Boolean.TRUE;
		}
		if (stringValue.equals(NO) ||
				stringValue.equals(N) ||
				stringValue.equals(FALSE) ||
				stringValue.equals(OFF) ||
				stringValue.equals(ZERO)) {
			return Boolean.FALSE;
		}

		throw new TypeConversionException(value);
	}

}