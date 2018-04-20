// Copyright (c) 2003-present, Jodd Team (http://jodd.org)
// All rights reserved.
//
// Redistribution and use in source and binary forms, with or without
// modification, are permitted provided that the following conditions are met:
//
// 1. Redistributions of source code must retain the above copyright notice,
// this list of conditions and the following disclaimer.
//
// 2. Redistributions in binary form must reproduce the above copyright
// notice, this list of conditions and the following disclaimer in the
// documentation and/or other materials provided with the distribution.
//
// THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
// AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
// IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
// ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE
// LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
// CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
// SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
// INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
// CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
// ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
// POSSIBILITY OF SUCH DAMAGE.

package jodd.typeconverter.impl;

import jodd.typeconverter.TypeConverter;
import jodd.typeconverter.TypeConverterManager;
import jodd.util.CsvUtil;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Converts given object to an array. This converter is specific, as it
 * is not directly registered to a type; but created when needed.
 * Conversion rules:
 * <ul>
 * <li><code>null</code> value is returned as <code>null</code></li>
 * <li>source non-array value is checked for <code>Collections</code></li>
 * <li>if non-array element can't be resolved, it is converted to single element array</li>
 * <li>source array is converted to target array, by converting each element</li>
 * </ul>
 */
@SuppressWarnings("unchecked")
public class ArrayConverter<T> implements TypeConverter<T[]> {

	public static final char[] NUMBER_DELIMITERS = new char[] {',', ';', '\n'};

	protected final TypeConverterManager typeConverterManager;
	protected final Class<T> targetComponentType;

	public ArrayConverter(final TypeConverterManager typeConverterManager, final Class<T> targetComponentType) {
		this.typeConverterManager = typeConverterManager;
		this.targetComponentType = targetComponentType;
	}

	@Override
	public T[] convert(final Object value) {
		if (value == null) {
			return null;
		}

		Class valueClass = value.getClass();

		if (!valueClass.isArray()) {
			// source is not an array
	        return convertValueToArray(value);
		}

		// source is an array
		return convertArrayToArray(value);
	}

	/**
	 * Converts type using type converter manager.
	 */
	protected T convertType(final Object value) {
		return typeConverterManager.convertType(value, targetComponentType);
	}

	/**
	 * Creates new array of target component type.
	 * Default implementation uses reflection to create
	 * an array of target type. Override it for better performances.
	 */
	protected T[] createArray(final int length) {
		return (T[]) Array.newInstance(targetComponentType, length);
	}

	/**
	 * Creates an array with single element.
	 */
	protected T[] convertToSingleElementArray(final Object value) {
		T[] singleElementArray = createArray(1);

		singleElementArray[0] = convertType(value);

		return singleElementArray;
	}

	/**
	 * Converts non-array value to array. Detects various
	 * collection types and iterates them to make conversion
	 * and to create target array.
 	 */
	protected T[] convertValueToArray(final Object value) {
		if (value instanceof Collection) {
			Collection collection = (Collection) value;
			T[] target = createArray(collection.size());

			int i = 0;
			for (Object element : collection) {
				target[i] = convertType(element);
				i++;
			}

			return target;
		}

		if (value instanceof Iterable) {
			Iterable iterable = (Iterable) value;
			List<T> list = new ArrayList<>();

			for (Object element : iterable) {
				list.add(convertType(element));
			}

			T[] target = createArray(list.size());
			return list.toArray(target);
		}

		if (value instanceof CharSequence) {
			String[] strings = convertStringToArray(value.toString());
			return convertArrayToArray(strings);
		}

		// everything else:
		return convertToSingleElementArray(value);
	}

	/**
	 * Converts string to array, for the {@link #convertValueToArray(Object)} method.
	 * By default, the string is converted into an array using {@link jodd.util.CsvUtil}.
	 */
	protected String[] convertStringToArray(final String value) {
		return CsvUtil.toStringArray(value);
	}

	/**
	 * Converts array value to array.
	 */
	protected T[] convertArrayToArray(final Object value) {
		Class valueComponentType = value.getClass().getComponentType();

		if (valueComponentType == targetComponentType) {
			// equal types, no conversion needed
			return (T[]) value;
		}

		T[] result;

		if (valueComponentType.isPrimitive()) {
			// convert primitive array to target array
			result = convertPrimitiveArrayToArray(value, valueComponentType);
		} else {
			// convert object array to target array
			Object[] array = (Object[]) value;
			result = createArray(array.length);

			for (int i = 0; i < array.length; i++) {
				result[i] = convertType(array[i]);
			}
		}

		return result;
	}

	/**
	 * Converts primitive array to target array.
	 */
	@SuppressWarnings("AutoBoxing")
	protected T[] convertPrimitiveArrayToArray(final Object value, final Class primitiveComponentType) {
		T[] result = null;

		if (primitiveComponentType == int.class) {
			int[] array = (int[]) value;
			result = createArray(array.length);
			for (int i = 0; i < array.length; i++) {
				result[i] = convertType(array[i]);
			}
		}
		else if (primitiveComponentType == long.class) {
			long[] array = (long[]) value;
			result = createArray(array.length);
			for (int i = 0; i < array.length; i++) {
				result[i] = convertType(array[i]);
			}
		}
		else if (primitiveComponentType == float.class) {
			float[] array = (float[]) value;
			result = createArray(array.length);
			for (int i = 0; i < array.length; i++) {
				result[i] = convertType(array[i]);
			}
		}
		else if (primitiveComponentType == double.class) {
			double[] array = (double[]) value;
			result = createArray(array.length);
			for (int i = 0; i < array.length; i++) {
				result[i] = convertType(array[i]);
			}
		}
		else if (primitiveComponentType == short.class) {
			short[] array = (short[]) value;
			result = createArray(array.length);
			for (int i = 0; i < array.length; i++) {
				result[i] = convertType(array[i]);
			}
		}
		else if (primitiveComponentType == byte.class) {
			byte[] array = (byte[]) value;
			result = createArray(array.length);
			for (int i = 0; i < array.length; i++) {
				result[i] = convertType(array[i]);
			}
		}
		else if (primitiveComponentType == char.class) {
			char[] array = (char[]) value;
			result = createArray(array.length);
			for (int i = 0; i < array.length; i++) {
				result[i] = convertType(array[i]);
			}
		}
		else if (primitiveComponentType == boolean.class) {
			boolean[] array = (boolean[]) value;
			result = createArray(array.length);
			for (int i = 0; i < array.length; i++) {
				result[i] = convertType(array[i]);
			}
		}
		return result;
	}

}