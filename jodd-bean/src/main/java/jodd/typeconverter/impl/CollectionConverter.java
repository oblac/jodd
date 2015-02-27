// Copyright (c) 2003-present, Jodd Team (jodd.org). All Rights Reserved.

package jodd.typeconverter.impl;

import jodd.typeconverter.TypeConversionException;
import jodd.typeconverter.TypeConverter;
import jodd.typeconverter.TypeConverterManager;
import jodd.typeconverter.TypeConverterManagerBean;
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

	protected final TypeConverterManagerBean typeConverterManagerBean;
	protected final Class<? extends Collection> collectionType;
	protected final Class<T> targetComponentType;

	public CollectionConverter(
			Class<? extends Collection> collectionType,
			Class<T> targetComponentType) {
		this(TypeConverterManager.getDefaultTypeConverterManager(), collectionType, targetComponentType);
	}

	public CollectionConverter(
			TypeConverterManagerBean typeConverterManagerBean,
			Class<? extends Collection> collectionType,
			Class<T> targetComponentType) {

		this.typeConverterManagerBean = typeConverterManagerBean;
		this.collectionType = collectionType;
		this.targetComponentType = targetComponentType;
	}

	public Collection<T> convert(Object value) {
		if (value == null) {
			return null;
		}

		if (value instanceof Collection == false) {
			// source is not an array
	        return convertValueToCollection(value);
		}

		// source is a collection
		return convertCollectionToCollection((Collection)value);
	}

	/**
	 * Converts type using type converter manager.
	 */
	protected T convertType(Object value) {
		return typeConverterManagerBean.convertType(value, targetComponentType);
	}

	/**
	 * Creates new collection of target component type.
	 * Default implementation uses reflection to create
	 * an collection of target type. Override it for better performances.
	 */
	@SuppressWarnings("unchecked")
	protected Collection<T> createCollection(int length) {
		if (collectionType.isInterface()) {
			if (collectionType == List.class) {
				if (length > 0) {
					return new ArrayList<T>(length);
				} else {
					return new ArrayList<T>();
				}
			}

			if (collectionType == Set.class) {
				if (length > 0) {
					return new HashSet<T>(length);
				} else {
					return new HashSet<T>();
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
			return collectionType.newInstance();
		} catch (Exception ex) {
			throw new TypeConversionException(ex);
		}
	}

	/**
	 * Creates a collection with single element.
	 */
	protected Collection<T> convertToSingleElementCollection(Object value) {
		Collection<T> collection = createCollection(0);

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
	protected Collection<T> convertCollectionToCollection(Collection value) {
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
	protected Collection<T> convertPrimitiveArrayToCollection(Object value, Class primitiveComponentType) {
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