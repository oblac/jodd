// Copyright (c) 2003-present, Jodd Team (http://jodd.org)
// All rights reserved.
//
// Redistribution and use in source and binary forms, with or without
// modification, are permitted provided that the following conditions are met:
//
// 1. Redistributions of source code must retain the above copyright notice,
// this list of conditions and the following disclaimer.
//
// 2. Redistributions in binary form must reproduce the above copyright
// notice, this list of conditions and the following disclaimer in the
// documentation and/or other materials provided with the distribution.
//
// THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
// AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
// IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
// ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE
// LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
// CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
// SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
// INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
// CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
// ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
// POSSIBILITY OF SUCH DAMAGE.

package jodd.typeconverter.impl;

import jodd.io.FileUtil;
import jodd.typeconverter.TypeConversionException;
import jodd.typeconverter.TypeConverter;

import java.io.File;
import java.net.URI;
import java.net.URL;
import java.net.MalformedURLException;

/**
 * Converts given object to <code>URL</code>.
 * Conversion rules:
 * <ul>
 * <li><code>null</code> value is returned as <code>null</code></li>
 * <li>object of destination type is simply casted</li>
 * <li><code>File</code> is converted</li>
 * <li><code>URI</code> is converted</li>
 * <li><code>String</code> representation is used for creating URL</li>
 * </ul>
 */
public class URLConverter implements TypeConverter<URL> {

	public URL convert(Object value) {
		if (value == null) {
			return null;
		}
		
		if (value instanceof URL) {
			return (URL) value;
		}

		if (value instanceof File) {
			File file = (File) value;
			try {
				return FileUtil.toURL(file);
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
}