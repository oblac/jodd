// Copyright (c) 2003-2012, Jodd Team (jodd.org). All Rights Reserved.

package jodd.bean;

import jodd.typeconverter.TypeConverterManager;
import jodd.util.ReflectUtil;
import jodd.util.StringUtil;

import java.lang.reflect.Method;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.util.Map;
import java.util.List;
import java.util.HashMap;


/**
 * Supreme utility for reading and writing bean properties. However, this one is the fastest available.
 * Although it provides various methods, the whole thing can be easily extended to match most needs.
 * <p>
 * BeanUtil supports:
 * <ul>
 * <li>Nested properties: separated by a dot ('.')</li>
 * <li>Indexed properties: arrays or Lists</li>
 * <li>Simple properties: accessor or Map</li>
 * </ul>
 *
 * <p>
 * Variants includes combinations of forced, declared and silent writing.
 * <ul>
 * <li><i>Forced</i> setting property tries to create destination property so it can be set correctly.</li>
 * <li><i>Silent</i> doesn't throw an exception if destination doesn't exist or if conversion fails.</li>
 * <li><i>Declared</i> includes only declared (public) properties.</li>
 * </ul>
 * <p>
 * This utility considers both bean property methods (set and get accessors), and bean fields.
 * This is done because of several reasons: often there is no need for both set/get accessors, since
 * bean logic requires just one functionality (e.g. just reading). In such case, other bean manipulation
 * libraries still requires to have both accessors in order to set or get value.
 * Another reason is that most common usage is to work with public accessors, and in that case
 * private fields are ignored.
 *
 * @see BeanUtilUtil
 * @see BeanTool
 */
public class BeanUtil extends BeanUtilUtil {

	// ---------------------------------------------------------------- internal resolver

	/**
	 * Resolves nested property name to the very last indexed property.
	 * If forced, <code>null</code> or non-existing properties will be created.
	 */
	protected static void resolveNestedProperties(BeanProperty bp) {
		String name = bp.name;
		int dotNdx;
		while ((dotNdx = indexOfDot(name)) != -1) {
			bp.last = false;
			bp.name = name.substring(0, dotNdx);
			bp.setBean(getIndexProperty(bp, true));
			name = name.substring(dotNdx + 1);
		}
		bp.last = true;
		bp.name = name;
	}

	protected static boolean resolveExistingNestedProperties(BeanProperty bp) {
		String name = bp.name;
		int dotNdx;
		while ((dotNdx = indexOfDot(name)) != -1) {
			bp.last = false;
			String temp = bp.name = name.substring(0, dotNdx);
			if (hasIndexProperty(bp, true) == false) {
				return false;
			}
			bp.name = temp;
			bp.setBean(getIndexProperty(bp, true));
			name = name.substring(dotNdx + 1);
		}
		bp.last = true;
		bp.name = name;
		return true;
	}


	// ---------------------------------------------------------------- simple property

	/**
	 * Returns <code>true</code> if simple property exist.
	 */
	public static boolean hasSimpleProperty(Object bean, String property, boolean suppressSecurity) {
		return hasSimpleProperty(new BeanProperty(bean, property, false), suppressSecurity);
	}

	protected static boolean hasSimpleProperty(BeanProperty bp, boolean suppressSecurity) {
		if (bp.bean == null) {
			return false;
		}

		// try: getProperty() or isProperty()
		bp.field = null;
		bp.method = bp.cd.getBeanGetter(bp.name, suppressSecurity);
		if (bp.method != null) {
			return true;
		}

		// try: =property
		bp.field = bp.cd.getField(bp.name, suppressSecurity);
		if (bp.field != null) {
			return true;
		}

		// try: (Map) get("property")
		if (bp.cd.isMap()) {
			Map map = (Map) bp.bean;
			if (map.containsKey(bp.name) == true) {
				return true;
			}
		}

		return false;
	}



	/**
	 * Reads simple property.
	 */
	public static Object getSimpleProperty(Object bean, String property, boolean suppressSecurity) {
		return getSimpleProperty(new BeanProperty(bean, property, false), suppressSecurity);
	}

	/**
	 * Reads simple property forced: when property value doesn't exist, it will be created.
	 */
	public static Object getSimplePropertyForced(Object bean, String property, boolean suppressSecurity) {
		return getSimpleProperty(new BeanProperty(bean, property, true), suppressSecurity);
	}

	public static final String THIS_REF = "*this";

	protected static Object getSimpleProperty(BeanProperty bp, boolean suppressSecurity) {

		if (bp.name.length() == 5) {			// hardcoded!
			if (bp.name.equals(THIS_REF)) {
				return bp.bean;
			}
		}

		// try: getProperty() or isProperty()
		bp.field = null;
		bp.method = bp.cd.getBeanGetter(bp.name, suppressSecurity);
		if (bp.method != null) {
			Object result = invokeGetter(bp.bean, bp.method);
			if ((result == null) && (bp.forced == true)) {
				result = createBeanProperty(bp);
			}
			return result;
		}

		// try: =property
		bp.field = bp.cd.getField(bp.name, suppressSecurity);
		if (bp.field != null) {
			Object result = getField(bp.bean, bp.field);
			if ((result == null) && (bp.forced == true)) {
				result = createBeanProperty(bp);
			}
			return result;
		}

		// try: (Map) get("property")
		if (bp.cd.isMap()) {
			Map map = (Map) bp.bean;
			if (map.containsKey(bp.name) == false) {
				if (bp.forced == false) {
					throw new BeanException("Map key not found: " + bp.name, bp);
				}
				Map value = new HashMap();
				//noinspection unchecked
				map.put(bp.name, value);
				return value;
			}
			return map.get(bp.name);
		}

		// failed
		throw new BeanException("Simple property not found: " + bp.name, bp);
	}

	public static void setSimpleProperty(Object bean, String property, Object value, boolean suppressSecurity) {
		setSimpleProperty(new BeanProperty(bean, property, false), value, suppressSecurity);
	}

	/**
	 * Sets a value of simple property.
	 */
	@SuppressWarnings({"unchecked"})
	protected static void setSimpleProperty(BeanProperty bp, Object value, boolean suppressSecurity) {

		// try: setProperty(value)
		Method method = bp.cd.getBeanSetter(bp.name, suppressSecurity);
		if (method != null) {
			invokeSetter(bp.bean, method, value);
			return;
		}

		// try: property=
		Field field = bp.cd.getField(bp.name, suppressSecurity);
		if (field != null) {
			setField(bp.bean, field, value);
			return;
		}

		// try: put("property", value)
		if (bp.cd.isMap() == true) {
			((Map) bp.bean).put(bp.name, value);
			return;
		}
		throw new BeanException("Simple property not found: " + bp.name, bp);
	}




	// ---------------------------------------------------------------- indexed property

	public static boolean hasIndexProperty(Object bean, String property, boolean suppressSecurity) {
		return hasIndexProperty(new BeanProperty(bean, property, false), suppressSecurity);
	}

	protected static boolean hasIndexProperty(BeanProperty bp, boolean suppressSecurity) {

		if (bp.bean == null) {
			return false;
		}
		String indexString = extractIndex(bp);

		if (indexString == null) {
			return hasSimpleProperty(bp, suppressSecurity);
		}

		Object resultBean = getSimpleProperty(bp, suppressSecurity);

		if (resultBean == null) {
			return false;
		}

		// try: property[index]
		if (resultBean.getClass().isArray() == true) {
			int index = parseInt(indexString, bp);
			return (index >= 0) && (index < Array.getLength(resultBean));
		}

		// try: list.get(index)
		if (resultBean instanceof List) {
			int index = parseInt(indexString, bp);
			return (index >= 0) && (index < ((List)resultBean).size());
		}
		if (resultBean instanceof Map) {
			return ((Map)resultBean).containsKey(indexString);
		}

		// failed
		return false;
	}


	public static Object getIndexProperty(Object bean, String property, boolean suppressSecurity, boolean forced) {
		return getIndexProperty(new BeanProperty(bean, property, forced), suppressSecurity);
	}

	/**
	 * Get non-nested property value: either simple or indexed property.
	 * If forced, missing bean will be created if possible.
	 */
	protected static Object getIndexProperty(BeanProperty bp, boolean suppressSecurity) {
		String indexString = extractIndex(bp);

		Object resultBean = getSimpleProperty(bp, suppressSecurity);

		if (indexString == null) {
			return resultBean;	// no index, just simple bean
		}
		if (resultBean == null) {
			throw new BeanException("Index property is null: " + bp.name, bp);
		}

		// try: property[index]
		if (resultBean.getClass().isArray() == true) {
			int index = parseInt(indexString, bp);
			if (bp.forced == true) {
				return arrayForcedGet(bp, resultBean, index);
			} else {
				return Array.get(resultBean, index);
			}
		}

		// try: list.get(index)
		if (resultBean instanceof List) {
			int index = parseInt(indexString, bp);
			List list = (List) resultBean;
			if (bp.forced == false) {
				return list.get(index);
			}
			if (bp.last == false) {
				ensureListSize(list, index);
			}
			Object value = list.get(index);
			if (value == null) {
				Class listType = extracticGenericType(bp, 0);
				if (listType == null) {
					listType = Map.class;
				}
				try {
					value = ReflectUtil.newInstance(listType);
				} catch (Exception ex) {
					throw new BeanException("Unable to create list element: " + bp.name + '[' + index + ']', bp, ex);
				}
				//noinspection unchecked
				list.set(index, value);
			}
			return value;
		}

		// try: map.get('index')
		if (resultBean instanceof Map) {
			Map map = (Map) resultBean;
			if (bp.forced == false) {
				return map.get(indexString);
			}
			Object value = map.get(indexString);
			if (bp.last == false) {
				if (value == null) {
					Class mapType = extracticGenericType(bp, 1);
					if (mapType == null) {
						mapType = Map.class;
					}
					try {
						value = ReflectUtil.newInstance(mapType);
					} catch (Exception ex) {
						throw new BeanException("Unable to create map element: " + bp.name + '[' + indexString + ']', bp, ex);
					}

					//noinspection unchecked
					map.put(indexString, value);
				}
			}
			return value;
		}

		// failed
		throw new BeanException("Index property '" + bp.name + "' is neither an array, a list or a map.", bp);
	}



	public static void setIndexProperty(Object bean, String property, Object value, boolean suppressSecurity, boolean forced) {
		setIndexProperty(new BeanProperty(bean, property, forced), value, suppressSecurity);
	}

	/**
	 * Sets indexed or regular properties (no nested!).
	 */
	@SuppressWarnings({"unchecked"})
	protected static void setIndexProperty(BeanProperty bp, Object value, boolean suppressSecurity) {
		String indexString = extractIndex(bp);

		if (indexString == null) {
			setSimpleProperty(bp, value, suppressSecurity);
			return;
		}

		// try: getInner()
		Object nextBean = getSimpleProperty(bp, suppressSecurity);

		// inner bean found
		if (nextBean.getClass().isArray() == true) {
			int index = parseInt(indexString, bp);
			if (bp.forced == true) {
				arrayForcedSet(bp, nextBean, index, value);
			} else {
				Array.set(nextBean, index, value);
			}
			return;
		}

		if (nextBean instanceof List) {
			int index = parseInt(indexString, bp);
			Class listType = extracticGenericType(bp, 0);
			if (listType != null) {
				value = TypeConverterManager.castType(value, listType);
			}
			List list = (List) nextBean;
			if (bp.forced == true) {
				ensureListSize(list, index);
			}
			list.set(index, value);
			return;
		}
		if (nextBean instanceof Map) {
			Map map = ((Map) nextBean);
			Class mapType = extracticGenericType(bp, 1);
			if (mapType != null) {
				value = TypeConverterManager.castType(value, mapType);
			}
			map.put(indexString, value);
			return;
		}
		throw new BeanException("Index property '" + bp.name + "' is neither an array, a list or a map.", bp);
	}


	// ---------------------------------------------------------------- SET

	/**
	 * Sets Java Bean property.
	 */
	public static void setProperty(Object bean, String name, Object value) {
		BeanProperty beanProperty = new BeanProperty(bean, name, false);
		resolveNestedProperties(beanProperty);
		setIndexProperty(beanProperty, value, false);
	}
	/**
	 * Sets Java Bean property silently, without throwing an exception on non-existing properties.
	 */
	public static boolean setPropertySilent(Object bean, String name, Object value) {
		BeanProperty beanProperty = new BeanProperty(bean, name, false);
		try {
			resolveNestedProperties(beanProperty);
			setIndexProperty(beanProperty, value, false);
			return true;
		} catch (Exception ex) {
			return false;
		}
	}

	/**
	 * Sets Java Bean property forced.
	 */
	public static void setPropertyForced(Object bean, String name, Object value) {
		BeanProperty beanProperty = new BeanProperty(bean, name, true);
		resolveNestedProperties(beanProperty);
		setIndexProperty(beanProperty, value, false);
	}
	/**
	 * Sets Java Bean property forced, without throwing an exception on non-existing properties.
	 */
	public static boolean setPropertyForcedSilent(Object bean, String name, Object value) {
		BeanProperty beanProperty = new BeanProperty(bean, name, true);
		try {
			resolveNestedProperties(beanProperty);
			setIndexProperty(beanProperty, value, false);
			return true;
		} catch (Exception ex) {
			return false;
		}
	}

	/**
	 * Sets declared Java Bean property.
	 */
	public static void setDeclaredProperty(Object bean, String name, Object value) {
		BeanProperty beanProperty = new BeanProperty(bean, name, false);
		resolveNestedProperties(beanProperty);
		setIndexProperty(beanProperty, value, true);
	}
	/**
	 * Silently sets declared Java Bean property.
	 */
	public static boolean setDeclaredPropertySilent(Object bean, String name, Object value) {
		BeanProperty beanProperty = new BeanProperty(bean, name, false);
		try {
			resolveNestedProperties(beanProperty);
			setIndexProperty(beanProperty, value, true);
			return true;
		} catch (Exception ex) {
			return false;
		}
	}
	/**
	 * Sets declared Java Bean property forced.
	 */
	public static void setDeclaredPropertyForced(Object bean, String name, Object value) {
		BeanProperty beanProperty = new BeanProperty(bean, name, true);
		resolveNestedProperties(beanProperty);
		setIndexProperty(beanProperty, value, true);
	}
	/**
	 * Silently sets declared Java Bean property forced.
	 */
	public static boolean setDeclaredPropertyForcedSilent(Object bean, String name, Object value) {
		BeanProperty beanProperty = new BeanProperty(bean, name, true);
		try {
			resolveNestedProperties(beanProperty);
			setIndexProperty(beanProperty, value, true);
			return true;
		} catch (Exception ex) {
			return false;
		}
	}


	// ---------------------------------------------------------------- GET

	/**
	 * Returns value of bean's property.
	 */
	public static Object getProperty(Object bean, String name) {
		BeanProperty beanProperty = new BeanProperty(bean, name, false);
		resolveNestedProperties(beanProperty);
		return getIndexProperty(beanProperty, false);
	}

	/**
	 * Silently returns value of bean's property.
	 * Return value <code>null</code> is ambiguous: it may means that property name
	 * is valid and property value is <code>null</code> or that property name is invalid.
	 */
	public static Object getPropertySilently(Object bean, String name) {
		BeanProperty beanProperty = new BeanProperty(bean, name, false);
		try {
			resolveNestedProperties(beanProperty);
			return getIndexProperty(beanProperty, false);
		} catch (Exception ex) {
			return null;
		}
	}

	/**
	 * Returns value of declared bean's property.
	 */
	public static Object getDeclaredProperty(Object bean, String name) {
		BeanProperty beanProperty = new BeanProperty(bean, name, false);
		resolveNestedProperties(beanProperty);
		return getIndexProperty(beanProperty, true);
	}

	/**
	 * Silently returns value of declared bean's property.
	 * Return value <code>null</code> is ambiguous: it may means that property name
	 * is valid and property value is <code>null</code> or that property name is invalid.
	 */
	public static Object getDeclaredPropertySilently(Object bean, String name) {
		BeanProperty beanProperty = new BeanProperty(bean, name, false);
		try {
			resolveNestedProperties(beanProperty);
			return getIndexProperty(beanProperty, true);
		} catch (Exception ex) {
			return null;
		}
	}


	// ---------------------------------------------------------------- HAS

	public static boolean hasProperty(Object bean, String name) {
		BeanProperty beanProperty = new BeanProperty(bean, name, false);
		if (resolveExistingNestedProperties(beanProperty) == false) {
			return false;
		}
		return hasIndexProperty(beanProperty, false);
	}

	public static boolean hasDeclaredProperty(Object bean, String name) {
		BeanProperty beanProperty = new BeanProperty(bean, name, false);
		if (resolveExistingNestedProperties(beanProperty) == false) {
			return false;
		}
		return hasIndexProperty(beanProperty, true);
	}

	// ---------------------------------------------------------------- type

	public static Class getPropertyType(Object bean, String name) {
		BeanProperty beanProperty = new BeanProperty(bean, name, false);
		if (resolveExistingNestedProperties(beanProperty) == false) {
			return null;
		}
		hasIndexProperty(beanProperty, false);
		return extractType(beanProperty);
	}

	public static Class getDeclaredPropertyType(Object bean, String name) {
		BeanProperty beanProperty = new BeanProperty(bean, name, false);
		if (resolveExistingNestedProperties(beanProperty) == false) {
			return null;
		}
		hasIndexProperty(beanProperty, true);
		return extractType(beanProperty);
	}


	// ---------------------------------------------------------------- utilities

	private static char[] indexChars = new char[] {'.', '['};

	/**
	 * Extract the first name of this reference. 
	 */
	public static String extractThisReference(String propertyName) {
		int ndx = StringUtil.indexOfChars(propertyName, indexChars);
		if (ndx == -1) {
			return propertyName;
		}
		return propertyName.substring(0, ndx);
	}
}
