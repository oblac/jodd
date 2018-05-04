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

import jodd.typeconverter.TypeConversionException;
import jodd.typeconverter.TypeConverter;
import jodd.typeconverter.TypeConverterManager;
import jodd.util.CsvUtil;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Collection converter.
 */
public class CollectionConverter<T> implements TypeConverter<Collection<T>> {

	protected final TypeConverterManager typeConverterManager;
	protected final Class<? extends Collection> collectionType;
	protected final Class<T> targetComponentType;

	public CollectionConverter(
		final Class<? extends Collection> collectionType,
		final Class<T> targetComponentType) {
		this(TypeConverterManager.get(), collectionType, targetComponentType);
	}

	public CollectionConverter(
		final TypeConverterManager typeConverterManager,
		final Class<? extends Collection> collectionType,
		final Class<T> targetComponentType) {

		this.typeConverterManager = typeConverterManager;
		this.collectionType = collectionType;
		this.targetComponentType = targetComponentType;
	}

	@Override
	public Collection<T> convert(final Object value) {
		if (value == null) {
			return null;
		}

		if (!(value instanceof Collection)) {
			// source is not an array
	        return convertValueToCollection(value);
		}

		// source is a collection
		return convertCollectionToCollection((Collection)value);
	}

	/**
	 * Converts type using type converter manager.
	 */
	protected T convertType(final Object value) {
		return typeConverterManager.convertType(value, targetComponentType);
	}

	/**
	 * Creates new collection of target component type.
	 * Default implementation uses reflection to create
	 * an collection of target type. Override it for better performances.
	 */
	@SuppressWarnings("unchecked")
	protected Collection<T> createCollection(final int length) {
		if (collectionType.isInterface()) {
			if (collectionType == List.class) {
				if (length > 0) {
					return new ArrayList<>(length);
				} else {
					return new ArrayList<>();
				}
			}

			if (collectionType == Set.class) {
				if (length > 0) {
					return new HashSet<>(length);
				} else {
					return new HashSet<>();
				}
			}

			throw new TypeConversionException("Unknown collection: " + collectionType.getName());
		}
		if (length > 0) {
			try {
				Constructor<Collection<T>> ctor = (Constructor<Collection<T>>) collectionType.getConstructor(int.class);
				return ctor.newInstance(Integer.valueOf(length));
			} catch (Exception ex) {
				// ignore exception
			}
		}

		try {
			return collectionType.getDeclaredConstructor().newInstance();
		} catch (Exception ex) {
			throw new TypeConversionException(ex);
		}
	}

	/**
	 * Creates a collection with single element.
	 */
	protected Collection<T> convertToSingleElementCollection(final Object value) {
		Collection<T> collection = createCollection(0);

		//noinspection unchecked
		collection.add((T) value);

		return collection;
	}

	/**
	 * Converts non-collection value to collection.
 	 */
	protected Collection<T> convertValueToCollection(Object value) {
		if (value instanceof Iterable) {
			Iterable iterable = (Iterable) value;
			Collection<T> collection = createCollection(0);

			for (Object element : iterable) {
				collection.add(convertType(element));
			}
			return collection;
		}

		if (value instanceof CharSequence) {
			value = CsvUtil.toStringArray(value.toString());
		}

		Class type = value.getClass();

		if (type.isArray()) {
			// convert arrays
			Class componentType = type.getComponentType();

			if (componentType.isPrimitive()) {
				return convertPrimitiveArrayToCollection(value, componentType);
			} else {
				Object[] array = (Object[]) value;
				Collection<T> result = createCollection(array.length);
				for (Object a : array) {
					result.add(convertType(a));
				}
				return result;
			}
		}

		// everything else:
		return convertToSingleElementCollection(value);
	}

	/**
	 * Converts collection value to target collection.
	 * Each element is converted to target component type.
	 */
	protected Collection<T> convertCollectionToCollection(final Collection value) {
		Collection<T> collection = createCollection(value.size());

		for (Object v : value) {
			collection.add(convertType(v));
		}

		return collection;
	}

	/**
	 * Converts primitive array to target collection.
	 */
	@SuppressWarnings("AutoBoxing")
	protected Collection<T> convertPrimitiveArrayToCollection(final Object value, final Class primitiveComponentType) {
		Collection<T> result = null;

		if (primitiveComponentType == int.class) {
			int[] array = (int[]) value;
			result = createCollection(array.length);
			for (int a : array) {
				result.add(convertType(a));
			}
		}
		else if (primitiveComponentType == long.class) {
			long[] array = (long[]) value;
			result = createCollection(array.length);
			for (long a : array) {
				result.add(convertType(a));
			}
		}
		else if (primitiveComponentType == float.class) {
			float[] array = (float[]) value;
			result = createCollection(array.length);
			for (float a : array) {
				result.add(convertType(a));
			}
		}
		else if (primitiveComponentType == double.class) {
			double[] array = (double[]) value;
			result = createCollection(array.length);
			for (double a : array) {
				result.add(convertType(a));
			}
		}
		else if (primitiveComponentType == short.class) {
			short[] array = (short[]) value;
			result = createCollection(array.length);
			for (short a : array) {
				result.add(convertType(a));
			}
		}
		else if (primitiveComponentType == byte.class) {
			byte[] array = (byte[]) value;
			result = createCollection(array.length);
			for (byte a : array) {
				result.add(convertType(a));
			}
		}
		else if (primitiveComponentType == char.class) {
			char[] array = (char[]) value;
			result = createCollection(array.length);
			for (char a : array) {
				result.add(convertType(a));
			}
		}
		else if (primitiveComponentType == boolean.class) {
			boolean[] array = (boolean[]) value;
			result = createCollection(array.length);
			for (boolean a : array) {
				result.add(convertType(a));
			}
		}
		return result;
	}

}