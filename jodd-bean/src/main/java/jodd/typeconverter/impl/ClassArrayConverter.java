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

import jodd.typeconverter.TypeConverterManager;
import jodd.util.ArraysUtil;
import jodd.util.StringPool;
import jodd.util.StringUtil;

/**
 * Converts given object to <code>Class</code> array.
 * Conversion rules:
 * <ul>
 * <li><code>null</code> value is returned as <code>null</code></li>
 * <li>object of destination type is simply casted</li>
 * <li>single Class is wrapped in 1-length array</li>
 * <li>string value is converted to string array (from CSV format) and
 * then each element is converted</li>
 * </ul>
 *
 * This converter supports custom syntax for defining array of classes in a String:
 * <ul>
 * <li>The following chars are delimiters: {@code ,;\n}.</li>
 * <li>Blank lines are ignored</li>
 * <li>Lines that starts with '#' (after trim) are ignored.</li>
 * </ul>
 */
public class ClassArrayConverter extends ArrayConverter<Class> {

	public ClassArrayConverter(final TypeConverterManager typeConverterManager) {
		super(typeConverterManager, Class.class);
	}

	@Override
	protected Class[] createArray(final int length) {
		return new Class[length];
	}

	@Override
	protected String[] convertStringToArray(final String value) {
		String[] strings = StringUtil.splitc(value, NUMBER_DELIMITERS);

		int count = 0;

		for (int i = 0; i < strings.length; i++) {
			strings[count] = strings[i].trim();

			if (strings[count].length() == 0) {
				continue;
			}

			if (!strings[count].startsWith(StringPool.HASH)) {
				count++;
			}
		}

		if (count != strings.length) {
			return ArraysUtil.subarray(strings, 0, count);
		}

		return strings;
	}

}