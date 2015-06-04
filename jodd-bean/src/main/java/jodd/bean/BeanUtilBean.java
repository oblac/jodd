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

import jodd.exception.ExceptionUtil;
import jodd.introspector.Getter;
import jodd.introspector.Setter;
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
			bp.setName(name.substring(0, dotNdx));
			bp.setBean(getIndexProperty(bp));
			name = name.substring(dotNdx + 1);
		}
		bp.last = true;
		bp.setName(name);
	}

	protected boolean resolveExistingNestedProperties(BeanProperty bp) {
		String name = bp.name;
		int dotNdx;
		while ((dotNdx = indexOfDot(name)) != -1) {
			bp.last = false;
			bp.setName(name.substring(0, dotNdx));
			String temp = bp.name;
			if (hasIndexProperty(bp) == false) {
				return false;
			}
			bp.setName(temp);
			bp.setBean(getIndexProperty(bp));
			name = name.substring(dotNdx + 1);
		}
		bp.last = true;
		bp.setName(name);
		return true;
	}


	// ---------------------------------------------------------------- simple property

	/**
	 * Returns <code>true</code> if simple property exist.
	 */
	public boolean hasSimpleProperty(Object bean, String property, boolean declared) {
		return hasSimpleProperty(new BeanProperty(bean, property, declared, false));
	}

	protected boolean hasSimpleProperty(BeanProperty bp) {
		if (bp.bean == null) {
			return false;
		}

		// try: getter
		Getter getter = bp.getGetter(bp.declared);
		if (getter != null) {
			return true;
		}

		// try: (Map) get("property")
		if (bp.isMap()) {
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
		return getSimpleProperty(new BeanProperty(bean, property, declared, false));
	}

	/**
	 * Reads simple property forced: when property value doesn't exist, it will be created.
	 */
	public Object getSimplePropertyForced(Object bean, String property, boolean declared) {
		return getSimpleProperty(new BeanProperty(bean, property, declared, true));
	}

	protected Object getSimpleProperty(BeanProperty bp) {

		if (bp.name.length() == 0) {
			if (bp.indexString != null) {
				// index string exist, but property name is missing
				return bp.bean;
			}
			throw new BeanException("Invalid property", bp);
		}

		Getter getter = bp.getGetter(bp.declared);

		if (getter != null) {
			Object result;
			try {
				result = getter.invokeGetter(bp.bean);
			} catch (Exception ex) {
				if (bp.silent) {
					return null;
				}
				throw new BeanException("Getter failed: " + getter, ex);
			}

			if ((result == null) && (bp.forced == true)) {
				result = createBeanProperty(bp);
			}
			return result;
		}

		// try: (Map) get("property")
		if (bp.isMap()) {
			Map map = (Map) bp.bean;
			Object key = convertIndexToMapKey(getter, bp.name);

			if (map.containsKey(key) == false) {
				if (bp.forced == false) {
					if (bp.silent) {
						return null;
					}
					throw new BeanException("Map key not found: " + bp.name, bp);
				}
				Map value = new HashMap();
				//noinspection unchecked
				map.put(key, value);
				return value;
			}
			return map.get(key);
		}

		// failed
		if (bp.silent) {
			return null;
		}
		throw new BeanException("Simple property not found: " + bp.name, bp);
	}

	/**
	 * Sets simple property.
	 */
	public void setSimpleProperty(Object bean, String property, Object value, boolean declared) {
		setSimpleProperty(new BeanProperty(bean, property, declared, false), value);
	}

	/**
	 * Sets a value of simple property.
	 */
	@SuppressWarnings({"unchecked"})
	protected void setSimpleProperty(BeanProperty bp, Object value) {

		Setter setter = bp.getSetter(bp.declared);

		// try: setter
		if (setter != null) {
			invokeSetter(setter, bp, value);
			return;
		}

		// try: put("property", value)
		if (bp.isMap()) {
			((Map) bp.bean).put(bp.name, value);
			return;
		}
		if (bp.silent) {
			return;
		}
		throw new BeanException("Simple property not found: " + bp.name, bp);
	}

	// ---------------------------------------------------------------- indexed property

	/**
	 * Returns <code>true</code> if bean has indexed property.
	 */
	public boolean hasIndexProperty(Object bean, String property, boolean declared) {
		return hasIndexProperty(new BeanProperty(bean, property, declared, false));
	}

	protected boolean hasIndexProperty(BeanProperty bp) {

		if (bp.bean == null) {
			return false;
		}
		String indexString = extractIndex(bp);

		if (indexString == null) {
			return hasSimpleProperty(bp);
		}

		Object resultBean = getSimpleProperty(bp);

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
		return getIndexProperty(new BeanProperty(bean, property, declared, forced));
	}

	/**
	 * Get non-nested property value: either simple or indexed property.
	 * If forced, missing bean will be created if possible.
	 */
	protected Object getIndexProperty(BeanProperty bp) {
		bp.indexString = extractIndex(bp);

		Object value = _getIndexProperty(bp);

		bp.indexString = null;

		return value;
	}

	private Object _getIndexProperty(BeanProperty bp) {
		Object resultBean = getSimpleProperty(bp);
		Getter getter = bp.getGetter(bp.declared);

		if (bp.indexString == null) {
			return resultBean;	// no index, just simple bean
		}
		if (resultBean == null) {
			if (bp.silent) {
				return null;
			}
			throw new BeanException("Index property is null: " + bp.name, bp);
		}

		// try: property[index]
		if (resultBean.getClass().isArray() == true) {
			int index = parseInt(bp.indexString, bp);
			if (bp.forced == true) {
				return arrayForcedGet(bp, resultBean, index);
			} else {
				return Array.get(resultBean, index);
			}
		}

		// try: list.get(index)
		if (resultBean instanceof List) {
			int index = parseInt(bp.indexString, bp);
			List list = (List) resultBean;
			if (bp.forced == false) {
				return list.get(index);
			}
			if (bp.last == false) {
				ensureListSize(list, index);
			}
			Object value = list.get(index);
			if (value == null) {
				Class listComponentType = extractGenericComponentType(getter);
				if (listComponentType == Object.class) {
					// not an error: when component type is unknown, use Map as generic bean
					listComponentType = Map.class;
				}
				try {
					value = ReflectUtil.newInstance(listComponentType);
				} catch (Exception ex) {
					if (bp.silent) {
						return null;
					}
					throw new BeanException("Invalid list element: " + bp.name + '[' + index + ']', bp, ex);
				}
				//noinspection unchecked
				list.set(index, value);
			}
			return value;
		}

		// try: map.get('index')
		if (resultBean instanceof Map) {
			Map map = (Map) resultBean;
			Object key = convertIndexToMapKey(getter, bp.indexString);

			if (bp.forced == false) {
				return map.get(key);
			}
			Object value = map.get(key);
			if (bp.last == false) {
				if (value == null) {
					Class mapComponentType = extractGenericComponentType(getter);
					if (mapComponentType == Object.class) {
						mapComponentType = Map.class;
					}
					try {
						value = ReflectUtil.newInstance(mapComponentType);
					} catch (Exception ex) {
						if (bp.silent) {
							return null;
						}
						throw new BeanException("Invalid map element: " + bp.name + '[' + bp.indexString + ']', bp, ex);
					}

					//noinspection unchecked
					map.put(key, value);
				}
			}
			return value;
		}

		// failed
		if (bp.silent) {
			return null;
		}
		throw new BeanException("Index property is not an array, list or map: " + bp.name, bp);
	}

	public void setIndexProperty(Object bean, String property, Object value, boolean declared, boolean forced) {
		setIndexProperty(new BeanProperty(bean, property, declared, forced), value);
	}

	/**
	 * Sets indexed or regular properties (no nested!).
	 */
	protected void setIndexProperty(BeanProperty bp, Object value) {
		bp.indexString = extractIndex(bp);

		_setIndexProperty(bp, value);

		bp.indexString = null;
	}

	@SuppressWarnings({"unchecked"})
	private void _setIndexProperty(BeanProperty bp, Object value) {
		if (bp.indexString == null) {
			setSimpleProperty(bp, value);
			return;
		}

		// try: getInner()
		Object nextBean = getSimpleProperty(bp);
		Getter getter = bp.getGetter(bp.declared);

		if (nextBean == null) {
			if (bp.silent) {
				return;
			}
			throw new BeanException("Index property is null:" + bp.name, bp);
		}

		// inner bean found
		if (nextBean.getClass().isArray() == true) {
			int index = parseInt(bp.indexString, bp);
			if (bp.forced == true) {
				arrayForcedSet(bp, nextBean, index, value);
			} else {
				Array.set(nextBean, index, value);
			}
			return;
		}

		if (nextBean instanceof List) {
			int index = parseInt(bp.indexString, bp);
			Class listComponentType = extractGenericComponentType(getter);
			if (listComponentType != Object.class) {
				value = convertType(value, listComponentType);
			}
			List list = (List) nextBean;
			if (bp.forced == true) {
				ensureListSize(list, index);
			}
			list.set(index, value);
			return;
		}
		if (nextBean instanceof Map) {
			Map map = (Map) nextBean;
			Object key = convertIndexToMapKey(getter, bp.indexString);

			Class mapComponentType = extractGenericComponentType(getter);
			if (mapComponentType != Object.class) {
				value = convertType(value, mapComponentType);
			}
			map.put(key, value);
			return;
		}

		// failed
		if (bp.silent) {
			return;
		}
		throw new BeanException("Index property is not an array, list or map: " + bp.name, bp);
	}


	// ---------------------------------------------------------------- SET

	/**
	 * Sets Java Bean property.
	 * @param bean Java POJO bean or a Map
	 * @param name property name
	 * @param value property value
	 * @param declared consider declared properties as well
	 * @param forced force creation of missing values
	 * @param silent silent mode, no exception is thrown
	 */
	public void setProperty(Object bean, String name, Object value, boolean declared, boolean forced, boolean silent) {
		BeanProperty beanProperty = new BeanProperty(bean, name, declared, forced);

		try {
			resolveNestedProperties(beanProperty);
			setIndexProperty(beanProperty, value);
		} catch (Exception ex) {
			if (!silent) {
				ExceptionUtil.throwException(ex);
			}
		}

	}

	/**
	 * Sets Java Bean property.
	 */
	public void setProperty(Object bean, String name, Object value) {
		BeanProperty beanProperty = new BeanProperty(bean, name, false, false);
		resolveNestedProperties(beanProperty);
		setIndexProperty(beanProperty, value);
	}
	/**
	 * Sets Java Bean property silently, without throwing an exception on non-existing properties.
	 */
	public void setPropertySilent(Object bean, String name, Object value) {
		BeanProperty beanProperty = new BeanProperty(bean, name, false, false, true);
		try {
			resolveNestedProperties(beanProperty);
			setIndexProperty(beanProperty, value);
		} catch (Exception ignored) {
		}
	}

	/**
	 * Sets Java Bean property forced.
	 */
	public void setPropertyForced(Object bean, String name, Object value) {
		BeanProperty beanProperty = new BeanProperty(bean, name, false, true);
		resolveNestedProperties(beanProperty);
		setIndexProperty(beanProperty, value);
	}
	/**
	 * Sets Java Bean property forced, without throwing an exception on non-existing properties.
	 */
	public void setPropertyForcedSilent(Object bean, String name, Object value) {
		BeanProperty beanProperty = new BeanProperty(bean, name, false, true, true);
		try {
			resolveNestedProperties(beanProperty);
			setIndexProperty(beanProperty, value);
		} catch (Exception ignored) {
		}
	}

	/**
	 * Sets declared Java Bean property.
	 */
	public void setDeclaredProperty(Object bean, String name, Object value) {
		BeanProperty beanProperty = new BeanProperty(bean, name, true, false);
		resolveNestedProperties(beanProperty);
		setIndexProperty(beanProperty, value);
	}
	/**
	 * Silently sets declared Java Bean property.
	 */
	public void setDeclaredPropertySilent(Object bean, String name, Object value) {
		BeanProperty beanProperty = new BeanProperty(bean, name, true, false, true);
		try {
			resolveNestedProperties(beanProperty);
			setIndexProperty(beanProperty, value);
		} catch (Exception ignored) {
		}
	}
	/**
	 * Sets declared Java Bean property forced.
	 */
	public void setDeclaredPropertyForced(Object bean, String name, Object value) {
		BeanProperty beanProperty = new BeanProperty(bean, name, true, true);
		resolveNestedProperties(beanProperty);
		setIndexProperty(beanProperty, value);
	}
	/**
	 * Silently sets declared Java Bean property forced.
	 */
	public void setDeclaredPropertyForcedSilent(Object bean, String name, Object value) {
		BeanProperty beanProperty = new BeanProperty(bean, name, true, true, true);
		try {
			resolveNestedProperties(beanProperty);
			setIndexProperty(beanProperty, value);
		} catch (Exception ignored) {
		}
	}


	// ---------------------------------------------------------------- GET

	/**
	 * Returns value of bean's property.
	 */
	public Object getProperty(Object bean, String name) {
		BeanProperty beanProperty = new BeanProperty(bean, name, false, false);
		resolveNestedProperties(beanProperty);
		return getIndexProperty(beanProperty);
	}

	/**
	 * Silently returns value of bean's property.
	 * Return value <code>null</code> is ambiguous: it may means that property name
	 * is valid and property value is <code>null</code> or that property name is invalid.
	 */
	public Object getPropertySilently(Object bean, String name) {
		BeanProperty beanProperty = new BeanProperty(bean, name, false, false, true);
		try {
			resolveNestedProperties(beanProperty);
			return getIndexProperty(beanProperty);
		} catch (Exception ignored) {
			return null;
		}
	}

	/**
	 * Returns value of declared bean's property.
	 */
	public Object getDeclaredProperty(Object bean, String name) {
		BeanProperty beanProperty = new BeanProperty(bean, name, true, false);
		resolveNestedProperties(beanProperty);
		return getIndexProperty(beanProperty);
	}

	/**
	 * Silently returns value of declared bean's property.
	 * Return value <code>null</code> is ambiguous: it may means that property name
	 * is valid and property value is <code>null</code> or that property name is invalid.
	 */
	public Object getDeclaredPropertySilently(Object bean, String name) {
		BeanProperty beanProperty = new BeanProperty(bean, name, true, false, true);
		try {
			resolveNestedProperties(beanProperty);
			return getIndexProperty(beanProperty);
		} catch (Exception ignored) {
			return null;
		}
	}


	// ---------------------------------------------------------------- HAS

	/**
	 * Returns <code>true</code> if bean has a property.
	 */
	public boolean hasProperty(Object bean, String name) {
		BeanProperty beanProperty = new BeanProperty(bean, name, false, false);
		if (resolveExistingNestedProperties(beanProperty) == false) {
			return false;
		}
		return hasIndexProperty(beanProperty);
	}

	/**
	 * Returns <code>true</code> if bean has only a root property.
	 * If yes, this means that property may be injected into the bean.
	 * If not, bean does not contain the property.
	 */
	public boolean hasRootProperty(Object bean, String name) {
		int dotNdx = indexOfDot(name);
		if (dotNdx != -1) {
			name = name.substring(0, dotNdx);
		}
		BeanProperty beanProperty = new BeanProperty(bean, name, false, false);
		extractIndex(beanProperty);
		return hasSimpleProperty(beanProperty);
	}

	/**
	 * Returns <code>true</code> if bean has a declared property.
	 */
	public boolean hasDeclaredProperty(Object bean, String name) {
		BeanProperty beanProperty = new BeanProperty(bean, name, true, false);
		if (resolveExistingNestedProperties(beanProperty) == false) {
			return false;
		}
		return hasIndexProperty(beanProperty);
	}

	/**
	 * Returns <code>true</code> if bean has only a declared root property.
	 * If yes, this means that property may be injected into the bean.
	 * If not, bean does not contain the property.
	 */
	public boolean hasDeclaredRootProperty(Object bean, String name) {
		int dotNdx = indexOfDot(name);
		if (dotNdx != -1) {
			name = name.substring(0, dotNdx);
		}
		BeanProperty beanProperty = new BeanProperty(bean, name, true, false);
		extractIndex(beanProperty);
		return hasSimpleProperty(beanProperty);
	}

	// ---------------------------------------------------------------- type

	/**
	 * Returns property type.
	 */
	public Class getPropertyType(Object bean, String name) {
		BeanProperty beanProperty = new BeanProperty(bean, name, false, false);
		if (resolveExistingNestedProperties(beanProperty) == false) {
			return null;
		}
		hasIndexProperty(beanProperty);
		return extractType(beanProperty);
	}

	/**
	 * Returns property type of declared property.
	 */
	public Class getDeclaredPropertyType(Object bean, String name) {
		BeanProperty beanProperty = new BeanProperty(bean, name, true, false);
		if (resolveExistingNestedProperties(beanProperty) == false) {
			return null;
		}
		hasIndexProperty(beanProperty);
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