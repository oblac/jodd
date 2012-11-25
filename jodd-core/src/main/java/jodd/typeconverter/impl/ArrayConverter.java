package jodd.typeconverter.impl;

import jodd.typeconverter.TypeConverter;
import jodd.typeconverter.TypeConverterManagerBean;

import java.lang.reflect.Array;

/**
 * Converts given object to an array. This converter is specific, as it
 * is not directly registered to a type; but created when needed.
 * Conversion rules:
 * <ul>
 * <li><code>null</code> value is returned as <code>null</code></li>
 * <li>source non-array value is created to single element array</li>
 * <li>source array is converted to target array, by converting each element</li>
 * </ul>
 */
public class ArrayConverter<T> implements TypeConverter<T[]> {

	protected final TypeConverterManagerBean typeConverterManagerBean;
	protected final Class<T> targetComponentType;

	public ArrayConverter(TypeConverterManagerBean typeConverterManagerBean, Class<T> targetComponentType) {
		this.typeConverterManagerBean = typeConverterManagerBean;
		this.targetComponentType = targetComponentType;
	}

	public T[] convert(Object value) {
		if (value == null) {
			return null;
		}

		Class valueClass = value.getClass();

		// source value itself is not an array
		if (valueClass.isArray() == false) {
			return convertToSingleElementArray(value);
		}

		return convertArray(value);
	}

	/**
	 * Converts type using type converter manager.
	 */
	protected T convertType(Object value) {
		return typeConverterManagerBean.convertType(value, targetComponentType);
	}

	/**
	 * Creates an array with single element.
	 */
	@SuppressWarnings("unchecked")
	protected T[] convertToSingleElementArray(Object value) {
		Object singleElementArray = Array.newInstance(targetComponentType, 1);

		Array.set(singleElementArray, 0, convertType(value));

		return (T[]) singleElementArray;
	}

	/**
	 * Converts array value.
	 */
	@SuppressWarnings("unchecked")
	protected T[] convertArray(Object value) {
		Class valueComponentType = value.getClass().getComponentType();

		if (targetComponentType == valueComponentType) {
			return (T[]) value;
		}

		Object result;

		if (valueComponentType.isPrimitive()) {
			if (valueComponentType == int.class) {
				int[] array = (int[]) value;
				T[] objArray = (T[]) Array.newInstance(targetComponentType, array.length);
				for (int i = 0; i < array.length; i++) {
					objArray[i] = convertType(array[i]);
				}
				result = objArray;
			}
			else if (valueComponentType == long.class) {
				long[] array = (long[]) value;
				T[] objArray = (T[]) Array.newInstance(targetComponentType, array.length);
				for (int i = 0; i < array.length; i++) {
					objArray[i] = convertType(array[i]);
				}
				result = objArray;
			}
			else if (valueComponentType == float.class) {
				float[] array = (float[]) value;
				T[] objArray = (T[]) Array.newInstance(targetComponentType, array.length);
				for (int i = 0; i < array.length; i++) {
					objArray[i] = convertType(array[i]);
				}
				result = objArray;
			}
			else if (valueComponentType == double.class) {
				double[] array = (double[]) value;
				T[] objArray = (T[]) Array.newInstance(targetComponentType, array.length);
				for (int i = 0; i < array.length; i++) {
					objArray[i] = convertType(array[i]);
				}
				result = objArray;
			}
			else if (valueComponentType == short.class) {
				short[] array = (short[]) value;
				T[] objArray = (T[]) Array.newInstance(targetComponentType, array.length);
				for (int i = 0; i < array.length; i++) {
					objArray[i] = convertType(array[i]);
				}
				result = objArray;
			}
			else if (valueComponentType == byte.class) {
				byte[] array = (byte[]) value;
				T[] objArray = (T[]) Array.newInstance(targetComponentType, array.length);
				for (int i = 0; i < array.length; i++) {
					objArray[i] = convertType(array[i]);
				}
				result = objArray;
			}
			else if (valueComponentType == char.class) {
				char[] array = (char[]) value;
				T[] objArray = (T[]) Array.newInstance(targetComponentType, array.length);
				for (int i = 0; i < array.length; i++) {
					objArray[i] = convertType(array[i]);
				}
				result = objArray;
			}
			else if (valueComponentType == boolean.class) {
				boolean [] array = (boolean[]) value;
				T[] objArray = (T[]) Array.newInstance(targetComponentType, array.length);
				for (int i = 0; i < array.length; i++) {
					objArray[i] = convertType(array[i]);
				}
				result = objArray;
			}
			else {
				throw new IllegalArgumentException();
			}
		} else {
			Object[] array = (Object[]) value;
			T[] objArray = (T[]) Array.newInstance(targetComponentType, array.length);
			for (int i = 0; i < array.length; i++) {
				objArray[i] = convertType(array[i]);
			}
			result = objArray;
		}
		return (T[]) result;
	}

}
