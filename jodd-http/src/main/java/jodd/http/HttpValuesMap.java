// Copyright (c) 2003-2014, Jodd Team (jodd.org). All Rights Reserved.

package jodd.http;

import jodd.util.ArraysUtil;

import java.util.LinkedHashMap;

/**
 * Map of HTTP parameters, either query or form.
 * It detects duplicate values and does not overwrite them, but
 * store them as an array.
 */
public abstract class HttpValuesMap<T> extends LinkedHashMap<String, T[]> {

	/**
	 * Creates new {@link jodd.http.HttpValuesMap} of strings values.
	 */
	public static HttpValuesMap<String> ofStrings() {
		return new HttpValuesMap<String>() {
			@Override
			protected String[] createNewArray() {
				return new String[1];
			}
		};
	}

	/**
	 * Creates new {@link jodd.http.HttpValuesMap} of object values.
	 */
	public static HttpValuesMap<Object> ofObjects() {
		return new HttpValuesMap<Object>() {
			@Override
			protected Object[] createNewArray() {
				return new Object[1];
			}
		};
	}

	/**
	 * Sets parameter value.
	 */
	public void set(String key, T value) {
		remove(key);
		add(key, value);
	}

	/**
	 * Creates new array.
	 */
	protected abstract T[] createNewArray();

	/**
	 * Adds parameter value.
	 */
	public void add(String key, T value) {
		// null values replaces all existing values for this key
		if (value == null) {
			put(key, null);
			return;
		}

		T[] values = get(key);

		if (values == null) {
			values = createNewArray();
			values[0] = value;
		} else {
			values = ArraysUtil.append(values, value);
		}

		super.put(key, values);
	}

	/**
	 * Returns the first value for given key.
	 */
	public T getFirst(String key) {
		T[] value = get(key);

		if (value == null) {
			return null;
		}

		return value[0];
	}

	/**
	 * Returns values as strings array.
	 */
	public String[] getStrings(String key) {
		T[] values = get(key);

		if (values == null) {
			return null;
		}

		String[] strings = new String[values.length];

		for (int i = 0; i < values.length; i++) {
			T value = values[i];

			strings[i] = value.toString();
		}

		return strings;
	}

}