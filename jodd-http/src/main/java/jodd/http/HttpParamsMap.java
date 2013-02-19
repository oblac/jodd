// Copyright (c) 2003-2013, Jodd Team (jodd.org). All Rights Reserved.

package jodd.http;

import jodd.util.ArraysUtil;

import java.lang.reflect.Array;
import java.util.LinkedHashMap;

/**
 * Map of HTTP parameters, either query or form.
 * It detects duplicate values and does not overwrite them, but
 * store them as an array.
 */
public class HttpParamsMap extends LinkedHashMap<String, Object> {

	@Override
	public Object put(String key, Object value) {
		Object existingValue = get(key);
		Object newValue = value;

		if (existingValue != null) {
			Class type = existingValue.getClass();

			if (!type.isArray()) {
				if ((value != null) && (value.getClass() != type)) {
					// second type is different from the first type
					throw new HttpException("Different types for: " + key);
				}

				Object[] values = (Object[]) Array.newInstance(type, 2);

				values[0] = existingValue;
				values[1] = value;

				newValue = values;
			}
			else {
				if ((value != null) && (value.getClass() != type.getComponentType())) {
					// second type is different from the first type
					throw new HttpException("Different types for: " + key);
				}

				newValue = ArraysUtil.append((Object[]) existingValue, value);
			}
		}

		return super.put(key, newValue);
	}

}