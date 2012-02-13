// Copyright (c) 2003-2012, Jodd Team (jodd.org). All Rights Reserved.

package jodd.typeconverter.impl;

import jodd.typeconverter.ConvertBean;
import jodd.typeconverter.TypeConverter;
import jodd.util.CsvUtil;

/**
 *  Converts given object to <code>double[]</code>.
 */
public class DoubleArrayConverter implements TypeConverter<double[]> {

	protected final ConvertBean convertBean;

	public DoubleArrayConverter(ConvertBean convertBean) {
		this.convertBean = convertBean;
	}

	public double[] convert(Object value) {
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
			return new double[] {convertBean.toDoubleValue(value)};
		}

		if (type.getComponentType().isPrimitive()) {
			// primitive arrays
			if (type == double[].class) {
				return (double[]) value;
			}
			if (type == float[].class) {
				float[] values = (float[]) value;
				double[] results = new double[values.length];
				for (int i = 0; i < values.length; i++) {
					results[i] = values[i];
				}
				return results;
			}
			if (type == int[].class) {
				int[] values = (int[]) value;
				double[] results = new double[values.length];
				for (int i = 0; i < values.length; i++) {
					results[i] = values[i];
				}
				return results;
			}
			if (type == long[].class) {
				long[] values = (long[]) value;
				double[] results = new double[values.length];
				for (int i = 0; i < values.length; i++) {
					results[i] = values[i];
				}
				return results;
			}
			if (type == byte[].class) {
				byte[] values = (byte[]) value;
				double[] results = new double[values.length];
				for (int i = 0; i < values.length; i++) {
					results[i] = values[i];
				}
				return results;
			}
			if (type == boolean[].class) {
				boolean[] values = (boolean[]) value;
				double[] results = new double[values.length];
				for (int i = 0; i < values.length; i++) {
					results[i] = (values[i] == true ? 1 : 0);
				}
				return results;
			}
			if (type == short[].class) {
				short[] values = (short[]) value;
				double[] results = new double[values.length];
				for (int i = 0; i < values.length; i++) {
					results[i] = values[i];
				}
				return results;
			}
		}

		// array
		return convertArray((Object[]) value);
	}

	protected double[] convertArray(Object[] values) {
		double[] results = new double[values.length];
		for (int i = 0; i < values.length; i++) {
			results[i] = convertBean.toDoubleValue(values[i]);
		}
		return results;
	}

}