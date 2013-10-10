// Copyright (c) 2003-2013, Jodd Team (jodd.org). All Rights Reserved.

package jodd.typeconverter.impl;

import jodd.typeconverter.TypeConverter;
import jodd.typeconverter.TypeConverterManagerBean;
import jodd.util.CsvUtil;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;

/**
 * Converts given object to <code>boolean[]</code>.
 */
public class BooleanArrayConverter implements TypeConverter<boolean[]> {

	protected final TypeConverterManagerBean typeConverterManagerBean;

	public BooleanArrayConverter(TypeConverterManagerBean typeConverterManagerBean) {
		this.typeConverterManagerBean = typeConverterManagerBean;
	}

	public boolean[] convert(Object value) {
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
	protected boolean convertType(Object value) {
		return typeConverterManagerBean.convertType(value, boolean.class).booleanValue();
	}

	/**
	 * Creates an array with single element.
	 */
	protected boolean[] convertToSingleElementArray(Object value) {
		return new boolean[] {convertType(value)};
	}

	/**
	 * Converts non-array value to array. Detects various
	 * collection types and iterates them to make conversion
	 * and to create target array.
 	 */
	protected boolean[] convertValueToArray(Object value) {
		if (value instanceof List) {
			List list = (List) value;
			boolean[] target = new boolean[list.size()];

			for (int i = 0; i < list.size(); i++) {
				Object element = list.get(i);
				target[i] = convertType(element);
			}
			return target;
		}

		if (value instanceof Collection) {
			Collection collection = (Collection) value;
			boolean[] target = new boolean[collection.size()];

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
			Iterator iterator = iterable.iterator();
			while (iterator.hasNext()) {
				iterator.next();
				count++;
			}

			boolean[] target = new boolean[count];
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
	protected boolean[] convertArrayToArray(Object value) {
		Class valueComponentType = value.getClass().getComponentType();

		if (valueComponentType == boolean.class) {
			// equal types, no conversion needed
			return (boolean[]) value;
		}

		boolean[] result;

		if (valueComponentType.isPrimitive()) {
			// convert primitive array to target array
			result = convertPrimitiveArrayToArray(value, valueComponentType);
		} else {
			// convert object array to target array
			Object[] array = (Object[]) value;
			result = new boolean[array.length];

			for (int i = 0; i < array.length; i++) {
				result[i] = convertType(array[i]);
			}
		}

		return result;
	}


	/**
	 * Converts primitive array to target array.
	 */
	protected boolean[] convertPrimitiveArrayToArray(Object value, Class primitiveComponentType) {
		boolean[] result = null;

		if (primitiveComponentType == boolean[].class) {
			return (boolean[]) value;
		}

		if (primitiveComponentType == int.class) {
			int[] array = (int[]) value;
			result = new boolean[array.length];
			for (int i = 0; i < array.length; i++) {
				result[i] = array[i] != 0;
			}
		}
		else if (primitiveComponentType == long.class) {
			long[] array = (long[]) value;
			result = new boolean[array.length];
			for (int i = 0; i < array.length; i++) {
				result[i] = array[i] != 0;
			}
		}
		else if (primitiveComponentType == float.class) {
			float[] array = (float[]) value;
			result = new boolean[array.length];
			for (int i = 0; i < array.length; i++) {
				result[i] = array[i] != 0;
			}
		}
		else if (primitiveComponentType == double.class) {
			double[] array = (double[]) value;
			result = new boolean[array.length];
			for (int i = 0; i < array.length; i++) {
				result[i] = array[i] != 0;
			}
		}
		else if (primitiveComponentType == short.class) {
			short[] array = (short[]) value;
			result = new boolean[array.length];
			for (int i = 0; i < array.length; i++) {
				result[i] = array[i] != 0;
			}
		}
		else if (primitiveComponentType == byte.class) {
			byte[] array = (byte[]) value;
			result = new boolean[array.length];
			for (int i = 0; i < array.length; i++) {
				result[i] = array[i] != 0;
			}
		}
		else if (primitiveComponentType == char.class) {
			char[] array = (char[]) value;
			result = new boolean[array.length];
			for (int i = 0; i < array.length; i++) {
				result[i] = array[i] != 0;
			}
		}
		return result;
	}

}