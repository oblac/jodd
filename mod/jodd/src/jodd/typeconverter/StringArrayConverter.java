// Copyright (c) 2003-2010, Jodd Team (jodd.org). All Rights Reserved.

package jodd.typeconverter;

import jodd.util.CsvUtil;

/**
 * Converts given object to String[].
 * If object is an array than it is converted to String[] array.
 * If object is not an array, new String[] array will be created from CSV representation of <code>toString</code>.
 * It handles special cases when <code>toString()</code> representation is not quite useful
 * (such of <code>Class</code>, when <code>toString</code> is replaced with <code>getName</code>.).
 */
public class StringArrayConverter implements TypeConverter<String[]> {

	public static String[] valueOf(Object value) {

		if (value == null) {
			return null;
		}
		Class type = value.getClass();
		if (type.isArray() == false) {
			// special case #1
			if (value instanceof Class) {
				return new String[] {((Class)value).getName()};
			}
			return CsvUtil.toStringArray(value.toString());
		}

		// handle arrays
		if (type == String[].class) {
			return (String[]) value;
		}

		if (type == int[].class) {
			int[] values = (int[]) value;
			String[] result = new String[values.length];
			for (int i = 0; i < values.length; i++) {
				result[i] = String.valueOf(values[i]);
			}
			return result;
		}
		if (type == long[].class) {
			long[] values = (long[]) value;
			String[] result = new String[values.length];
			for (int i = 0; i < values.length; i++) {
				result[i] = String.valueOf(values[i]);
			}
			return result;
		}
		if (type == double[].class) {
			double[] values = (double[]) value;
			String[] result = new String[values.length];
			for (int i = 0; i < values.length; i++) {
				result[i] = String.valueOf(values[i]);
			}
			return result;
		}
		if (type == byte[].class) {
			byte[] values = (byte[]) value;
			String[] result = new String[values.length];
			for (int i = 0; i < values.length; i++) {
				result[i] = String.valueOf(values[i]);
			}
			return result;
		}
		if (type == float[].class) {
			float[] values = (float[]) value;
			String[] result = new String[values.length];
			for (int i = 0; i < values.length; i++) {
				result[i] = String.valueOf(values[i]);
			}
			return result;
		}
		if (type == boolean[].class) {
			boolean[] values = (boolean[]) value;
			String[] result = new String[values.length];
			for (int i = 0; i < values.length; i++) {
				result[i] = String.valueOf(values[i]);
			}
			return result;
		}
		if (type == short[].class) {
			short[] values = (short[]) value;
			String[] result = new String[values.length];
			for (int i = 0; i < values.length; i++) {
				result[i] = String.valueOf(values[i]);
			}
			return result;
		}

		Object[] values = (Object[]) value;
		String[] result = new String[values.length];
		for (int i = 0; i < values.length; i++) {
			Object v = values[i];
			if (v != null) {
				// special case #1
				if (v instanceof Class) {
					result[i] = ((Class)v).getName();
				}
				result[i] = v.toString();
			}
		}
		return result;
	}

	public String[] convert(Object value) {
		return valueOf(value);
	}
	
}
