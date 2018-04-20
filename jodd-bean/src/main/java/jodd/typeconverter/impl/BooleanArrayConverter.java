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
import jodd.util.StringUtil;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Converts given object to <code>boolean[]</code>.
 */
public class BooleanArrayConverter implements TypeConverter<boolean[]> {

	protected final TypeConverterManager typeConverterManager;

	public BooleanArrayConverter(final TypeConverterManager typeConverterManager) {
		this.typeConverterManager = typeConverterManager;
	}

	@Override
	public boolean[] convert(final Object value) {
		if (value == null) {
			return null;
		}

		final Class valueClass = value.getClass();

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
	protected boolean convertType(final Object value) {
		return typeConverterManager.convertType(value, boolean.class).booleanValue();
	}

	/**
	 * Creates an array with single element.
	 */
	protected boolean[] convertToSingleElementArray(final Object value) {
		return new boolean[] {convertType(value)};
	}

	/**
	 * Converts non-array value to array. Detects various
	 * collection types and iterates them to make conversion
	 * and to create target array.
 	 */
	protected boolean[] convertValueToArray(final Object value) {
		if (value instanceof Collection) {
			final Collection collection = (Collection) value;
			final boolean[] target = new boolean[collection.size()];

			int i = 0;
			for (final Object element : collection) {
				target[i] = convertType(element);
				i++;
			}
			return target;
		}

		if (value instanceof Iterable) {
			final Iterable iterable = (Iterable) value;

			final ArrayList<Boolean> booleanArrayList = new ArrayList<>();

			for (final Object element : iterable) {
				final boolean convertedValue = convertType(element);
				booleanArrayList.add(Boolean.valueOf(convertedValue));
            }
			
			final boolean[] array = new boolean[booleanArrayList.size()];

			for (int i = 0; i < booleanArrayList.size(); i++) {
				final Boolean b = booleanArrayList.get(i);
				array[i] = b.booleanValue();
			}
			
			return array;
		}

		if (value instanceof CharSequence) {
			final String[] strings = StringUtil.splitc(value.toString(), ArrayConverter.NUMBER_DELIMITERS);
			return convertArrayToArray(strings);
		}

		// everything else:
		return convertToSingleElementArray(value);
	}

	/**
	 * Converts array value to array.
	 */
	protected boolean[] convertArrayToArray(final Object value) {
		final Class valueComponentType = value.getClass().getComponentType();

		final boolean[] result;

		if (valueComponentType.isPrimitive()) {
			result = convertPrimitiveArrayToArray(value, valueComponentType);
		} else {
			// convert object array to target array
			final Object[] array = (Object[]) value;
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
	protected boolean[] convertPrimitiveArrayToArray(final Object value, final Class primitiveComponentType) {
		boolean[] result = null;

		if (primitiveComponentType == boolean.class) {
			return (boolean[]) value;
		}

		if (primitiveComponentType == int.class) {
			final int[] array = (int[]) value;
			result = new boolean[array.length];
			for (int i = 0; i < array.length; i++) {
				result[i] = array[i] != 0;
			}
		}
		else if (primitiveComponentType == long.class) {
			final long[] array = (long[]) value;
			result = new boolean[array.length];
			for (int i = 0; i < array.length; i++) {
				result[i] = array[i] != 0;
			}
		}
		else if (primitiveComponentType == float.class) {
			final float[] array = (float[]) value;
			result = new boolean[array.length];
			for (int i = 0; i < array.length; i++) {
				result[i] = array[i] != 0;
			}
		}
		else if (primitiveComponentType == double.class) {
			final double[] array = (double[]) value;
			result = new boolean[array.length];
			for (int i = 0; i < array.length; i++) {
				result[i] = array[i] != 0;
			}
		}
		else if (primitiveComponentType == short.class) {
			final short[] array = (short[]) value;
			result = new boolean[array.length];
			for (int i = 0; i < array.length; i++) {
				result[i] = array[i] != 0;
			}
		}
		else if (primitiveComponentType == byte.class) {
			final byte[] array = (byte[]) value;
			result = new boolean[array.length];
			for (int i = 0; i < array.length; i++) {
				result[i] = array[i] != 0;
			}
		}
		else if (primitiveComponentType == char.class) {
			final char[] array = (char[]) value;
			result = new boolean[array.length];
			for (int i = 0; i < array.length; i++) {
				result[i] = array[i] != 0;
			}
		}
		return result;
	}

}