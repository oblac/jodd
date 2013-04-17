// Copyright (c) 2003-2013, Jodd Team (jodd.org). All Rights Reserved.

package jodd.db.oom;

import jodd.introspector.ClassDescriptor;
import jodd.introspector.ClassIntrospector;
import jodd.util.ArraysUtil;
import jodd.util.ReflectUtil;
import jodd.util.StringUtil;
import jodd.bean.BeanUtil;

import java.lang.reflect.Array;
import java.util.Collection;
import java.util.Map;
import java.util.HashMap;

/**
 * Joins an array of objects using provided hints.
 * If hint is not available, methods returns the very same object array instance. 
 */
public class JoinHintResolver {

	public Object[] join(Object[] data, String hints) {
		if (hints == null) {
			return data;
		}
		return join(data, StringUtil.splitc(hints, ','));
	}

	/**
	 * Joins entity array using provided string hints.
	 */
	public Object[] join(Object[] data, String[] hints) {
		if (hints == null) {
			return data;
		}
		// build context
		Map<String, Object> context = new HashMap<String, Object>(hints.length);
		for (int i = 0; i < hints.length; i++) {
			hints[i] = hints[i].trim();
			String hint = hints[i];
			if (hint.indexOf('.') == -1) {
				context.put(hint, data[i]);
			}
		}

		// no joining hints found
		if (context.size() == data.length) {
			return data;
		}

		// joining
		Object[] result = new Object[context.size()];
		int count = 0;
		for (int i = 0; i < hints.length; i++) {
			String hint = hints[i];
			int ndx = hint.indexOf('.');
			if (ndx != -1) {
				String key = hint.substring(0, ndx);
				Object value = context.get(key);
				if (value == null) {
					throw new DbOomException("Hint key not found:" + key);
				}

				String hintPropertyName = hint.substring(ndx + 1);
				Class hintPropertyType = BeanUtil.getPropertyType(value, hintPropertyName);

				if (hintPropertyType != null) {
					ClassDescriptor cd = ClassIntrospector.lookup(hintPropertyType);

					if (cd.isCollection()) {
						// add element to collection
						try {
							Collection collection = (Collection) BeanUtil.getDeclaredProperty(value, hintPropertyName);

							if (collection == null) {
								collection = (Collection) ReflectUtil.newInstance(hintPropertyType);
								BeanUtil.setDeclaredPropertySilent(value, hintPropertyName, collection);
							}

							collection.add(data[i]);
						} catch (Exception ex) {
							throw new DbOomException(ex);
						}
					} else if (cd.isArray()) {
						// add element to array
						try {
							Object[] array = (Object[]) BeanUtil.getDeclaredProperty(value, hintPropertyName);

							if (array == null) {
								array = (Object[]) Array.newInstance(hintPropertyType.getComponentType(), 1);

								BeanUtil.setDeclaredPropertySilent(value, hintPropertyName, array);

								array[0] = data[i];
							} else {
								Object[] newArray = ArraysUtil.append(array, data[i]);

								if (newArray != array) {
									BeanUtil.setDeclaredPropertySilent(value, hintPropertyName, newArray);
								}
							}
						} catch (Exception ex) {
							throw new DbOomException(ex);
						}
					} else {
						// set value
						BeanUtil.setDeclaredPropertySilent(value, hintPropertyName, data[i]);
					}
				}

			} else {
				result[count] = data[i];
				count++;
			}
		}
		return result;
	}
}
