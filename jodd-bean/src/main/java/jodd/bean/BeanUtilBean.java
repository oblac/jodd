// Copyright (c) 2003-2013, Jodd Team (jodd.org). All Rights Reserved.

package jodd.bean;

import jodd.JoddBean;
import jodd.introspector.FieldDescriptor;
import jodd.introspector.MethodDescriptor;
import jodd.util.ReflectUtil;
import jodd.util.StringUtil;

import java.lang.reflect.Array;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Instantiable version of {@link BeanUtil}.
 */
public class BeanUtilBean extends BeanUtilUtil {

	// ---------------------------------------------------------------- internal resolver

	/**
	 * Resolves nested property name to the very last indexed property.
	 * If forced, <code>null</code> or non-existing properties will be created.
	 */
	protected void resolveNestedProperties(BeanProperty bp) {
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

	protected boolean resolveExistingNestedProperties(BeanProperty bp) {
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
	public boolean hasSimpleProperty(Object bean, String property, boolean declared) {
		return hasSimpleProperty(new BeanProperty(bean, property, false), declared);
	}

	protected boolean hasSimpleProperty(BeanProperty bp, boolean declared) {
		if (bp.bean == null) {
			return false;
		}

		// try: getProperty() or isProperty()
		bp.field = null;
		bp.method = bp.cd.getBeanGetter(bp.name, declared);
		if (bp.method != null) {
			return true;
		}

		// try: =property
		bp.field = getField(bp, declared);
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
	public Object getSimpleProperty(Object bean, String property, boolean declared) {
		return getSimpleProperty(new BeanProperty(bean, property, false), declared);
	}

	/**
	 * Reads simple property forced: when property value doesn't exist, it will be created.
	 */
	public Object getSimplePropertyForced(Object bean, String property, boolean declared) {
		return getSimpleProperty(new BeanProperty(bean, property, true), declared);
	}

	protected Object getSimpleProperty(BeanProperty bp, boolean declared) {

		if ((bp.name.length() == 0 && bp.first) || bp.name.equals(JoddBean.thisRef)) {
			return bp.bean;
		}

		// try: getProperty() or isProperty()
		bp.field = null;
		bp.method = bp.cd.getBeanGetter(bp.name, declared);
		if (bp.method != null) {
			Object result = invokeGetter(bp.bean, bp.method);
			if ((result == null) && (bp.forced == true)) {
				result = createBeanProperty(bp);
			}
			return result;
		}

		// try: =property
		bp.field = getField(bp, declared);
		if (bp.field != null) {
			Object result = getFieldValue(bp.bean, bp.field);
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

	public void setSimpleProperty(Object bean, String property, Object value, boolean declared) {
		setSimpleProperty(new BeanProperty(bean, property, false), value, declared);
	}

	/**
	 * Sets a value of simple property.
	 */
	@SuppressWarnings({"unchecked"})
	protected void setSimpleProperty(BeanProperty bp, Object value, boolean declared) {

		// try: setProperty(value)
		MethodDescriptor method = bp.cd.getBeanSetterMethodDescriptor(bp.name, declared);
		if (method != null) {
			invokeSetter(bp.bean, method, value);
			return;
		}

		// try: property=
		FieldDescriptor fieldDescriptor = getField(bp, declared);
		if (fieldDescriptor != null) {
			setFieldValue(bp.bean, fieldDescriptor, value);
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

	public boolean hasIndexProperty(Object bean, String property, boolean declared) {
		return hasIndexProperty(new BeanProperty(bean, property, false), declared);
	}

	protected boolean hasIndexProperty(BeanProperty bp, boolean declared) {

		if (bp.bean == null) {
			return false;
		}
		String indexString = extractIndex(bp);

		if (indexString == null) {
			return hasSimpleProperty(bp, declared);
		}

		Object resultBean = getSimpleProperty(bp, declared);

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


	public Object getIndexProperty(Object bean, String property, boolean declared, boolean forced) {
		return getIndexProperty(new BeanProperty(bean, property, forced), declared);
	}

	/**
	 * Get non-nested property value: either simple or indexed property.
	 * If forced, missing bean will be created if possible.
	 */
	protected Object getIndexProperty(BeanProperty bp, boolean declared) {
		String indexString = extractIndex(bp);

		Object resultBean = getSimpleProperty(bp, declared);

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
				Class listType = extractGenericType(bp, 0);
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
					Class mapType = extractGenericType(bp, 1);
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
		throw new BeanException("Index property '" + bp.name + "' is not array, list or map.", bp);
	}



	public void setIndexProperty(Object bean, String property, Object value, boolean declared, boolean forced) {
		setIndexProperty(new BeanProperty(bean, property, forced), value, declared);
	}

	/**
	 * Sets indexed or regular properties (no nested!).
	 */
	@SuppressWarnings({"unchecked"})
	protected void setIndexProperty(BeanProperty bp, Object value, boolean declared) {
		String indexString = extractIndex(bp);

		if (indexString == null) {
			setSimpleProperty(bp, value, declared);
			return;
		}

		// try: getInner()
		Object nextBean = getSimpleProperty(bp, declared);

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
			Class listType = extractGenericType(bp, 0);
			if (listType != null) {
				value = convertType(value, listType);
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
			Class mapType = extractGenericType(bp, 1);
			if (mapType != null) {
				value = convertType(value, mapType);
			}
			map.put(indexString, value);
			return;
		}
		throw new BeanException("Index property '" + bp.name + "' is not array, list or map.", bp);
	}


	// ---------------------------------------------------------------- SET

	/**
	 * Sets Java Bean property.
	 */
	public void setProperty(Object bean, String name, Object value) {
		BeanProperty beanProperty = new BeanProperty(bean, name, false);
		resolveNestedProperties(beanProperty);
		setIndexProperty(beanProperty, value, false);
	}
	/**
	 * Sets Java Bean property silently, without throwing an exception on non-existing properties.
	 */
	public boolean setPropertySilent(Object bean, String name, Object value) {
		BeanProperty beanProperty = new BeanProperty(bean, name, false);
		try {
			resolveNestedProperties(beanProperty);
			setIndexProperty(beanProperty, value, false);
			return true;
		} catch (Exception ignored) {
			return false;
		}
	}

	/**
	 * Sets Java Bean property forced.
	 */
	public void setPropertyForced(Object bean, String name, Object value) {
		BeanProperty beanProperty = new BeanProperty(bean, name, true);
		resolveNestedProperties(beanProperty);
		setIndexProperty(beanProperty, value, false);
	}
	/**
	 * Sets Java Bean property forced, without throwing an exception on non-existing properties.
	 */
	public boolean setPropertyForcedSilent(Object bean, String name, Object value) {
		BeanProperty beanProperty = new BeanProperty(bean, name, true);
		try {
			resolveNestedProperties(beanProperty);
			setIndexProperty(beanProperty, value, false);
			return true;
		} catch (Exception ignored) {
			return false;
		}
	}

	/**
	 * Sets declared Java Bean property.
	 */
	public void setDeclaredProperty(Object bean, String name, Object value) {
		BeanProperty beanProperty = new BeanProperty(bean, name, false);
		resolveNestedProperties(beanProperty);
		setIndexProperty(beanProperty, value, true);
	}
	/**
	 * Silently sets declared Java Bean property.
	 */
	public boolean setDeclaredPropertySilent(Object bean, String name, Object value) {
		BeanProperty beanProperty = new BeanProperty(bean, name, false);
		try {
			resolveNestedProperties(beanProperty);
			setIndexProperty(beanProperty, value, true);
			return true;
		} catch (Exception ignored) {
			return false;
		}
	}
	/**
	 * Sets declared Java Bean property forced.
	 */
	public void setDeclaredPropertyForced(Object bean, String name, Object value) {
		BeanProperty beanProperty = new BeanProperty(bean, name, true);
		resolveNestedProperties(beanProperty);
		setIndexProperty(beanProperty, value, true);
	}
	/**
	 * Silently sets declared Java Bean property forced.
	 */
	public boolean setDeclaredPropertyForcedSilent(Object bean, String name, Object value) {
		BeanProperty beanProperty = new BeanProperty(bean, name, true);
		try {
			resolveNestedProperties(beanProperty);
			setIndexProperty(beanProperty, value, true);
			return true;
		} catch (Exception ignored) {
			return false;
		}
	}


	// ---------------------------------------------------------------- GET

	/**
	 * Returns value of bean's property.
	 */
	public Object getProperty(Object bean, String name) {
		BeanProperty beanProperty = new BeanProperty(bean, name, false);
		resolveNestedProperties(beanProperty);
		return getIndexProperty(beanProperty, false);
	}

	/**
	 * Silently returns value of bean's property.
	 * Return value <code>null</code> is ambiguous: it may means that property name
	 * is valid and property value is <code>null</code> or that property name is invalid.
	 */
	public Object getPropertySilently(Object bean, String name) {
		BeanProperty beanProperty = new BeanProperty(bean, name, false);
		try {
			resolveNestedProperties(beanProperty);
			return getIndexProperty(beanProperty, false);
		} catch (Exception ignored) {
			return null;
		}
	}

	/**
	 * Returns value of declared bean's property.
	 */
	public Object getDeclaredProperty(Object bean, String name) {
		BeanProperty beanProperty = new BeanProperty(bean, name, false);
		resolveNestedProperties(beanProperty);
		return getIndexProperty(beanProperty, true);
	}

	/**
	 * Silently returns value of declared bean's property.
	 * Return value <code>null</code> is ambiguous: it may means that property name
	 * is valid and property value is <code>null</code> or that property name is invalid.
	 */
	public Object getDeclaredPropertySilently(Object bean, String name) {
		BeanProperty beanProperty = new BeanProperty(bean, name, false);
		try {
			resolveNestedProperties(beanProperty);
			return getIndexProperty(beanProperty, true);
		} catch (Exception ignored) {
			return null;
		}
	}


	// ---------------------------------------------------------------- HAS

	public boolean hasProperty(Object bean, String name) {
		BeanProperty beanProperty = new BeanProperty(bean, name, false);
		if (resolveExistingNestedProperties(beanProperty) == false) {
			return false;
		}
		return hasIndexProperty(beanProperty, false);
	}

	public boolean hasDeclaredProperty(Object bean, String name) {
		BeanProperty beanProperty = new BeanProperty(bean, name, false);
		if (resolveExistingNestedProperties(beanProperty) == false) {
			return false;
		}
		return hasIndexProperty(beanProperty, true);
	}

	// ---------------------------------------------------------------- type

	public Class getPropertyType(Object bean, String name) {
		BeanProperty beanProperty = new BeanProperty(bean, name, false);
		if (resolveExistingNestedProperties(beanProperty) == false) {
			return null;
		}
		hasIndexProperty(beanProperty, false);
		return extractType(beanProperty);
	}

	public Class getDeclaredPropertyType(Object bean, String name) {
		BeanProperty beanProperty = new BeanProperty(bean, name, false);
		if (resolveExistingNestedProperties(beanProperty) == false) {
			return null;
		}
		hasIndexProperty(beanProperty, true);
		return extractType(beanProperty);
	}


	// ---------------------------------------------------------------- populate

	/**
	 * Populates bean from a <code>Map</code>.
	 */
	public void populateBean(Object bean, Map<?, ?> map) {
		populateProperty(bean, null, map);
	}

	/**
	 * Populates <b>simple</b> bean property from a <code>Map</code>.
	 */
	public void populateProperty(Object bean, String name, Map<?, ?> map) {

		if (name != null) {
			if (map == null) {
				setSimpleProperty(bean, name, null, false);
				return;
			}

			bean = getSimplePropertyForced(bean, name, true);
		}

		for (Map.Entry<?, ?> entry : map.entrySet()) {
			String key = entry.getKey().toString();
			Object value = entry.getValue();

			if (value != null) {
				if (value instanceof Map) {
					populateProperty(bean, key, (Map) value);
					continue;
				}
				if (value instanceof List) {
					populateProperty(bean, key, (List) value);
					continue;
				}
			}

			setSimpleProperty(bean, key, value, true);
		}
	}

	/**
	 * Populates <b>indexed</b> bean property from a <code>List</code>.
	 */
	public void populateProperty(Object bean, String name, List<?> list) {
		if (list == null) {
			setSimpleProperty(bean, name, null, false);
			return;
		}

		name += '[';
		int index = 0;

		for (Object item : list) {
			setIndexProperty(bean, name + index + ']', item, true, true);
			index++;
		}
	}

	// ---------------------------------------------------------------- utilities

	private static final char[] INDEX_CHARS = new char[] {'.', '['};

	/**
	 * Extract the first name of this reference.
	 */
	public String extractThisReference(String propertyName) {
		int ndx = StringUtil.indexOfChars(propertyName, INDEX_CHARS);
		if (ndx == -1) {
			return propertyName;
		}
		return propertyName.substring(0, ndx);
	}

}