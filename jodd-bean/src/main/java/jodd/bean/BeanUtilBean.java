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

import jodd.introspector.Getter;
import jodd.introspector.Setter;
import jodd.util.ClassUtil;
import jodd.util.StringUtil;

import java.lang.reflect.Array;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Instantiable version of {@link BeanUtil}.
 */
public class BeanUtilBean extends BeanUtilUtil implements BeanUtil {

	/**
	 * Sets the declared flag.
	 */
	public BeanUtilBean declared(boolean declared) {
		this.isDeclared = declared;
		return this;
	}

	/**
	 * Sets the forced flag.
	 */
	public BeanUtilBean forced(boolean forced) {
		this.isForced = forced;
		return this;
	}

	/**
	 * Sets the silent flag.
	 */
	public BeanUtilBean silent(boolean silent) {
		this.isSilent = silent;
		return this;
	}

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
			if (!hasIndexProperty(bp)) {
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

	@Override
	public boolean hasSimpleProperty(Object bean, String property) {
		return hasSimpleProperty(new BeanProperty(this, bean, property));
	}

	protected boolean hasSimpleProperty(BeanProperty bp) {
		if (bp.bean == null) {
			return false;
		}

		// try: getter
		Getter getter = bp.getGetter(isDeclared);
		if (getter != null) {
			return true;
		}

		// try: (Map) get("property")
		if (bp.isMap()) {
			Map map = (Map) bp.bean;
			if (map.containsKey(bp.name)) {
				return true;
			}
		}

		return false;
	}

	@Override
	public <T> T getSimpleProperty(Object bean, String property) {
		return (T) getSimpleProperty(new BeanProperty(this, bean, property));
	}

	protected Object getSimpleProperty(BeanProperty bp) {

		if (bp.name.length() == 0) {
			if (bp.indexString != null) {
				// index string exist, but property name is missing
				return bp.bean;
			}
			throw new BeanException("Invalid property", bp);
		}

		Getter getter = bp.getGetter(isDeclared);

		if (getter != null) {
			Object result;
			try {
				result = getter.invokeGetter(bp.bean);
			} catch (Exception ex) {
				if (isSilent) {
					return null;
				}
				throw new BeanException("Getter failed: " + getter, ex);
			}

			if ((result == null) && (isForced)) {
				result = createBeanProperty(bp);
			}
			return result;
		}

		// try: (Map) get("property")
		if (bp.isMap()) {
			Map map = (Map) bp.bean;
			Object key = convertIndexToMapKey(getter, bp.name);

			if (!map.containsKey(key)) {
				if (!isForced) {
					if (isSilent) {
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
		if (isSilent) {
			return null;
		}
		throw new BeanException("Simple property not found: " + bp.name, bp);
	}

	@Override
	public void setSimpleProperty(Object bean, String property, Object value) {
		setSimpleProperty(new BeanProperty(this, bean, property), value);
	}

	/**
	 * Sets a value of simple property.
	 */
	@SuppressWarnings({"unchecked"})
	protected void setSimpleProperty(BeanProperty bp, Object value) {
		Setter setter = bp.getSetter(isDeclared);

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
		if (isSilent) {
			return;
		}
		throw new BeanException("Simple property not found: " + bp.name, bp);
	}

	// ---------------------------------------------------------------- indexed property

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
		if (resultBean.getClass().isArray()) {
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

	@Override
	public <T> T getIndexProperty(Object bean, String property, int index) {
		BeanProperty bp = new BeanProperty(this, bean, property);

		bp.indexString = bp.index = String.valueOf(index);

		Object value = _getIndexProperty(bp);

		bp.indexString = null;

		return (T) value;
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
		Getter getter = bp.getGetter(isDeclared);

		if (bp.indexString == null) {
			return resultBean;	// no index, just simple bean
		}
		if (resultBean == null) {
			if (isSilent) {
				return null;
			}
			throw new BeanException("Index property is null: " + bp.name, bp);
		}

		// try: property[index]
		if (resultBean.getClass().isArray()) {
			int index = parseInt(bp.indexString, bp);
			if (isForced) {
				return arrayForcedGet(bp, resultBean, index);
			} else {
				return Array.get(resultBean, index);
			}
		}

		// try: list.get(index)
		if (resultBean instanceof List) {
			int index = parseInt(bp.indexString, bp);
			List list = (List) resultBean;
			if (!isForced) {
				return list.get(index);
			}
			if (!bp.last) {
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
					value = ClassUtil.newInstance(listComponentType);
				} catch (Exception ex) {
					if (isSilent) {
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

			if (!isForced) {
				return map.get(key);
			}
			Object value = map.get(key);
			if (!bp.last) {
				if (value == null) {
					Class mapComponentType = extractGenericComponentType(getter);
					if (mapComponentType == Object.class) {
						mapComponentType = Map.class;
					}
					try {
						value = ClassUtil.newInstance(mapComponentType);
					} catch (Exception ex) {
						if (isSilent) {
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
		if (isSilent) {
			return null;
		}
		throw new BeanException("Index property is not an array, list or map: " + bp.name, bp);
	}

	@Override
	public void setIndexProperty(Object bean, String property, int index, Object value) {
		BeanProperty bp = new BeanProperty(this, bean, property);

		bp.indexString = bp.index = String.valueOf(index);

		_setIndexProperty(bp, value);

		bp.indexString = null;
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
		Getter getter = bp.getGetter(isDeclared);

		if (nextBean == null) {
			if (isSilent) {
				return;
			}
			throw new BeanException("Index property is null:" + bp.name, bp);
		}

		// inner bean found
		if (nextBean.getClass().isArray()) {
			int index = parseInt(bp.indexString, bp);
			if (isForced) {
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
			if (isForced) {
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
		if (isSilent) {
			return;
		}
		throw new BeanException("Index property is not an array, list or map: " + bp.name, bp);
	}


	// ---------------------------------------------------------------- SET

	@Override
	public void setProperty(Object bean, String name, Object value) {
		BeanProperty beanProperty = new BeanProperty(this, bean, name);

		if (!isSilent) {
			resolveNestedProperties(beanProperty);
			setIndexProperty(beanProperty, value);
		}
		else {
			try {
				resolveNestedProperties(beanProperty);
				setIndexProperty(beanProperty, value);
			}
			catch (Exception ignore) {}
		}
	}

	// ---------------------------------------------------------------- GET

	/**
	 * Returns value of bean's property.
	 */
	@Override
	public <T> T getProperty(Object bean, String name) {
		BeanProperty beanProperty = new BeanProperty(this, bean, name);
		if (!isSilent) {
			resolveNestedProperties(beanProperty);
			return (T) getIndexProperty(beanProperty);
		}
		else {
			try {
				resolveNestedProperties(beanProperty);
				return (T) getIndexProperty(beanProperty);
			}
			catch (Exception ignore) {
				return null;
			}
		}
	}

	// ---------------------------------------------------------------- HAS

	@Override
	public boolean hasProperty(Object bean, String name) {
		BeanProperty beanProperty = new BeanProperty(this, bean, name);
		if (!resolveExistingNestedProperties(beanProperty)) {
			return false;
		}
		return hasIndexProperty(beanProperty);
	}

	@Override
	public boolean hasRootProperty(Object bean, String name) {
		int dotNdx = indexOfDot(name);
		if (dotNdx != -1) {
			name = name.substring(0, dotNdx);
		}
		BeanProperty beanProperty = new BeanProperty(this, bean, name);
		extractIndex(beanProperty);
		return hasSimpleProperty(beanProperty);
	}

	// ---------------------------------------------------------------- type

	@Override
	public Class<?> getPropertyType(Object bean, String name) {
		BeanProperty beanProperty = new BeanProperty(this, bean, name);
		if (!resolveExistingNestedProperties(beanProperty)) {
			return null;
		}
		hasIndexProperty(beanProperty);
		return extractType(beanProperty);
	}

	// ---------------------------------------------------------------- utilities

	private static final char[] INDEX_CHARS = new char[] {'.', '['};

	/**
	 * Extract the first name of this reference.
	 */
	@Override
	public String extractThisReference(String propertyName) {
		int ndx = StringUtil.indexOfChars(propertyName, INDEX_CHARS);
		if (ndx == -1) {
			return propertyName;
		}
		return propertyName.substring(0, ndx);
	}

}