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

package jodd.bean;

import jodd.introspector.ClassIntrospector;
import jodd.introspector.Getter;
import jodd.introspector.MapperFunction;
import jodd.introspector.Setter;
import jodd.typeconverter.TypeConverterManager;
import jodd.util.ClassUtil;

import java.lang.reflect.Array;
import java.util.Collection;
import java.util.List;

/**
 * Various bean property utilities that makes writings of {@link BeanUtil} classes easy.
 */
abstract class BeanUtilUtil implements BeanUtil {

	// ---------------------------------------------------------------- flags

	protected boolean isDeclared = false;
	protected boolean isForced = false;
	protected boolean isSilent = false;

	// ---------------------------------------------------------------- introspector

	protected ClassIntrospector introspector = ClassIntrospector.get();
	protected TypeConverterManager typeConverterManager = TypeConverterManager.get();

	/**
	 * Sets {@link ClassIntrospector introspector} implementation.
	 */
	public void setIntrospector(final ClassIntrospector introspector) {
		this.introspector = introspector;
	}

	/**
	 * Sets {@link TypeConverterManager type converter manager} implementation.
	 */
	public void setTypeConverterManager(final TypeConverterManager typeConverterManager) {
		this.typeConverterManager = typeConverterManager;
	}

	/**
	 * Converts object to destination type. Invoked before the
	 * value is set into destination. Throws <code>TypeConversionException</code>
	 * if conversion fails.
	 */
	@SuppressWarnings("unchecked")
	protected Object convertType(final Object value, final Class type) {
		return typeConverterManager.convertType(value, type);
	}

	/**
	 * Converter to collection.
	 */
	@SuppressWarnings("unchecked")
	protected Object convertToCollection(final Object value, final Class destinationType, final Class componentType) {
		return typeConverterManager.convertToCollection(value, destinationType, componentType);
	}

	
	// ---------------------------------------------------------------- accessors

	/**
	 * Invokes setter, but first converts type to match the setter type.
	 */
	protected Object invokeSetter(final Setter setter, final BeanProperty bp, Object value) {
		try {

			final MapperFunction setterMapperFunction = setter.getMapperFunction();

			if (setterMapperFunction != null) {
				value = setterMapperFunction.apply(value);
			}

			final Class type = setter.getSetterRawType();

			if (ClassUtil.isTypeOf(type, Collection.class)) {
				Class componentType = setter.getSetterRawComponentType();

				value = convertToCollection(value, type, componentType);
			} else {
				// no collections
				value = convertType(value, type);
			}

			setter.invokeSetter(bp.bean, value);
		} catch (Exception ex) {
			if (isSilent) {
				return null;
			}
			throw new BeanException("Setter failed: " + setter, ex);
		}
		return value;
	}

	// ---------------------------------------------------------------- forced

	/**
	 * Returns the element of an array forced. If value is <code>null</code>, it will be instantiated.
	 * If not the last part of indexed bean property, array will be expanded to the index if necessary.
	 */
	protected Object arrayForcedGet(final BeanProperty bp, Object array, final int index) {
		Class componentType = array.getClass().getComponentType();
		if (!bp.last) {
			array = ensureArraySize(bp, array, componentType, index);
		}
		Object value = Array.get(array, index);
		if (value == null) {
			try {
				//noinspection unchecked
				value = ClassUtil.newInstance(componentType);
			} catch (Exception ex) {
				if (isSilent) {
					return null;
				}
				throw new BeanException("Invalid array element: " + bp.name + '[' + index + ']', bp, ex);
			}
			Array.set(array, index, value);
		}
		return value;
	}

	/**
	 * Sets the array element forced. If index is greater then arrays length, array will be expanded to the index.
	 * If speed is critical, it is better to allocate an array with proper size before using this method. 
	 */
	protected void arrayForcedSet(final BeanProperty bp, Object array, final int index, Object value) {
		Class componentType = array.getClass().getComponentType();
		array = ensureArraySize(bp, array, componentType, index);
		value = convertType(value, componentType);
		Array.set(array, index, value);
	}


	@SuppressWarnings({"SuspiciousSystemArraycopy"})
	protected Object ensureArraySize(final BeanProperty bp, Object array, final Class componentType, final int index) {
		int len = Array.getLength(array);
		if (index >= len) {
			Object newArray = Array.newInstance(componentType, index + 1);
			System.arraycopy(array, 0, newArray, 0, len);

			Setter setter = bp.getSetter(true);
			if (setter == null) {
				// no point to check for bp.silent, throws NPE later
				throw new BeanException("Setter or field not found: " + bp.name, bp);
			}

			newArray = invokeSetter(setter, bp, newArray);

			array = newArray;
		}
		return array;
	}

	@SuppressWarnings({"unchecked"})
	protected void ensureListSize(final List list, final int size) {
		int len = list.size();
		while (size >= len) {
			list.add(null);
			len++;
		}
	}

	// ---------------------------------------------------------------- index

	/**
	 * Finds the very first next dot. Ignores dots between index brackets.
	 * Returns <code>-1</code> when dot is not found.
	 */
	protected int indexOfDot(final String name) {
		int ndx = 0;
		int len = name.length();

		boolean insideBracket = false;

		while (ndx < len) {
			char c = name.charAt(ndx);

			if (insideBracket) {
				if (c == ']') {
					insideBracket = false;
				}
			} else {
				if (c == '.') {
					return ndx;
				}
				if (c == '[') {
					insideBracket = true;
				}
			}
			ndx++;
		}
		return -1;
	}


	/**
	 * Extract index string from non-nested property name.
	 * If index is found, it is stripped from bean property name.
	 * If no index is found, it returns <code>null</code>.
	 */
	protected String extractIndex(final BeanProperty bp) {
		bp.index = null;
		String name = bp.name;
		int lastNdx = name.length() - 1;
		if (lastNdx < 0) {
			return null;
		}
		if (name.charAt(lastNdx) == ']') {
			int leftBracketNdx = name.lastIndexOf('[');
			if (leftBracketNdx != -1) {
				bp.setName(name.substring(0, leftBracketNdx));
				bp.index = name.substring(leftBracketNdx + 1, lastNdx);
				return bp.index;
			}
		}
		return null;
	}

	protected int parseInt(final String indexString, final BeanProperty bp) {
		try {
			return Integer.parseInt(indexString);
		} catch (NumberFormatException nfex) {
			// no point to use bp.silent, as will throw exception
			throw new BeanException("Invalid index: " + indexString, bp, nfex);
		}
	}

	// ---------------------------------------------------------------- create property

	/**
	 * Creates new instance for current property name through its setter.
	 * It uses default constructor!
	 */
	protected Object createBeanProperty(final BeanProperty bp) {
		Setter setter = bp.getSetter(true);
		if (setter == null) {
			return null;
		}

		Class type = setter.getSetterRawType();

		Object newInstance;
		try {
			newInstance = ClassUtil.newInstance(type);
		} catch (Exception ex) {
			if (isSilent) {
				return null;
			}
			throw new BeanException("Invalid property: " + bp.name, bp, ex);
		}

		newInstance = invokeSetter(setter, bp, newInstance);

		return newInstance;
	}

	// ---------------------------------------------------------------- generic and type

	/**
	 * Extracts generic component type of a property. Returns <code>Object.class</code>
	 * when property does not have component.
	 */
	protected Class extractGenericComponentType(final Getter getter) {
		Class componentType = null;

		if (getter != null) {
			componentType = getter.getGetterRawComponentType();
		}

		if (componentType == null) {
			componentType = Object.class;
		}
		return componentType;
	}

	/**
	 * Converts <b>Map</b> index to key type. If conversion fails, original value will be returned.
	 */
	protected Object convertIndexToMapKey(final Getter getter, final Object index) {
		Class indexType = null;

		if (getter != null) {
			indexType = getter.getGetterRawKeyComponentType();
		}

		// check if set
		if (indexType == null) {
			indexType = Object.class;	// marker for no generic type
		}

		if (indexType == Object.class) {
			return index;
		}

		try {
			return convertType(index, indexType);
		} catch (Exception ignore) {
			return index;
		}
	}

	/**
	 * Extracts type of current property.
	 */
	protected Class extractType(final BeanProperty bp) {
		Getter getter = bp.getGetter(isDeclared);
		if (getter != null) {
			if (bp.index != null) {
				Class type = getter.getGetterRawComponentType();
				return type == null ? Object.class : type;
			}
			return getter.getGetterRawType();
		}

		return null;	// this should not happens
	}

}