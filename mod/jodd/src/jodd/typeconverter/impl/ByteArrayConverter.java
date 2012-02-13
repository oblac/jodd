// Copyright (c) 2003-2012, Jodd Team (jodd.org). All Rights Reserved.

package jodd.typeconverter.impl;

import jodd.typeconverter.TypeConversionException;
import jodd.typeconverter.TypeConverter;
import jodd.util.CsvUtil;

import java.sql.Blob;
import java.sql.SQLException;

/**
 *  Converts given object to <code>byte[]</code>.
 */
public class ByteArrayConverter implements TypeConverter<byte[]> {

	public byte[] convert(Object value) {
		if (value == null) {
			return null;
		}
		Class type = value.getClass();
		if (type.isArray() == false) {
			if (value instanceof Number) {
				return new byte[] {((Number) value).byteValue()};
			}
			if (value instanceof Blob) {
				Blob blob = (Blob) value;
				try {
					long length = blob.length();
					if (length > Integer.MAX_VALUE) {
						throw new TypeConversionException("Blob is too big.");
					}
					return blob.getBytes(1, (int) length);
				} catch (SQLException sex) {
					throw new TypeConversionException(value, sex);
				}
			}

			String[] values = CsvUtil.toStringArray(value.toString());
			byte[] result = new byte[values.length];
			try {
				for (int i = 0, valuesLength = values.length; i < valuesLength; i++) {
					result[i] = Byte.parseByte(values[i].trim());
				}
			} catch (NumberFormatException nfex) {
				throw new TypeConversionException(value, nfex);
			}
			return result;
		}

		if (type.getComponentType().isPrimitive()) {
			// primitive arrays
			if (type == byte[].class) {
				return (byte[]) value;
			}
			if (type == int[].class) {
				int[] values = (int[]) value;
				byte[] results = new byte[values.length];
				for (int i = 0; i < values.length; i++) {
					results[i] = (byte) values[i];
				}
				return results;
			}
			if (type == long[].class) {
				long[] values = (long[]) value;
				byte[] results = new byte[values.length];
				for (int i = 0; i < values.length; i++) {
					results[i] = (byte) values[i];
				}
				return results;
			}
			if (type == double[].class) {
				double[] values = (double[]) value;
				byte[] results = new byte[values.length];
				for (int i = 0; i < values.length; i++) {
					results[i] = (byte) values[i];
				}
				return results;
			}
			if (type == float[].class) {
				float[] values = (float[]) value;
				byte[] results = new byte[values.length];
				for (int i = 0; i < values.length; i++) {
					results[i] = (byte) values[i];
				}
				return results;
			}
			if (type == boolean[].class) {
				boolean[] values = (boolean[]) value;
				byte[] results = new byte[values.length];
				for (int i = 0; i < values.length; i++) {
					results[i] = (byte) (values[i] == true ? 1 : 0);
				}
				return results;
			}
			if (type == short[].class) {
				short[] values = (short[]) value;
				byte[] results = new byte[values.length];
				for (int i = 0; i < values.length; i++) {
					results[i] = (byte) values[i];
				}
				return results;
			}
		}

		// arrays
		Object[] values = (Object[]) value;
		byte[] results = new byte[values.length];
		try {
			for (int i = 0; i < values.length; i++) {
				if (values[i] instanceof Number) {
					results[i] = ((Number) values[i]).byteValue();
				} else {
					results[i] = Byte.parseByte(values[i].toString().trim());
				}
			}
		} catch (NumberFormatException nfex) {
			throw new TypeConversionException(value, nfex);
		}
		return results;
	}

}