// Copyright (c) 2003-2012, Jodd Team (jodd.org). All Rights Reserved.

package jodd.typeconverter.impl;

import jodd.typeconverter.TypeConversionException;
import jodd.typeconverter.TypeConverter;

import java.io.File;
import java.net.URI;
import java.net.URL;
import java.net.MalformedURLException;

/**
 * Converts given object to <code>URL</code>.
 */
public class URLConverter implements TypeConverter<URL> {

	public static URL valueOf(Object value) {
		if (value == null) {
			return null;
		}
		
		if (value instanceof URL) {
			return (URL) value;
		}

		if (value instanceof File) {
			File file = (File) value;
			try {
				return file.toURL();
			} catch (MalformedURLException muex) {
				throw new TypeConversionException(value, muex);
			}
		}

		if (value instanceof URI) {
			URI uri = (URI) value;
			try {
				return uri.toURL();
			} catch (MalformedURLException muex) {
				throw new TypeConversionException(value, muex);
			}
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