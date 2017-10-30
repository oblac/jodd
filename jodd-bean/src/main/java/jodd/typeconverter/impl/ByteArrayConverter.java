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

import jodd.io.FileUtil;
import jodd.typeconverter.TypeConversionException;
import jodd.typeconverter.TypeConverter;
import jodd.typeconverter.TypeConverterManager;
import jodd.util.StringUtil;

import java.io.File;
import java.io.IOException;
import java.sql.Blob;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Converts given object to <code>byte[]</code>.
 */
public class ByteArrayConverter implements TypeConverter<byte[]> {

	protected final TypeConverterManager typeConverterManager;

	public ByteArrayConverter(TypeConverterManager typeConverterManager) {
		this.typeConverterManager = typeConverterManager;
	}

	@Override
	public byte[] convert(Object value) {
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
	protected byte convertType(Object value) {
		return typeConverterManager.convertType(value, byte.class).byteValue();
	}

	/**
	 * Creates an array with single element.
	 */
	protected byte[] convertToSingleElementArray(Object value) {
		return new byte[] {convertType(value)};
	}

	/**
	 * Converts non-array value to array. Detects various
	 * types and collections, iterates them to make conversion
	 * and to create target array.
 	 */
	protected byte[] convertValueToArray(Object value) {
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

		if (value instanceof File) {
			try {
				return FileUtil.readBytes((File) value);
			} catch (IOException ioex) {
				throw new TypeConversionException(value, ioex);
			}
		}

		if (value instanceof List) {
			List list = (List) value;
			byte[] target = new byte[list.size()];

			for (int i = 0; i < list.size(); i++) {
				Object element = list.get(i);
				target[i] = convertType(element);
			}

			return target;
		}

		if (value instanceof Collection) {
			Collection collection = (Collection) value;
			byte[] target = new byte[collection.size()];

			int i = 0;
			for (Object element : collection) {
				target[i] = convertType(element);
				i++;
			}

			return target;
		}

		if (value instanceof Iterable) {
			Iterable iterable = (Iterable) value;

			ArrayList<Byte> byteArrayList = new ArrayList<>();

			for (Object element : iterable) {
				byte convertedValue = convertType(element);
				byteArrayList.add(Byte.valueOf(convertedValue));
			}

			byte[] array = new byte[byteArrayList.size()];

			for (int i = 0; i < byteArrayList.size(); i++) {
				Byte b = byteArrayList.get(i);
				array[i] = b.byteValue();
			}

			return array;
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
	protected byte[] convertArrayToArray(Object value) {
		Class valueComponentType = value.getClass().getComponentType();

		if (valueComponentType == byte.class) {
			// equal types, no conversion needed
			return (byte[]) value;
		}

		byte[] result;

		if (valueComponentType.isPrimitive()) {
			// convert primitive array to target array
			result = convertPrimitiveArrayToArray(value, valueComponentType);
		} else {
			// convert object array to target array
			Object[] array = (Object[]) value;
			result = new byte[array.length];

			for (int i = 0; i < array.length; i++) {
				result[i] = convertType(array[i]);
			}
		}

		return result;
	}


	/**
	 * Converts primitive array to target array.
	 */
	protected byte[] convertPrimitiveArrayToArray(Object value, Class primitiveComponentType) {
		byte[] result = null;

		if (primitiveComponentType == byte[].class) {
			return (byte[]) value;
		}

		if (primitiveComponentType == int.class) {
			int[] array = (int[]) value;
			result = new byte[array.length];
			for (int i = 0; i < array.length; i++) {
				result[i] = (byte) array[i];
			}
		}
		else if (primitiveComponentType == long.class) {
			long[] array = (long[]) value;
			result = new byte[array.length];
			for (int i = 0; i < array.length; i++) {
				result[i] = (byte) array[i];
			}
		}
		else if (primitiveComponentType == float.class) {
			float[] array = (float[]) value;
			result = new byte[array.length];
			for (int i = 0; i < array.length; i++) {
				result[i] = (byte) array[i];
			}
		}
		else if (primitiveComponentType == double.class) {
			double[] array = (double[]) value;
			result = new byte[array.length];
			for (int i = 0; i < array.length; i++) {
				result[i] = (byte) array[i];
			}
		}
		else if (primitiveComponentType == short.class) {
			short[] array = (short[]) value;
			result = new byte[array.length];
			for (int i = 0; i < array.length; i++) {
				result[i] = (byte) array[i];
			}
		}
		else if (primitiveComponentType == char.class) {
			char[] array = (char[]) value;
			result = new byte[array.length];
			for (int i = 0; i < array.length; i++) {
				result[i] = (byte) array[i];
			}
		}
		else if (primitiveComponentType == boolean.class) {
			boolean[] array = (boolean[]) value;
			result = new byte[array.length];
			for (int i = 0; i < array.length; i++) {
				result[i] = (byte) (array[i] ? 1 : 0);
			}
		}
		return result;
	}

}