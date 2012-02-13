// Copyright (c) 2003-2012, Jodd Team (jodd.org). All Rights Reserved.

package jodd.typeconverter.impl;

import jodd.typeconverter.ConvertBean;
import jodd.typeconverter.TypeConverter;
import jodd.util.CsvUtil;

/**
 * Converts given object to <code>long[]</code>.
 * Conversion rules:
 * <li><code>null</code> value is returned as <code>null</code>
 * <li>string is considered as CSV value and split before conversion
 * <li>single value is returned as 1-length array wrapped over converted value
 * <li>native arrays are converted directly
 * <li>object arrays is converted element by element
 */
public class LongArrayConverter implements TypeConverter<long[]> {

	protected final ConvertBean convertBean;

	public LongArrayConverter(ConvertBean convertBean) {
		this.convertBean = convertBean;
	}

	public long[] convert(Object value) {
		if (value == null) {
			return null;
		}

		Class type = value.getClass();
		if (type.isArray() == false) {
			// string
			if (type == String.class) {
				String[] values = CsvUtil.toStringArray(value.toString());
				return convertArray(values);
			}

			// single value
			return new long[] {convertBean.toLongValue(value)};
		}

		if (type.getComponentType().isPrimitive()) {
			// primitive arrays
			if (type == long[].class) {
				return (long[]) value;
			}
			if (type == int[].class) {
				int[] values = (int[]) value;
				long[] results = new long[values.length];
				for (int i = 0; i < values.length; i++) {
					results[i] = values[i];
				}
				return results;
			}
			if (type == double[].class) {
				double[] values = (double[]) value;
				long[] results = new long[values.length];
				for (int i = 0; i < values.length; i++) {
					results[i] = (long) values[i];
				}
				return results;
			}
			if (type == byte[].class) {
				byte[] values = (byte[]) value;
				long[] results = new long[values.length];
				for (int i = 0; i < values.length; i++) {
					results[i] = values[i];
				}
				return results;
			}
			if (type == float[].class) {
				float[] values = (float[]) value;
				long[] results = new long[values.length];
				for (int i = 0; i < values.length; i++) {
					results[i] = (long) values[i];
				}
				return results;
			}
			if (type == boolean[].class) {
				boolean[] values = (boolean[]) value;
				long[] results = new long[values.length];
				for (int i = 0; i < values.length; i++) {
					results[i] = (values[i] == true ? 1L : 0L);
				}
				return results;
			}
			if (type == short[].class) {
				short[] values = (short[]) value;
				long[] results = new long[values.length];
				for (int i = 0; i < values.length; i++) {
					results[i] = values[i];
				}
				return results;
			}
		}

		// array
		return convertArray((Object[]) value);
	}

	protected long[] convertArray(Object[] values) {
		long[] results = new long[values.length];
		for (int i = 0; i < values.length; i++) {
			results[i] = convertBean.toLongValue(values[i]);
		}
		return results;
	}

}