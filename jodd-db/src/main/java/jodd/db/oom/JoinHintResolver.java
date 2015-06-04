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
					throw new DbOomException("Hint value missing: " + key);
				}

				// don't merge nulls
				if (data[i] == null) {
					continue;
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
