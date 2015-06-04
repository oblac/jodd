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