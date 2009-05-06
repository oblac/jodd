// Copyright (c) 2003-2009, Jodd Team (jodd.org). All Rights Reserved.

package jodd.typeconverter;

import jodd.util.CsvUtil;

/**
 * Converts given object to String[].
 * If object is an array than it is converted to String[] array.
 * If object is not an array, new String[] array will be created from CSV reprenstation of toString.
 */
public class StringArrayConverter implements TypeConverter<String[]> {

	public static String[] valueOf(Object value) {

		if (value == null) {
			return null;
		}
		Class type = value.getClass();
		if (type.isArray() == false) {
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
			if (values[i] != null) {
				result[i] = values[i].toString();
			}
		}
		return result;
	}

	public String[] convert(Object value) {
		return valueOf(value);
	}
	
}
