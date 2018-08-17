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

package jodd.json;

import jodd.introspector.ClassDescriptor;
import jodd.introspector.ClassIntrospector;
import jodd.introspector.PropertyDescriptor;
import jodd.introspector.Setter;
import jodd.typeconverter.TypeConverterManager;
import jodd.util.ClassLoaderUtil;
import jodd.util.ClassUtil;
import jodd.util.Wildcard;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Map to bean converter.
 * Used when parsing with class metadata enabled.
 */
public class MapToBean {

	protected boolean declared = true;
	protected final JsonParserBase jsonParser;
	protected final String classMetadataName;

	public MapToBean(final JsonParserBase jsonParser, final String classMetadataName) {
		this.jsonParser = jsonParser;
		this.classMetadataName = classMetadataName;
	}

	/**
	 * Converts map to target type.
	 */
	public Object map2bean(final Map map, Class targetType) {
		Object target = null;

		// create targets type
		String className = (String) map.get(classMetadataName);

		if (className == null) {
			if (targetType == null) {
				// nothing to do, no information about target type found
				target = map;
			}
		}
		else {
			checkClassName(jsonParser.classnameWhitelist, className);

			try {
				targetType = ClassLoaderUtil.loadClass(className);
			} catch (ClassNotFoundException cnfex) {
				throw new JsonException(cnfex);
			}
		}

		if (target == null) {
			target = jsonParser.newObjectInstance(targetType);
		}

		ClassDescriptor cd = ClassIntrospector.get().lookup(target.getClass());

		boolean targetIsMap = target instanceof Map;

		for (Object key : map.keySet()) {
			String keyName = key.toString();

			if (classMetadataName != null) {
				if (keyName.equals(classMetadataName)) {
					continue;
				}
			}

			PropertyDescriptor pd = cd.getPropertyDescriptor(keyName, declared);

			if (!targetIsMap && pd == null) {
				// target property does not exist, continue
				continue;
			}

			// value is one of JSON basic types, like Number, Map, List...
			Object value = map.get(key);

			Class propertyType = pd == null ? null : pd.getType();
			Class componentType = pd == null ? null : pd.resolveComponentType(true);

			if (value != null) {
				if (value instanceof List) {
					if (componentType != null && componentType != String.class) {
						value = generifyList((List) value, componentType);
					}
				}
				else if (value instanceof Map) {
					// if the value we want to inject is a Map...
					if (!ClassUtil.isTypeOf(propertyType, Map.class)) {
						// ... and if target is NOT a map
						value = map2bean((Map) value, propertyType);
					}
					else {
						// target is also a Map, but we might need to generify it
						Class keyType = pd == null ? null : pd.resolveKeyType(true);

						if (keyType != String.class || componentType != String.class) {
							// generify
							value = generifyMap((Map) value, keyType, componentType);
						}
					}
				}
			}

			if (targetIsMap) {
				((Map)target).put(keyName, value);
			}
			else {
				try {
					setValue(target, pd, value);
				} catch (Exception ignore) {
					ignore.printStackTrace();
				}
			}
		}

		return target;
	}

	private void checkClassName(final List<String> classnameWhitelist, final String className) {
		if (classnameWhitelist == null) {
			return;
		}
		classnameWhitelist.forEach(pattern -> {
			if (!Wildcard.equalsOrMatch(className, pattern)) {
				throw new JsonException("Class can't be loaded as it is not whitelisted: " + className);
			}
		});
	}

	/**
	 * Converts type of all list elements to match the component type.
	 */
	private Object generifyList(final List list, final Class componentType) {
		for (int i = 0; i < list.size(); i++) {
			Object element = list.get(i);

			if (element != null) {
				if (element instanceof Map) {
					Object bean = map2bean((Map) element, componentType);
					list.set(i, bean);
				} else {
					Object value = convert(element, componentType);
					list.set(i, value);
				}
			}
		}

		return list;
	}

	/**
	 * Sets the property value.
	 */
	private void setValue(final Object target, final PropertyDescriptor pd, Object value) throws InvocationTargetException, IllegalAccessException {
		Class propertyType;

		Setter setter = pd.getSetter(true);
		if (setter != null) {
			if (value != null) {
				propertyType = setter.getSetterRawType();
				value = jsonParser.convertType(value, propertyType);
			}
			setter.invokeSetter(target, value);
		}
	}

	/**
	 * Change map elements to match key and value types.
	 */
	protected <K,V> Map<K, V> generifyMap(final Map<Object, Object> map, final Class<K> keyType, final Class<V> valueType) {

		if (keyType == String.class) {
			// only value type is changed, we can make value replacements
			for (Map.Entry<Object, Object> entry : map.entrySet()) {
				Object value = entry.getValue();
				Object newValue = convert(value, valueType);

				if (value != newValue) {
					entry.setValue(newValue);
				}
			}
			return (Map<K, V>) map;
		}

		// key is changed too, we need a new map
		Map<K, V> newMap = new HashMap<>(map.size());

		for (Map.Entry<Object, Object> entry : map.entrySet()) {
			Object key = entry.getKey();
			Object newKey = convert(key, keyType);

			Object value = entry.getValue();
			Object newValue = convert(value, valueType);

			newMap.put((K)newKey, (V)newValue);
		}

		return newMap;
	}

	protected Object convert(final Object value, final Class targetType) {
		Class valueClass = value.getClass();

		if (valueClass == targetType) {
			return value;
		}

		if (value instanceof Map) {
			if (targetType == Map.class) {
				return value;
			}

			return map2bean((Map) value, targetType);
		}

		try {
			return TypeConverterManager.get().convertType(value, targetType);
		}
		catch (Exception ex) {
			throw new JsonException("Type conversion failed", ex);
		}
	}

}