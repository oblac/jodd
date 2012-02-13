// Copyright (c) 2003-2012, Jodd Team (jodd.org). All Rights Reserved.

package jodd.typeconverter.impl;

import jodd.typeconverter.ConvertBean;
import jodd.typeconverter.TypeConverter;

/**
 * Converts given object to <code>char[]</code>.
 * Conversion rules:
 * <li><code>null</code> value is returned as <code>null</code>
 * <li>single value is converted to string and its characters are returned
 * <li>native arrays are converted directly
 * <li>object arrays is converted element by element
 */
public class CharacterArrayConverter implements TypeConverter<char[]> {

	protected final ConvertBean convertBean;

	public CharacterArrayConverter(ConvertBean convertBean) {
		this.convertBean = convertBean;
	}

	public char[] convert(Object value) {
		if (value == null) {
			return null;
		}

		Class type = value.getClass();
		if (type.isArray() == false) {
			// single value
			return value.toString().toCharArray();
		}

		if (type.getComponentType().isPrimitive()) {
			// primitive arrays
			if (type == char[].class) {
				return (char[]) value;
			}
			if (type == int[].class) {
				int[] values = (int[]) value;
				char[] results = new char[values.length];
				for (int i = 0; i < values.length; i++) {
					results[i] = (char) values[i];
				}
				return results;
			}
			if (type == long[].class) {
				int[] values = (int[]) value;
				char[] results = new char[values.length];
				for (int i = 0; i < values.length; i++) {
					results[i] = (char) values[i];
				}
				return results;
			}
			if (type == double[].class) {
				double[] values = (double[]) value;
				char[] results = new char[values.length];
				for (int i = 0; i < values.length; i++) {
					results[i] = (char) values[i];
				}
				return results;
			}
			if (type == byte[].class) {
				byte[] values = (byte[]) value;
				char[] results = new char[values.length];
				for (int i = 0; i < values.length; i++) {
					results[i] = (char) values[i];
				}
				return results;
			}
			if (type == float[].class) {
				float[] values = (float[]) value;
				char[] results = new char[values.length];
				for (int i = 0; i < values.length; i++) {
					results[i] = (char) values[i];
				}
				return results;
			}
			if (type == boolean[].class) {
				boolean[] values = (boolean[]) value;
				char[] results = new char[values.length];
				for (int i = 0; i < values.length; i++) {
					results[i] = (values[i] == true ? '1' : '0');
				}
				return results;
			}
			if (type == short[].class) {
				short[] values = (short[]) value;
				char[] results = new char[values.length];
				for (int i = 0; i < values.length; i++) {
					results[i] = (char) values[i];
				}
				return results;
			}
		}

		// array
		return convertArray((Object[]) value);
	}

	protected char[] convertArray(Object[] values) {
		char[] results = new char[values.length];
		for (int i = 0; i < values.length; i++) {
			results[i] = convertBean.toCharValue(values[i]);
		}
		return results;
	}

}
