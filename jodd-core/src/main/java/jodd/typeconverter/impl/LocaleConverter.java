// Copyright (c) 2003-2012, Jodd Team (jodd.org). All Rights Reserved.

package jodd.typeconverter.impl;

import jodd.typeconverter.TypeConverter;
import jodd.util.LocaleUtil;

import java.util.Locale;

/**
 * Converts given object to Java <code>Locale</code>.
 * <ul>
 * <li><code>null</code> value is returned as <code>null</code></li>
 * <li>object of destination type is simply casted</li>
 * <li>finally, string representation of the object is used for getting the locale</li>
 * </ul>
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
