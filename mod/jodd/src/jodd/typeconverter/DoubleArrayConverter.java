// Copyright (c) 2003-2010, Jodd Team (jodd.org). All Rights Reserved.

package jodd.typeconverter;

/**
 *  Converts given object to double[].
 */
public class DoubleArrayConverter implements TypeConverter<double[]> {

	public static double[] valueOf(Object value) {

		if (value == null) {
			return null;
		}

		Class type = value.getClass();
		if (type.isArray() == false) {
			if (value instanceof Number) {
				return new double[] {((Number) value).doubleValue()};
			}
			try {
				return new double[] {Double.parseDouble(value.toString())};
			} catch (NumberFormatException nfex) {
				throw new TypeConversionException(value, nfex);
			}

		}

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


		Object[] values = (Object[]) value;
		double[] results = new double[values.length];
		try {
			for (int i = 0; i < values.length; i++) {
				if (values[i] != null) {
					if (values[i] instanceof Number) {
						results[i] = ((Number) values[i]).doubleValue();
					} else {
						results[i] = Double.parseDouble(values[i].toString());
					}
				}
			}
		} catch (NumberFormatException nfex) {
			throw new TypeConversionException(value, nfex);
		}
		return results;
	}

	public double[] convert(Object value) {
		return valueOf(value);
	}
}
