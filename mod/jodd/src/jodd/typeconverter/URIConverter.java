// Copyright (c) 2003-2010, Jodd Team (jodd.org). All Rights Reserved.

package jodd.typeconverter;

import java.net.URI;
import java.net.URISyntaxException;

/**
 * Convert given object to URI.
 */
public class URIConverter implements TypeConverter<URI> {

	public static URI valueOf(Object value) {
		if (value == null) {
			return null;
		}

		if (value instanceof URI) {
			return (URI) value;
		}
		try {
			return new URI(value.toString());
		} catch (URISyntaxException usex) {
			throw new TypeConversionException(value, usex);
		}
	}

	public URI convert(Object value) {
		return valueOf(value);
	}

}
