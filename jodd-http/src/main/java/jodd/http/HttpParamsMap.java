// Copyright (c) 2003-2013, Jodd Team (jodd.org). All Rights Reserved.

package jodd.http;

import jodd.util.ArraysUtil;

import java.util.LinkedHashMap;

/**
 * Map of HTTP parameters, either query or form.
 * It detects duplicate values and does not overwrite them, but
 * store them as an array.
 * todo rename to HttpValueMap
 */
public class HttpParamsMap extends LinkedHashMap<String, Object[]> {

	/**
	 * Sets parameter value.
	 */
	public void set(String key, Object value) {
		remove(key);
		add(key, value);
	}

	/**
	 * Adds parameter value.
	 */
	public void add(String key, Object value) {
		// null values replaces all existing values for this key
		if (value == null) {
			put(key, null);
			return;
		}

		Object[] values = get(key);

		if (values == null) {
			values = new Object[] {value};
		} else {
			values = ArraysUtil.append(values, value);
		}

		super.put(key, values);
	}

	/**
	 * Returns the first value for given key.
	 */
	public Object getFirst(String key) {
		Object[] value = get(key);

		if (value == null) {
			return null;
		}

		return value[0];
	}

	/**
	 * Returns values as strings array.
	 */
	public String[] getStrings(String key) {
		Object[] values = get(key);

		if (values == null) {
			return null;
		}

		String[] strings = new String[values.length];

		for (int i = 0; i < values.length; i++) {
			Object value = values[i];

			strings[i] = value.toString();
		}

		return strings;
	}

}