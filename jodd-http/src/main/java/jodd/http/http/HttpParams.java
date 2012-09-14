// Copyright (c) 2003-2012, Jodd Team (jodd.org). All Rights Reserved.

package jodd.io.http;

import jodd.servlet.URLCoder;
import jodd.servlet.URLDecoder;
import jodd.util.ArraysUtil;
import jodd.util.KeyValue;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * HTTP parameters for handling both query and request parameters.
 */
public class HttpParams {
	
	protected final Map<String, Object> params;

	protected boolean hasFiles;

	public HttpParams(Map<String, Object> params) {
		this.params = params;
	}

	public HttpParams() {
		this.params = new LinkedHashMap<String, Object>();
	}

	public HttpParams(String query) {
		this(query, false);
	}

	public HttpParams(String query, boolean decode) {
		this.params = new LinkedHashMap<String, Object>();
		addParameters(query, decode);
	}

	// ---------------------------------------------------------------- methods

	/**
	 * Returns total number of unique parameter names.
	 */
	public int getParamsCount() {
		return params.size();
	}

	/**
	 * Returns <code>true</code> if parameters contains
	 * at least on non-string parameter, i.e. an upload file.
	 */
	public boolean hasFiles() {
		return hasFiles;
	}

	/**
	 * Returns parameter value.
	 */
	public Object getParameter(String key) {
		return params.get(key);
	}

	/**
	 * Adds a parameter.Parameter may be:
	 * <li>string, for single-value parameters
	 * <li>string array, for multi-value parameter
	 * <li>File, for files
	 * <p>
	 * String parameters are accumulated, so adding a
	 * parameter with the same name twice will result in
	 * having a string array as a value.
	 */
	public void addParameter(String key, Object value) {
		Object existing = params.get(key);
		
		if (existing != null) {
			Class type = existing.getClass();

			if (type == String.class) {
				value = new String[] {existing.toString(), value.toString()};
			} else if (type == String[].class) {
				value = ArraysUtil.append((String[]) existing, value.toString());
			} else {
				hasFiles = true;
			}
		}

		setParameter(key, value);
	}

	/**
	 * Sets a parameter. Existing parameters are simply overwritten.
	 */
	public void setParameter(String name, Object value) {
		Class type = value.getClass();
		if ((type != String.class) && (type != String[].class)) {
			hasFiles = true;
		}
		params.put(name, value);
	}

	/**
	 * Add query parameters by parsing the query string.
	 * Optionally, all names and values may be URL decoded.
	 */
	public void addParameters(String query, boolean decode) {

		int ndx, ndx2 = 0;
		while (true) {
			ndx = query.indexOf('=', ndx2);
			if (ndx == -1) {
				if (ndx2 < query.length()) {
					params.put(query.substring(ndx2), null);
				}
				break;
			}
			String name = query.substring(ndx2, ndx);
			if (decode) {
				name = URLDecoder.decode(name);
			}

			ndx2 = ndx + 1;
			ndx = query.indexOf('&', ndx2);
			if (ndx == -1) {
				ndx = query.length();
			}
			String value = query.substring(ndx2, ndx);
			if (decode) {
				value = URLDecoder.decode(value);
			}

			Object newValue = value;
			Object existing = params.get(name);

			if (existing != null) {
				if (existing.getClass().isArray()) {
					newValue = ArraysUtil.append(((String[]) existing), newValue);
				} else {
					newValue = new String[] {existing.toString(), value};
				}
			}

			params.put(name, newValue);

			ndx2 = ndx + 1;
		}
	}

	/**
	 * Removes a parameter.
	 */
	public void removeParameter(String key) {
		params.remove(key);
	}

	/**
	 * Returns parameters iterator.
	 */
	public Iterator<KeyValue<String, Object>> iterate() {
		final Iterator<Map.Entry<String,Object>> iterator = params.entrySet().iterator();

		final KeyValue<String, Object> keyValue = new KeyValue<String, Object>();
		
		return new Iterator<KeyValue<String, Object>>() {
			public boolean hasNext() {
				return iterator.hasNext();
			}

			public KeyValue<String, Object> next() {
				Map.Entry<String,Object> entry = iterator.next();
				keyValue.setKey(entry.getKey());
				keyValue.setValue(entry.getValue());
				return keyValue;
			}

			public void remove() {
				throw new UnsupportedOperationException();
			}
		};
	}

	// ---------------------------------------------------------------- generates

	/**
	 * Generates encoded string of parameters.
	 */
	public String toString() {
		StringBuilder sb = new StringBuilder();

		boolean first = true;
		for (Map.Entry<String, Object> entry : params.entrySet()) {
			if (first) {
				first = false;
			} else {
				sb.append('&');
			}
			String key = URLCoder.encodeQuery(entry.getKey());

			Object value = entry.getValue();
			if (value != null) {
				Class type = value.getClass();
				
				if (type == String[].class) {
					Object[] array = (Object[]) value;
					for (int i = 0; i < array.length; i++) {
						if (i != 0) {
							sb.append('&');
						}
						Object o = array[i];
						sb.append(key);
						sb.append('=');
						sb.append(URLCoder.encodeQuery(o.toString()));
					}
				} else {
					sb.append(key);
					sb.append('=');
					sb.append(URLCoder.encodeQuery(value.toString()));
				}
			} else {
				sb.append(key);
			}
		}
		return sb.toString();
	}

}
