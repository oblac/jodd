// Copyright (c) 2003-2013, Jodd Team (jodd.org). All Rights Reserved.

package jodd.bean;

import jodd.JoddBean;
import jodd.introspector.FieldDescriptor;
import jodd.introspector.MethodDescriptor;
import jodd.typeconverter.TypeConverterManager;
import jodd.typeconverter.TypeConverterManagerBean;
import jodd.util.ReflectUtil;

import java.lang.reflect.Array;
import java.util.List;

/**
 * Various bean property utilities that makes writings of {@link BeanUtil} classes easy.
 */
class BeanUtilUtil {

	protected TypeConverterManagerBean typeConverterManager = TypeConverterManager.getDefaultTypeConverterManager();

	/**
	 * Sets custom {@link TypeConverterManagerBean type converter manager}.
	 */
	public void setTypeConverterManager(TypeConverterManagerBean typeConverterManager) {
		this.typeConverterManager = typeConverterManager;
	}

	/**
	 * Converts object to destination type. Invoked before the
	 * value is set into destination. Throws <code>ClassCastException</code>
	 * if conversion fails.
	 */
	@SuppressWarnings("unchecked")
	protected Object convertType(Object value, Class type) {
		return typeConverterManager.convertType(value, type);
	}

	
	// ---------------------------------------------------------------- accessors

	/**
	 * Invokes <code>setXxx()</code> method with appropriate conversion if available.
	 * It is assumed that all provided arguments are valid.
	 */
	protected void invokeSetter(Object bean, MethodDescriptor md, Object value) {
		try {
			Class[] paramTypes = md.getRawParameterTypes();

			value = convertType(value, paramTypes[0]);

			md.invoke(bean, value);
		} catch (Exception ex) {
			throw new BeanException("Unable to invoke setter: " +
					bean.getClass().getSimpleName() + '#' + md.getMethod().getName() + "()", ex);
		}
	}

	/**
	 * Invokes <code>getXxx()</code> method of specified bean.
	 * It is assumed that all provided arguments are valid.
	 */
	protected Object invokeGetter(Object bean, MethodDescriptor md) {
		try {
			return md.invoke(bean);
		} catch (Exception ex) {
			throw new BeanException("Unable to invoke getter: " +
					bean.getClass().getSimpleName() + '#' + md.getMethod().getName() + "()", ex);
		}
	}

	/**
	 * Sets field value.
	 */
	protected void setFieldValue(Object bean, FieldDescriptor fd, Object value) {
		try {
			Class type = fd.getRawType();

			value = convertType(value, type);

			fd.getField().set(bean, value);
		} catch (Exception ex) {
			throw new BeanException("Unable to set field: " +
					bean.getClass().getSimpleName() + '#' + fd.getField().getName(), ex);
		}
	}

	/**
	 * Return value of a field.
	 */
	protected Object getFieldValue(Object bean, FieldDescriptor fd) {
		try {
			return fd.getField().get(bean);
		} catch (Exception ex) {
			throw new BeanException("Unable to get field " +
					bean.getClass().getSimpleName() + '#' + fd.getField().getName(), ex);
		}
	}

	// ---------------------------------------------------------------- forced

	/**
	 * Returns the element of an array forced. If value is <code>null</code>, it will be instantiated.
	 * If not the last part of indexed bean property, array will be expanded to the index if necessary.
	 */
	protected Object arrayForcedGet(BeanProperty bp, Object array, int index) {
		Class componentType = array.getClass().getComponentType();
		if (bp.last == false) {
			array = ensureArraySize(bp, array, componentType, index);
		}
		Object value = Array.get(array, index);
		if (value == null) {
			try {
				value = ReflectUtil.newInstance(componentType);
			} catch (Exception ex) {
				throw new BeanException("Unable to create array element: " + bp.name + '[' + index + ']', bp, ex);
			}
			Array.set(array, index, value);
		}
		return value;
	}

	/**
	 * Sets the array element forced. If index is greater then arrays length, array will be expanded to the index.
	 * If speed is critical, it is better to allocate an array with proper size before using this method. 
	 */
	protected void arrayForcedSet(BeanProperty bp, Object array, int index, Object value) {
		Class componentType = array.getClass().getComponentType();
		array = ensureArraySize(bp, array, componentType, index);
		value = convertType(value, componentType);
		Array.set(array, index, value);
	}


	@SuppressWarnings({"SuspiciousSystemArraycopy"})
	protected Object ensureArraySize(BeanProperty bp, Object array, Class componentType, int index) {
		int len = Array.getLength(array);
		if (index >= len) {
			Object newArray = Array.newInstance(componentType, index + 1);
			System.arraycopy(array, 0, newArray, 0, len);
			MethodDescriptor setter = bp.cd.getBeanSetterMethodDescriptor(bp.name, true);
			if (setter != null) {
				invokeSetter(bp.bean, setter, newArray);
			} else {
				FieldDescriptor field = getField(bp, true);
				if (field == null) {
					throw new BeanException("Unable to find setter or field named as: " + bp.name, bp);
				}
				setFieldValue(bp.bean, field, newArray);
			}
			array = newArray;
		}
		return array;
	}

	@SuppressWarnings({"unchecked"})
	protected void ensureListSize(List list, int size) {
		int len = list.size();
		while (size >= len) {
			list.add(null);
			len++;
		}
	}

	// ---------------------------------------------------------------- index

	/**
	 * Finds the very first next dot. Ignores dots between index brackets.
	 */
	protected int indexOfDot(String name) {
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
	protected String extractIndex(BeanProperty bp) {
		bp.index = null;
		String name = bp.name;
		int lastNdx = name.length() - 1;
		if (lastNdx < 0) {
			return null;
		}
		if (name.charAt(lastNdx) == ']') {
			int leftBracketNdx = name.lastIndexOf('[');
			if (leftBracketNdx != -1) {
				bp.name = name.substring(0, leftBracketNdx);
				bp.index = name.substring(leftBracketNdx + 1, lastNdx);
				return bp.index;
			}
		}
		return null;
	}

	protected int parseInt(String indexString, BeanProperty bp) {
		try {
			return Integer.parseInt(indexString);
		} catch (NumberFormatException nfex) {
			throw new BeanException("Invalid index: " + indexString, bp, nfex);
		}
	}

	// ---------------------------------------------------------------- create property

	/**
	 * Creates new instance for current property name through its setter.
	 * It uses default constructor!
	 */
	protected Object createBeanProperty(BeanProperty bp) {
		MethodDescriptor setter = bp.cd.getBeanSetterMethodDescriptor(bp.name, true);
		FieldDescriptor field = null;
		Class type;
		if (setter != null) {
			type = setter.getRawParameterTypes()[0];
		} else {
			field = getField(bp, true);
			if (field == null) {
				return null;
			}
			type = field.getRawType();
		}
		Object newInstance;
		try {
			newInstance = ReflectUtil.newInstance(type);
		} catch (Exception ex) {
			throw new BeanException("Unable to create property: " + bp.name, bp, ex);
		}
		if (setter != null) {
			invokeSetter(bp.bean, setter, newInstance);
		} else {
			setFieldValue(bp.bean, field, newInstance);
		}
		return newInstance;
	}

	// ---------------------------------------------------------------- generic and type

	/**
	 * Extracts generic component type of a property. Returns <code>Object.class</code>
	 * when property does not have component.
	 */
	protected Class extractGenericComponentType(BeanProperty bp) {
		Class componentType = null;

		if (bp.field != null) {
			componentType = bp.field.getRawComponentType();
		} else if (bp.method != null) {
			componentType = bp.method.getRawReturnComponentType();
		}

		if (componentType == null) {
			componentType = Object.class;
		}
		return componentType;
	}

	/**
	 * Converts <b>Map</b> index to key type. If conversion fails, original value will be returned.
	 */
	protected Object convertIndexToMapKey(Object index, BeanProperty bp) {
		Class indexType = null;

		if (bp.field != null) {
			indexType = bp.field.getRawKeyComponentType();
		} else if (bp.method != null) {
			indexType = bp.method.getRawReturnKeyComponentType();
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
	protected Class extractType(BeanProperty bp) {
		if (bp.field != null) {
			if (bp.index != null) {
				Class type = bp.field.getRawComponentType();
				return type == null ? Object.class : type;
			}
			return bp.field.getRawType();
		}
		if (bp.method != null) {
			if (bp.index != null) {
				Class type = bp.method.getRawReturnComponentType();
				return type == null ? Object.class : type;
			}
			return bp.method.getRawReturnType();
		}
		return null;// this should not happens
	}

	// ---------------------------------------------------------------- field name

	/**
	 * Returns field for a property.
	 */
	protected FieldDescriptor getField(BeanProperty bp, boolean declared) {
		String fieldName = bp.name;

		if (JoddBean.fieldPrefix != null) {
			fieldName = JoddBean.fieldPrefix + fieldName;
		}

		return bp.cd.getFieldDescriptor(fieldName, declared);
	}

}