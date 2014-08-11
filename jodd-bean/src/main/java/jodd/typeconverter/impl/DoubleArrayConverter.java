// Copyright (c) 2003-2014, Jodd Team (jodd.org). All Rights Reserved.

package jodd.typeconverter.impl;

import jodd.typeconverter.TypeConverter;
import jodd.typeconverter.TypeConverterManagerBean;
import jodd.util.StringUtil;
import jodd.util.collection.DoubleArrayList;

import java.util.Collection;
import java.util.List;

/**
 * Converts given object to <code>double[]</code>.
 */
public class DoubleArrayConverter implements TypeConverter<double[]> {

	protected final TypeConverterManagerBean typeConverterManagerBean;

	public DoubleArrayConverter(TypeConverterManagerBean typeConverterManagerBean) {
		this.typeConverterManagerBean = typeConverterManagerBean;
	}

	public double[] convert(Object value) {
		if (value == null) {
			return null;
		}

		Class valueClass = value.getClass();

		if (valueClass.isArray() == false) {
			// source is not an array
			return convertValueToArray(value);
		}

		// source is an array
		return convertArrayToArray(value);
	}

	/**
	 * Converts type using type converter manager.
	 */
	protected double convertType(Object value) {
		return typeConverterManagerBean.convertType(value, double.class).doubleValue();
	}

	/**
	 * Creates an array with single element.
	 */
	protected double[] convertToSingleElementArray(Object value) {
		return new double[] {convertType(value)};
	}

	/**
	 * Converts non-array value to array. Detects various
	 * collection types and iterates them to make conversion
	 * and to create target array.
 	 */
	protected double[] convertValueToArray(Object value) {
		if (value instanceof List) {
			List list = (List) value;
			double[] target = new double[list.size()];

			for (int i = 0; i < list.size(); i++) {
				Object element = list.get(i);
				target[i] = convertType(element);
			}

			return target;
		}

		if (value instanceof Collection) {
			Collection collection = (Collection) value;
			double[] target = new double[collection.size()];

			int i = 0;
			for (Object element : collection) {
				target[i] = convertType(element);
				i++;
			}

			return target;
		}

		if (value instanceof Iterable) {
			Iterable iterable = (Iterable) value;

			DoubleArrayList doubleArrayList = new DoubleArrayList();

			for (Object element : iterable) {
				double convertedValue = convertType(element);
				doubleArrayList.add(convertedValue);
            }

			return doubleArrayList.toArray();
		}

		if (value instanceof CharSequence) {
			String[] strings = StringUtil.splitc(value.toString(), ArrayConverter.NUMBER_DELIMITERS);
			return convertArrayToArray(strings);
		}

		// everything else:
		return convertToSingleElementArray(value);
	}

	/**
	 * Converts array value to array.
	 */
	protected double[] convertArrayToArray(Object value) {
		Class valueComponentType = value.getClass().getComponentType();

		if (valueComponentType == double.class) {
			// equal types, no conversion needed
			return (double[]) value;
		}

		double[] result;

		if (valueComponentType.isPrimitive()) {
			// convert primitive array to target array
			result = convertPrimitiveArrayToArray(value, valueComponentType);
		} else {
			// convert object array to target array
			Object[] array = (Object[]) value;
			result = new double[array.length];

			for (int i = 0; i < array.length; i++) {
				result[i] = convertType(array[i]);
			}
		}

		return result;
	}


	/**
	 * Converts primitive array to target array.
	 */
	protected double[] convertPrimitiveArrayToArray(Object value, Class primitiveComponentType) {
		double[] result = null;

		if (primitiveComponentType == double[].class) {
			return (double[]) value;
		}

		if (primitiveComponentType == int.class) {
			int[] array = (int[]) value;
			result = new double[array.length];
			for (int i = 0; i < array.length; i++) {
				result[i] = array[i];
			}
		}
		else if (primitiveComponentType == long.class) {
			long[] array = (long[]) value;
			result = new double[array.length];
			for (int i = 0; i < array.length; i++) {
				result[i] = array[i];
			}
		}
		else if (primitiveComponentType == float.class) {
			float[] array = (float[]) value;
			result = new double[array.length];
			for (int i = 0; i < array.length; i++) {
				result[i] = array[i];
			}
		}
		else if (primitiveComponentType == short.class) {
			short[] array = (short[]) value;
			result = new double[array.length];
			for (int i = 0; i < array.length; i++) {
				result[i] = array[i];
			}
		}
		else if (primitiveComponentType == byte.class) {
			byte[] array = (byte[]) value;
			result = new double[array.length];
			for (int i = 0; i < array.length; i++) {
				result[i] = array[i];
			}
		}
		else if (primitiveComponentType == char.class) {
			char[] array = (char[]) value;
			result = new double[array.length];
			for (int i = 0; i < array.length; i++) {
				result[i] = array[i];
			}
		}
		else if (primitiveComponentType == boolean.class) {
			boolean[] array = (boolean[]) value;
			result = new double[array.length];
			for (int i = 0; i < array.length; i++) {
				result[i] = array[i] ? 1 : 0;
			}
		}
		return result;
	}

}