// Copyright (c) 2003-2012, Jodd Team (jodd.org). All Rights Reserved.

package jodd.typeconverter.impl;

import jodd.typeconverter.TypeConversionException;
import jodd.typeconverter.TypeConverter;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

/**
 * Convert given object to <code>URI</code>.
 * Conversion rules:
 * <li><code>null</code> value is returned as <code>null</code>
 * <li>object of destination type is simply casted
 * <li><code>File</code> is converted
 * <li><code>URL</code> is converted
 * <li><code>String</code> representation is used for creating URI
 */
public class URIConverter implements TypeConverter<URI> {

	public URI convert(Object value) {
		if (value == null) {
			return null;
		}

		if (value instanceof URI) {
			return (URI) value;
		}

		if (value instanceof File) {
			File file = (File) value;
			return file.toURI();
		}

		if (value instanceof URL) {
			URL url = (URL) value;
			try {
				return url.toURI();
			} catch (URISyntaxException usex) {
				throw new TypeConversionException(value, usex);
			}
		}

		try {
			return new URI(value.toString());
		} catch (URISyntaxException usex) {
			throw new TypeConversionException(value, usex);
		}
	}

}
