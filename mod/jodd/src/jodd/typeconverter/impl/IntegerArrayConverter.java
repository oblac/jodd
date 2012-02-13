// Copyright (c) 2003-2012, Jodd Team (jodd.org). All Rights Reserved.

package jodd.typeconverter.impl;

import jodd.typeconverter.ConvertBean;
import jodd.typeconverter.TypeConverter;
import jodd.util.CsvUtil;

/**
 *  Converts given object to <code>int[]</code>.
 */
public class IntegerArrayConverter implements TypeConverter<int[]> {

	protected final ConvertBean convertBean;

	public IntegerArrayConverter(ConvertBean convertBean) {
		this.convertBean = convertBean;
	}

	public int[] convert(Object value) {
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
			return new int[] {convertBean.toIntValue(value)};
		}

		if (type.getComponentType().isPrimitive()) {
			// primitive arrays
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
		}

		// array
		return convertArray((Object[]) value);
	}

	protected int[] convertArray(Object[] values) {
		int[] results = new int[values.length];
		for (int i = 0; i < values.length; i++) {
			results[i] = convertBean.toIntValue(values[i]);
		}
		return results;
	}

}