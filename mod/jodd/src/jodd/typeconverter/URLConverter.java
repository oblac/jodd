// Copyright (c) 2003-2010, Jodd Team (jodd.org). All Rights Reserved.

package jodd.typeconverter;

import java.net.URL;
import java.net.MalformedURLException;

/**
 * Converts given object to URL.
 */
public class URLConverter implements TypeConverter<URL> {

	public static URL valueOf(Object value) {
		if (value == null) {
			return null;
		}
		
		if (value instanceof URL) {
			return (URL) value;
		}
		try {
			return new URL(value.toString());
		} catch (MalformedURLException muex) {
			throw new TypeConversionException(value, muex);
		}
	}

	public URL convert(Object value) {
		return valueOf(value);
	}

}