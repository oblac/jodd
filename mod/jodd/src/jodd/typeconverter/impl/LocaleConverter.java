// Copyright (c) 2003-2012, Jodd Team (jodd.org). All Rights Reserved.

package jodd.typeconverter.impl;

import jodd.typeconverter.TypeConverter;
import jodd.util.LocaleUtil;

import java.util.Locale;

/**
 * Converts given object to Java locale.
 */
public class LocaleConverter implements TypeConverter<Locale> {

	public Locale convert(Object value) {
		if (value == null) {
			return null;
		}

		if (value.getClass() == Locale.class) {
			return (Locale) value;
		}

		return LocaleUtil.getLocale(value.toString());
	}

}
