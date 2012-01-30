// Copyright (c) 2003-2012, Jodd Team (jodd.org). All Rights Reserved.

package jodd.bean;

import jodd.typeconverter.TypeConverterManager;
import jodd.util.ReflectUtil;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.List;

/**
 * Various bean property utilities that makes writings of {@link BeanUtil} classes easy.
 */
public class BeanUtilUtil {

	// ---------------------------------------------------------------- accessors

	/**
	 * Invokes <code>setXxx()</code> method with appropriate conversion if available.
	 * It is assumed that all provided arguments are valid.
	 */
	@SuppressWarnings({"unchecked"})
	protected static void invokeSetter(Object bean, Method m, Object value) {
		try {
			Class[] paramTypes = m.getParameterTypes();
			value = TypeConverterManager.castType(value, paramTypes[0]);
			m.invoke(bean, value);
		} catch (Exception ex) {
			throw new BeanException("Unable to invoke setter: " + bean.getClass().getSimpleName() + '#' + m.getName() + "()", ex);
		}
	}

	/**
	 * Invokes <code>getXxx()</code> method of specified bean.
	 * It is assumed that all provided arguments are valid.
	 */
	protected static Object invokeGetter(Object bean, Method m) {
		try {
			return m.invoke(bean);
		} catch (Exception ex) {
			throw new BeanException("Unable to invoke getter: " + bean.getClass().getSimpleName() + '#' + m.getName() + "()", ex);
		}
	}

	/**
	 * Sets field value.
	 */
	@SuppressWarnings({"unchecked"})
	protected static void setField(Object bean, Field f, Object value) {
		try {
			Class type = f.getType();
			value = TypeConverterManager.castType(value, type);
			f.set(bean, value);
		} catch (Exception iaex) {
			throw new BeanException("Unable to set field: " + bean.getClass().getSimpleName() + '#' + f.getName(), iaex);
		}
	}

	/**
	 * Return value of a field.
	 */
	protected static Object getField(Object bean, Field f) {
		try {
			return f.get(bean);
		} catch (Exception iaex) {
			throw new BeanException("Unable to get field " + bean.getClass().getSimpleName() + '#' + f.getName(), iaex);
		}
	}

	// ---------------------------------------------------------------- forced

	/**
	 * Returns the element of an array forced. If value is <code>null</code>, it will be instantiated.
	 * If not the last part of indexed bean property, array will be expanded to the index if necessary.
	 */
	protected static Object arrayForcedGet(BeanProperty bp, Object array, int index) {
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
	@SuppressWarnings({"unchecked"})
	protected static void arrayForcedSet(BeanProperty bp, Object array, int index, Object value) {
		Class componentType = array.getClass().getComponentType();
		array = ensureArraySize(bp, array, componentType, index);
		value = TypeConverterManager.castType(value, componentType);
		Array.set(array, index, value);
	}


	@SuppressWarnings({"SuspiciousSystemArraycopy"})
	protected static Object ensureArraySize(BeanProperty bp, Object array, Class componentType, int index) {
		int len = Array.getLength(array);
		if (index >= len) {
			Object newArray = Array.newInstance(componentType, index + 1);
			System.arraycopy(array, 0, newArray, 0, len);
			Method setter = bp.cd.getBeanSetter(bp.name, true);
			if (setter != null) {
				invokeSetter(bp.bean, setter, newArray);
			} else {
				Field field = bp.cd.getField(bp.name, true);
				if (field == null) {
					throw new BeanException("Unable to find setter or field named as: " + bp.name, bp);
				}
				setField(bp.bean, field, newArray);
			}
			array = newArray;
		}
		return array;
	}


	@SuppressWarnings({"unchecked"})	
	protected static void ensureListSize(List list, int size) {
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
	protected static int indexOfDot(String name) {
		int ndx = 0;
		while (true) {
			ndx = name.indexOf('.', ndx);
			if (ndx == -1) {
				return -1;
			}
			int rightNdx = name.indexOf(']');
			if (rightNdx > ndx) {
				if (name.lastIndexOf('[', rightNdx) < ndx) {
					ndx = rightNdx + 1;
					continue;
				}
			}
			return ndx;
		}
	}


	/**
	 * Extract index string from non-nested property name.
	 * If index is found, it is stripped from bean property name.
	 * If no index is found, it returns <code>null</code>.
	 */
	protected static String extractIndex(BeanProperty bp) {
		bp.index = null;
		String name = bp.name;
		int lastNdx = name.length() - 1;
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


	protected static int parseInt(String indexString, BeanProperty bp) {
		try {
			return Integer.parseInt(indexString);
		} catch (NumberFormatException nfex) {
			throw new BeanException("Index not a number: " + indexString, bp, nfex);
		}
	}

	// ---------------------------------------------------------------- create property

	/**
	 * Creates new instance for current property name through its setter.
	 * It uses default constructor!
	 */
	protected static Object createBeanProperty(BeanProperty bp) {
		Method setter = bp.cd.getBeanSetter(bp.name, true);
		Field field = null;
		Class type;
		if (setter != null) {
			type = setter.getParameterTypes()[0];
		} else {
			field = bp.cd.getField(bp.name, true);
			if (field == null) {
				return null;
			}
			type = field.getType();
		}
		Object newInstance;
		try {
			newInstance = ReflectUtil.newInstance(type);
		} catch (Exception ex) {
			throw new BeanException("Unable to create '" + bp.name + "' through its setter.", bp);
		}
		if (setter != null) {
			invokeSetter(bp.bean, setter, newInstance);
		} else {
			setField(bp.bean, field, newInstance);
		}
		return newInstance;
	}

	// ---------------------------------------------------------------- generic and type

	/**
	 * Extracts generic parameter types. 
	 */
	protected static Class extracticGenericType(BeanProperty bp, int index) {
		Type type;
		if (bp.field != null) {
			type = bp.field.getGenericType();
		} else if (bp.method != null) {
			type = bp.method.getGenericReturnType();
		} else {
			return null;
		}
		return ReflectUtil.getComponentType(type, index);
	}

	/**
	 * Extracts type of current property.
	 */
	protected static Class extractType(BeanProperty bp) {
		Class<?> type = null;
		if (bp.field != null) {
			if (bp.index != null) {
				type = ReflectUtil.getComponentType(bp.field.getGenericType());
				if (type == null) {
					return Object.class;
				}
			}
			if (type == null) {
				type = bp.field.getType();
			}
		} else if (bp.method != null) {
			if (bp.index != null) {
				type = ReflectUtil.getComponentType(bp.method.getGenericReturnType());
				if (type == null) {
					return Object.class;
				}
			}
			if (type == null) {
				type = bp.method.getReturnType();
			}
		}
		return type;
	}

}
