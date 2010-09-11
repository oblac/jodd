// Copyright (c) 2003-2010, Jodd Team (jodd.org). All Rights Reserved.

package jodd.typeconverter;

import jodd.util.CsvUtil;

/**
 *  Converts given object to int[].
 */
public class IntegerArrayConverter implements TypeConverter<int[]> {

	public static int[] valueOf(Object value) {
		if (value == null) {
			return null;
		}

		Class type = value.getClass();
		if (type.isArray() == false) {
			if (value instanceof Number) {
				return new int[] {((Number) value).intValue()};
			}
			
			String[] values = CsvUtil.toStringArray(value.toString());
			int[] result = new int[values.length];
			try {
				for (int i = 0, valuesLength = values.length; i < valuesLength; i++) {
					result[i] = Integer.parseInt(values[i].trim());
				}
			} catch (NumberFormatException nfex) {
				throw new TypeConversionException(value, nfex);
			}
			return result;
		}

		if (type == int[].class) {
			return (int[]) value;
		}
		if (type == long[].class) {
			long[] values = (long[]) value;
			int results[] = new int[values.length];
			for (int i = 0; i < values.length; i++) {
				results[i] = (int) values[i];
			}
			return results;
		}
		if (type == double[].class) {
			double[] values = (double[]) value;
			int results[] = new int[values.length];
			for (int i = 0; i < values.length; i++) {
				results[i] = (int) values[i];
			}
			return results;
		}
		if (type == byte[].class) {
			byte[] values = (byte[]) value;
			int results[] = new int[values.length];
			for (int i = 0; i < values.length; i++) {
				results[i] = values[i];
			}
			return results;
		}
		if (type == float[].class) {
			float[] values = (float[]) value;
			int results[] = new int[values.length];
			for (int i = 0; i < values.length; i++) {
				results[i] = (int) values[i];
			}
			return results;
		}
		if (type == boolean[].class) {
			boolean[] values = (boolean[]) value;
			int results[] = new int[values.length];
			for (int i = 0; i < values.length; i++) {
				results[i] = (values[i] == true ? 1 : 0);
			}
			return results;
		}
		if (type == short[].class) {
			short[] values = (short[]) value;
			int results[] = new int[values.length];
			for (int i = 0; i < values.length; i++) {
				results[i] = values[i];
			}
			return results;
		}


		Object[] values = (Object[]) value;
		int[] results = new int[values.length];
		try {
			for (int i = 0; i < values.length; i++) {
				if (values[i] != null) {
					if (values[i] instanceof Number) {
						results[i] = ((Number) values[i]).intValue();
					} else {
						results[i] = Integer.parseInt(values[i].toString());
					}
				}
			}
		} catch (NumberFormatException nfex) {
			throw new TypeConversionException(value, nfex);
		}
		return results;
	}

	public int[] convert(Object value) {
		return valueOf(value);
	}
}
