// Copyright (c) 2003-2013, Jodd Team (jodd.org). All Rights Reserved.

package jodd.typeconverter.impl;

import jodd.typeconverter.TypeConverter;
import jodd.typeconverter.TypeConverterManagerBean;
import jodd.util.CsvUtil;

import java.util.Collection;
import java.util.List;

/**
 * Converts given object to <code>short[]</code>.
 */
public class ShortArrayConverter implements TypeConverter<short[]> {

	protected final TypeConverterManagerBean typeConverterManagerBean;

	public ShortArrayConverter(TypeConverterManagerBean typeConverterManagerBean) {
		this.typeConverterManagerBean = typeConverterManagerBean;
	}

	public short[] convert(Object value) {
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
	protected short convertType(Object value) {
		return typeConverterManagerBean.convertType(value, short.class).shortValue();
	}

	/**
	 * Creates an array with single element.
	 */
	protected short[] convertToSingleElementArray(Object value) {
		return new short[] {convertType(value)};
	}

	/**
	 * Converts non-array value to array. Detects various
	 * collection types and iterates them to make conversion
	 * and to create target array.
 	 */
	protected short[] convertValueToArray(Object value) {
		if (value instanceof List) {
			List list = (List) value;
			short[] target = new short[list.size()];

			for (int i = 0; i < list.size(); i++) {
				Object element = list.get(i);
				target[i] = convertType(element);
			}

			return target;
		}

		if (value instanceof Collection) {
			Collection collection = (Collection) value;
			short[] target = new short[collection.size()];

			int i = 0;
			for (Object element : collection) {
				target[i] = convertType(element);
				i++;
			}

			return target;
		}

		if (value instanceof Iterable) {
			Iterable iterable = (Iterable) value;

            int count = 0;
			for (Object element : iterable) {
				count++;
			}

			short[] target = new short[count];
			int i = 0;
			for (Object element : iterable) {
				target[i] = convertType(element);
            	i++;
            }

			return target;
		}

		if (value instanceof CharSequence) {
			String[] strings = CsvUtil.toStringArray(value.toString());
			return convertArrayToArray(strings);
		}

		// everything else:
		return convertToSingleElementArray(value);
	}

	/**
	 * Converts array value to array.
	 */
	protected short[] convertArrayToArray(Object value) {
		Class valueComponentType = value.getClass().getComponentType();

		if (valueComponentType == short.class) {
			// equal types, no conversion needed
			return (short[]) value;
		}

		short[] result;

		if (valueComponentType.isPrimitive()) {
			// convert primitive array to target array
			result = convertPrimitiveArrayToArray(value, valueComponentType);
		} else {
			// convert object array to target array
			Object[] array = (Object[]) value;
			result = new short[array.length];

			for (int i = 0; i < array.length; i++) {
				result[i] = convertType(array[i]);
			}
		}

		return result;
	}


	/**
	 * Converts primitive array to target array.
	 */
	protected short[] convertPrimitiveArrayToArray(Object value, Class primitiveComponentType) {
		short[] result = null;

		if (primitiveComponentType == short[].class) {
			return (short[]) value;
		}

		if (primitiveComponentType == int.class) {
			int[] array = (int[]) value;
			result = new short[array.length];
			for (int i = 0; i < array.length; i++) {
				result[i] = (short) array[i];
			}
		}
		else if (primitiveComponentType == long.class) {
			long[] array = (long[]) value;
			result = new short[array.length];
			for (int i = 0; i < array.length; i++) {
				result[i] = (short) array[i];
			}
		}
		else if (primitiveComponentType == float.class) {
			float[] array = (float[]) value;
			result = new short[array.length];
			for (int i = 0; i < array.length; i++) {
				result[i] = (short) array[i];
			}
		}
		else if (primitiveComponentType == double.class) {
			double[] array = (double[]) value;
			result = new short[array.length];
			for (int i = 0; i < array.length; i++) {
				result[i] = (short) array[i];
			}
		}
		else if (primitiveComponentType == byte.class) {
			byte[] array = (byte[]) value;
			result = new short[array.length];
			for (int i = 0; i < array.length; i++) {
				result[i] = array[i];
			}
		}
		else if (primitiveComponentType == char.class) {
			char[] array = (char[]) value;
			result = new short[array.length];
			for (int i = 0; i < array.length; i++) {
				result[i] = (short) array[i];
			}
		}
		else if (primitiveComponentType == boolean.class) {
			boolean[] array = (boolean[]) value;
			result = new short[array.length];
			for (int i = 0; i < array.length; i++) {
				result[i] = (short) (array[i] ? 1 : 0);
			}
		}
		return result;
	}

}