// Copyright (c) 2003-2013, Jodd Team (jodd.org). All Rights Reserved.

package jodd.typeconverter.impl;


import jodd.typeconverter.TypeConverter;
import jodd.typeconverter.TypeConverterManagerBean;
import jodd.util.CsvUtil;

import java.util.Collection;
import java.util.List;

/**
 * Converts given object to <code>char[]</code>.
 */
public class CharacterArrayConverter implements TypeConverter<char[]> {

	protected final TypeConverterManagerBean typeConverterManagerBean;

	public CharacterArrayConverter(TypeConverterManagerBean typeConverterManagerBean) {
		this.typeConverterManagerBean = typeConverterManagerBean;
	}

	public char[] convert(Object value) {
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
	protected char convertType(Object value) {
		return typeConverterManagerBean.convertType(value, char.class).charValue();
	}

	/**
	 * Creates an array with single element.
	 */
	protected char[] convertToSingleElementArray(Object value) {
		return new char[] {convertType(value)};
	}

	/**
	 * Converts non-array value to array. Detects various
	 * collection types and iterates them to make conversion
	 * and to create target array.
 	 */
	protected char[] convertValueToArray(Object value) {
		if (value instanceof List) {
			List list = (List) value;
			char[] target = new char[list.size()];

			for (int i = 0; i < list.size(); i++) {
				Object element = list.get(i);
				target[i] = convertType(element);
			}

			return target;
		}

		if (value instanceof Collection) {
			Collection collection = (Collection) value;
			char[] target = new char[collection.size()];

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

			char[] target = new char[count];
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
	protected char[] convertArrayToArray(Object value) {
		Class valueComponentType = value.getClass().getComponentType();

		if (valueComponentType == char.class) {
			// equal types, no conversion needed
			return (char[]) value;
		}

		char[] result;

		if (valueComponentType.isPrimitive()) {
			// convert primitive array to target array
			result = convertPrimitiveArrayToArray(value, valueComponentType);
		} else {
			// convert object array to target array
			Object[] array = (Object[]) value;
			result = new char[array.length];

			for (int i = 0; i < array.length; i++) {
				result[i] = convertType(array[i]);
			}
		}

		return result;
	}


	/**
	 * Converts primitive array to target array.
	 */
	protected char[] convertPrimitiveArrayToArray(Object value, Class primitiveComponentType) {
		char[] result = null;

		if (primitiveComponentType == char[].class) {
			return (char[]) value;
		}

		if (primitiveComponentType == int.class) {
			int[] array = (int[]) value;
			result = new char[array.length];
			for (int i = 0; i < array.length; i++) {
				result[i] = (char) array[i];
			}
		}
		else if (primitiveComponentType == long.class) {
			long[] array = (long[]) value;
			result = new char[array.length];
			for (int i = 0; i < array.length; i++) {
				result[i] = (char) array[i];
			}
		}
		else if (primitiveComponentType == float.class) {
			float[] array = (float[]) value;
			result = new char[array.length];
			for (int i = 0; i < array.length; i++) {
				result[i] = (char) array[i];
			}
		}
		else if (primitiveComponentType == double.class) {
			double[] array = (double[]) value;
			result = new char[array.length];
			for (int i = 0; i < array.length; i++) {
				result[i] = (char) array[i];
			}
		}
		else if (primitiveComponentType == short.class) {
			short[] array = (short[]) value;
			result = new char[array.length];
			for (int i = 0; i < array.length; i++) {
				result[i] = (char) array[i];
			}
		}
		else if (primitiveComponentType == byte.class) {
			byte[] array = (byte[]) value;
			result = new char[array.length];
			for (int i = 0; i < array.length; i++) {
				result[i] = (char) array[i];
			}
		}
		else if (primitiveComponentType == boolean.class) {
			boolean[] array = (boolean[]) value;
			result = new char[array.length];
			for (int i = 0; i < array.length; i++) {
				result[i] = (char) (array[i] ? 1 : 0);
			}
		}
		return result;
	}

}