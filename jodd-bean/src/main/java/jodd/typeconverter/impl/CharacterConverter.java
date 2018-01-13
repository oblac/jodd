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

import jodd.typeconverter.TypeConversionException;
import jodd.typeconverter.TypeConverter;
import jodd.util.StringUtil;

/**
 * Converts given object to <code>Character</code>.
 * Conversion rules:
 * <ul>
 * <li><code>null</code> value is returned as <code>null</code></li>
 * <li>object of destination type is simply casted</li>
 * <li><code>Number</code> is converted to <code>char</code> value</li>
 * <li>finally, <code>toString()</code> value of length 1 is converted to <code>char</code></li>
 * <li>if string is longer, and made of digits, try to convert it to int first</li>
 * </ul>
 */
public class CharacterConverter implements TypeConverter<Character> {

	public Character convert(final Object value) {
		if (value == null) {
			return null;
		}
		if (value.getClass() == Character.class) {
			return (Character) value;
		}
		if (value instanceof Number) {
			char c = (char) ((Number) value).intValue();
			return Character.valueOf(c);
		}
		try {
			String s = value.toString();
			if (s.length() != 1) {
				s = s.trim();
				if (!StringUtil.containsOnlyDigitsAndSigns(s)) {
					throw new TypeConversionException(value);
				}

				try {
					char c = (char) Integer.parseInt(s);
					return Character.valueOf(c);
				} catch (NumberFormatException nfex) {
					throw new TypeConversionException(value, nfex);
				}
			}
			return Character.valueOf(s.charAt(0));
		} catch (IndexOutOfBoundsException ioobex) {
			throw new TypeConversionException(value, ioobex);
		}
	}

}