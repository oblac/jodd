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

package jodd.introspector;

import jodd.util.ClassUtil;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;

import static jodd.util.ClassUtil.METHOD_GET_PREFIX;
import static jodd.util.ClassUtil.METHOD_IS_PREFIX;

/**
 * Bean properties collection. Property in Java is defined as a pair of
 * read and write method. In Jodd, property can be extended with field
 * definition. Moreover, properties will include just single fields.
 * This behavior can be controlled via {@link ClassDescriptor}.
 */
public class Properties {

	protected final ClassDescriptor classDescriptor;
	protected final HashMap<String, PropertyDescriptor> propertyDescriptors;

	// cache
	private PropertyDescriptor[] allProperties;

	public Properties(final ClassDescriptor classDescriptor) {
		this.classDescriptor = classDescriptor;
		this.propertyDescriptors = inspectProperties();
	}

	/**
	 * Inspects all properties of target type.
	 */
	protected HashMap<String, PropertyDescriptor> inspectProperties() {
		boolean scanAccessible = classDescriptor.isScanAccessible();
		Class type = classDescriptor.getType();

		HashMap<String, PropertyDescriptor> map = new HashMap<>();

		Method[] methods = scanAccessible ? ClassUtil.getAccessibleMethods(type) : ClassUtil.getSupportedMethods(type);

		for (int iteration = 0; iteration < 2; iteration++) {
			// first find the getters, and then the setters!
			for (Method method : methods) {
				if (Modifier.isStatic(method.getModifiers())) {
					continue;            // ignore static methods
				}

				boolean add = false;
				boolean issetter = false;

				String propertyName;

				if (iteration == 0) {
					propertyName = ClassUtil.getBeanPropertyGetterName(method);
					if (propertyName != null) {
						add = true;
						issetter = false;
					}
				} else {
					propertyName = ClassUtil.getBeanPropertySetterName(method);
					if (propertyName != null) {
						add = true;
						issetter = true;
					}
				}

				if (add) {
					MethodDescriptor methodDescriptor = classDescriptor.getMethodDescriptor(method.getName(), method.getParameterTypes(), true);
					addProperty(map, propertyName, methodDescriptor, issetter);
				}
			}
		}

		if (classDescriptor.isIncludeFieldsAsProperties()) {
			FieldDescriptor[] fieldDescriptors = classDescriptor.getAllFieldDescriptors();
			String[] prefix = classDescriptor.getPropertyFieldPrefix();

			for (FieldDescriptor fieldDescriptor : fieldDescriptors) {
				Field field = fieldDescriptor.getField();

				if (Modifier.isStatic(field.getModifiers())) {
					continue;            // ignore static fields
				}

				String name = field.getName();

				if (prefix != null) {
					for (String p : prefix) {
						if (!name.startsWith(p)) {
							continue;
						}
						name = name.substring(p.length());
						break;
					}
				}

				if (!map.containsKey(name)) {
					// add missing field as a potential property
					map.put(name, createPropertyDescriptor(name, fieldDescriptor));
				}
			}

		}

		return map;
	}


	/**
	 * Adds a setter and/or getter method to the property.
	 * If property is already defined, the new, updated, definition will be created.
	 */
	protected void addProperty(final HashMap<String, PropertyDescriptor> map, final String name, final MethodDescriptor methodDescriptor, final boolean isSetter) {
		MethodDescriptor setterMethod = isSetter ? methodDescriptor : null;
		MethodDescriptor getterMethod = isSetter ? null : methodDescriptor;

		PropertyDescriptor existing = map.get(name);

		if (existing == null) {
			// new property, just add it
			PropertyDescriptor propertyDescriptor = createPropertyDescriptor(name, getterMethod, setterMethod);

			map.put(name, propertyDescriptor);
			return;
		}

		// property exist

		if (!isSetter) {
			// use existing setter
			setterMethod = existing.getWriteMethodDescriptor();

			// check existing
			MethodDescriptor existingMethodDescriptor = existing.getReadMethodDescriptor();
			if (existingMethodDescriptor != null) {
				// check for special case of double get/is

				// getter with the same name already exist
				String methodName = methodDescriptor.getMethod().getName();
				String existingMethodName = existingMethodDescriptor.getMethod().getName();

				if (
						existingMethodName.startsWith(METHOD_IS_PREFIX) &&
						methodName.startsWith(METHOD_GET_PREFIX)) {

					// ignore getter when ister exist
					return;
				}
			}
		} else {
			// setter
			// use existing getter
			getterMethod = existing.getReadMethodDescriptor();

			if (getterMethod != null) {
				Class returnType = getterMethod.getMethod().getReturnType();

				if (setterMethod != null) {
					Class parameterType = setterMethod.getMethod().getParameterTypes()[0];

					if (returnType != parameterType) {
						// getter's type is different then setter's
						return;
					}

				}
			}
		}

		PropertyDescriptor propertyDescriptor = createPropertyDescriptor(name, getterMethod, setterMethod);

		map.put(name, propertyDescriptor);
	}

	/**
	 * Creates new {@link PropertyDescriptor}. Note that this method may be called
	 * up to three times (depends on use case) for the same property. Each time when
	 * a property is updated, a new definition is created with updated information.
	 */
	protected PropertyDescriptor createPropertyDescriptor(final String name, final MethodDescriptor getterMethod, final MethodDescriptor setterMethod) {
		return new PropertyDescriptor(classDescriptor, name, getterMethod, setterMethod);
	}

	/**
	 * Creates new field-only {@link PropertyDescriptor}. It will be invoked only once.
	 */
	protected PropertyDescriptor createPropertyDescriptor(final String name, final FieldDescriptor fieldDescriptor) {
			return new PropertyDescriptor(classDescriptor, name, fieldDescriptor);
	}

	// ---------------------------------------------------------------- get

	/**
	 * Returns {@link PropertyDescriptor property descriptor}.
	 */
	public PropertyDescriptor getPropertyDescriptor(final String name) {
		return propertyDescriptors.get(name);
	}

	/**
	 * Returns all property descriptors.
	 * Properties are sorted by name.
	 */
	public PropertyDescriptor[] getAllPropertyDescriptors() {
		if (allProperties == null) {
			PropertyDescriptor[] allProperties = new PropertyDescriptor[propertyDescriptors.size()];

			int index = 0;
			for (PropertyDescriptor propertyDescriptor : propertyDescriptors.values()) {
				allProperties[index] = propertyDescriptor;
				index++;
			}

			Arrays.sort(allProperties, new Comparator<PropertyDescriptor>() {
				@Override
				public int compare(final PropertyDescriptor pd1, final PropertyDescriptor pd2) {
					return pd1.getName().compareTo(pd2.getName());
				}
			});

			this.allProperties = allProperties;
		}
		return allProperties;
	}

}