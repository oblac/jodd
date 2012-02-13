// Copyright (c) 2003-2012, Jodd Team (jodd.org). All Rights Reserved.

package jodd.typeconverter.impl;

import jodd.typeconverter.ConvertBean;
import jodd.typeconverter.TypeConverter;
import jodd.util.CsvUtil;

/**
 * Converts given object to <code>float[]</code>.
 * Conversion rules:
 * <li><code>null</code> value is returned as <code>null</code>
 * <li>string is considered as CSV value and split before conversion
 * <li>single value is returned as 1-length array wrapped over converted value
 * <li>native arrays are converted directly
 * <li>object arrays is converted element by element
 */
public class FloatArrayConverter implements TypeConverter<float[]> {

	protected final ConvertBean convertBean;

	public FloatArrayConverter(ConvertBean convertBean) {
		this.convertBean = convertBean;
	}

	public float[] convert(Object value) {
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
			return new float[] {convertBean.toFloatValue(value)};
		}

		if (type.getComponentType().isPrimitive()) {
			// primitive arrays
			if (type == float[].class) {
				return (float[]) value;
			}
			if (type == double[].class) {
				double[] values = (double[]) value;
				float[] results = new float[values.length];
				for (int i = 0; i < values.length; i++) {
					results[i] = (float) values[i];
				}
				return results;
			}
			if (type == int[].class) {
				int[] values = (int[]) value;
				float[] results = new float[values.length];
				for (int i = 0; i < values.length; i++) {
					results[i] = values[i];
				}
				return results;
			}
			if (type == long[].class) {
				long[] values = (long[]) value;
				float[] results = new float[values.length];
				for (int i = 0; i < values.length; i++) {
					results[i] = values[i];
				}
				return results;
			}
			if (type == byte[].class) {
				byte[] values = (byte[]) value;
				float[] results = new float[values.length];
				for (int i = 0; i < values.length; i++) {
					results[i] = values[i];
				}
				return results;
			}
			if (type == boolean[].class) {
				boolean[] values = (boolean[]) value;
				float[] results = new float[values.length];
				for (int i = 0; i < values.length; i++) {
					results[i] = (values[i] == true ? 1 : 0);
				}
				return results;
			}
			if (type == short[].class) {
				short[] values = (short[]) value;
				float[] results = new float[values.length];
				for (int i = 0; i < values.length; i++) {
					results[i] = values[i];
				}
				return results;
			}
		}

		// array
		return convertArray((Object[]) value);
	}

	protected float[] convertArray(Object[] values) {
		float[] results = new float[values.length];
		for (int i = 0; i < values.length; i++) {
			results[i] = convertBean.toFloatValue(values[i]);
		}
		return results;
	}

}