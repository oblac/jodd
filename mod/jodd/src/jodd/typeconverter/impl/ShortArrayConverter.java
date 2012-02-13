// Copyright (c) 2003-2012, Jodd Team (jodd.org). All Rights Reserved.

package jodd.typeconverter.impl;

import jodd.typeconverter.TypeConversionException;
import jodd.typeconverter.TypeConverter;
import jodd.util.CsvUtil;

/**
 *  Converts given object to <code>short[]</code>.
 */
public class ShortArrayConverter implements TypeConverter<short[]> {

	public short[] convert(Object value) {
		if (value == null) {
			return null;
		}

		Class type = value.getClass();
		if (type.isArray() == false) {
			if (value instanceof Number) {
				return new short[] {((Number) value).shortValue()};
			}

			String[] values = CsvUtil.toStringArray(value.toString());
			short[] result = new short[values.length];
			try {
				for (int i = 0, valuesLength = values.length; i < valuesLength; i++) {
					result[i] = Short.parseShort(values[i].trim());
				}
			} catch (NumberFormatException nfex) {
				throw new TypeConversionException(value, nfex);
			}
			return result;
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
		Object values[] = (Object[]) value;
		short[] results = new short[values.length];
		try {
			for (int i = 0; i < values.length; i++) {
				if (values[i] instanceof Number) {
					results[i] = ((Number) values[i]).shortValue();
				} else {
					results[i] = Short.parseShort(values[i].toString().trim());
				}
			}
		} catch (NumberFormatException nfex) {
			throw new TypeConversionException(value, nfex);
		}
		return results;
	}

}
