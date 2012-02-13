// Copyright (c) 2003-2012, Jodd Team (jodd.org). All Rights Reserved.

package jodd.typeconverter.impl;

import jodd.typeconverter.ConvertBean;
import jodd.typeconverter.TypeConverter;
import jodd.util.CsvUtil;

/**
 * Converts given object to <code>boolean[]</code>.
 * Conversion rules:
 * <li><code>null</code> value is returned as <code>null</code>
 * <li>string is considered as CSV value and split before conversion
 * <li>single value is returned as 1-length array wrapped over converted value
 * <li>native arrays are converted directly
 * <li>object arrays is converted element by element
 */
public class BooleanArrayConverter implements TypeConverter<boolean[]> {

	protected final ConvertBean convertBean;

	public BooleanArrayConverter(ConvertBean convertBean) {
		this.convertBean = convertBean;
	}

	public boolean[] convert(Object value) {
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
			return new boolean[] {convertBean.toBooleanValue(value)};
		}

		if (type.getComponentType().isPrimitive()) {
			// primitive arrays
			if (type == boolean[].class) {
				return (boolean[]) value;
			}
			if (type == int[].class) {
				int[] values = (int[]) value;
				boolean[] results = new boolean[values.length];
				for (int i = 0; i < values.length; i++) {
					results[i] = (values[i] != 0);
				}
				return results;
			}
			if (type == long[].class) {
				long[] values = (long[]) value;
				boolean[] results = new boolean[values.length];
				for (int i = 0; i < values.length; i++) {
					results[i] = (values[i] != 0);
				}
				return results;
			}
			if (type == double[].class) {
				double[] values = (double[]) value;
				boolean[] results = new boolean[values.length];
				for (int i = 0; i < values.length; i++) {
					results[i] = (values[i] != 0);
				}
				return results;
			}
			if (type == float[].class) {
				float[] values = (float[]) value;
				boolean[] results = new boolean[values.length];
				for (int i = 0; i < values.length; i++) {
					results[i] = (values[i] != 0);
				}
				return results;
			}
			if (type == byte[].class) {
				byte[] values = (byte[]) value;
				boolean[] results = new boolean[values.length];
				for (int i = 0; i < values.length; i++) {
					results[i] = (values[i] != 0);
				}
				return results;
			}
			if (type == short[].class) {
				short[] values = (short[]) value;
				boolean[] results = new boolean[values.length];
				for (int i = 0; i < values.length; i++) {
					results[i] = (values[i] != 0);
				}
				return results;
			}
		}

		// array
		return convertArray((Object[]) value);
	}

	private boolean[] convertArray(Object[] values) {
		boolean[] result = new boolean[values.length];
		for (int i = 0; i < values.length; i++) {
			result[i] = convertBean.toBooleanValue(values[i]);
		}
		return result;
	}

}