// Copyright (c) 2003-present, Jodd Team (jodd.org). All Rights Reserved.

package jodd.typeconverter.impl;

import jodd.typeconverter.TypeConverterManagerBean;
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

	public ClassArrayConverter(TypeConverterManagerBean typeConverterManagerBean) {
		super(typeConverterManagerBean, Class.class);
	}

	@Override
	protected Class[] createArray(int length) {
		return new Class[length];
	}

	@Override
	protected String[] convertStringToArray(String value) {
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