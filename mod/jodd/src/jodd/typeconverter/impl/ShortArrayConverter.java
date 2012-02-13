// Copyright (c) 2003-2012, Jodd Team (jodd.org). All Rights Reserved.

package jodd.typeconverter.impl;

import jodd.typeconverter.ConvertBean;
import jodd.typeconverter.TypeConverter;
import jodd.util.CsvUtil;

/**
 *  Converts given object to <code>short[]</code>.
 */
public class ShortArrayConverter implements TypeConverter<short[]> {

	protected final ConvertBean convertBean;

	public ShortArrayConverter(ConvertBean convertBean) {
		this.convertBean = convertBean;
	}

	public short[] convert(Object value) {
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
			return new short[] {convertBean.toShortValue(value)};
		}

		if (type.getComponentType().isPrimitive()) {
			// primitive arrays
			if (type == short[].class) {
				return (short[]) value;
			}
			if (type == int[].class) {
				int[] values = (int[]) value;
				short[] results = new short[values.length];
				for (int i = 0; i < values.length; i++) {
					results[i] = (short) values[i];
				}
				return results;
			}
			if (type == long[].class) {
				long[] values = (long[]) value;
				short[] results = new short[values.length];
				for (int i = 0; i < values.length; i++) {
					results[i] = (short) values[i];
				}
				return results;
			}
			if (type == double[].class) {
				double[] values = (double[]) value;
				short[] results = new short[values.length];
				for (int i = 0; i < values.length; i++) {
					results[i] = (short) values[i];
				}
				return results;
			}
			if (type == byte[].class) {
				byte[] values = (byte[]) value;
				short[] results = new short[values.length];
				for (int i = 0; i < values.length; i++) {
					results[i] = values[i];
				}
				return results;
			}
			if (type == float[].class) {
				float[] values = (float[]) value;
				short[] results = new short[values.length];
				for (int i = 0; i < values.length; i++) {
					results[i] = (short) values[i];
				}
				return results;
			}
			if (type == boolean[].class) {
				boolean[] values = (boolean[]) value;
				short[] results = new short[values.length];
				for (int i = 0; i < values.length; i++) {
					results[i] = (short) (values[i] == true ? 1 : 0);
				}
				return results;
			}
		}

		// array
		return convertArray((Object[]) value);
	}

	protected short[] convertArray(Object[] values) {
		short[] results = new short[values.length];
		for (int i = 0; i < values.length; i++) {
			results[i] = convertBean.toShortValue(values[i]);
		}
		return results;
	}

}