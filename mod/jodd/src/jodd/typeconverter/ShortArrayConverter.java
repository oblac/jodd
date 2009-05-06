// Copyright (c) 2003-2009, Jodd Team (jodd.org). All Rights Reserved.

package jodd.typeconverter;

/**
 *  Converts given object to short[].
 */
public class ShortArrayConverter implements TypeConverter<short[]> {

	public static short[] valueOf(Object value) {

		if (value == null) {
			return null;
		}

		Class type = value.getClass();
		if (type.isArray() == false) {
			if (value instanceof Number) {
				return new short[] {((Number) value).shortValue()};
			}
			try {
				return new short[] {Short.parseShort(value.toString())};
			} catch (NumberFormatException nfex) {
				throw new TypeConversionException(value, nfex);
			}
		}

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

		Object values[] = (Object[]) value;
		short[] results = new short[values.length];
		try {
			for (int i = 0; i < values.length; i++) {
				if (values[i] instanceof Number) {
					results[i] = ((Number) values[i]).shortValue();
				} else {
					results[i] = Short.parseShort(values[i].toString());
				}
			}
		} catch (NumberFormatException nfex) {
			throw new TypeConversionException(value, nfex);
		}
		return results;
	}

	public short[] convert(Object value) {
		return valueOf(value);
	}
}
