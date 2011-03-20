// Copyright (c) 2003-2011, Jodd Team (jodd.org). All Rights Reserved.

package jodd.typeconverter.impl;

import jodd.typeconverter.TypeConversionException;
import jodd.typeconverter.TypeConverter;

/**
 *  Converts given object to <code>float[]</code>.
 */
public class FloatArrayConverter implements TypeConverter<float[]> {

	public static float[] valueOf(Object value) {
		if (value == null) {
			return null;
		}

		Class type = value.getClass();
		if (type.isArray() == false) {
			if (value instanceof Number) {
				return new float[] {((Number) value).floatValue()};
			}
			try {
				return new float[] {Float.parseFloat(value.toString())};
			} catch (NumberFormatException nfex) {
				throw new TypeConversionException(value, nfex);
			}
		}

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


		Object[] values = (Object[]) value;
		float[] results = new float[values.length];
		try {
			for (int i = 0; i < values.length; i++) {
				if (values[i] != null) {
					if (values[i] instanceof Number) {
						results[i] = ((Number) values[i]).floatValue();
					} else {
						results[i] = Float.parseFloat(values[i].toString());
					}
				}
			}
		} catch (NumberFormatException nfex) {
			throw new TypeConversionException(value, nfex);
		}
		return results;
	}

	public float[] convert(Object value) {
		return valueOf(value);
	}
}
