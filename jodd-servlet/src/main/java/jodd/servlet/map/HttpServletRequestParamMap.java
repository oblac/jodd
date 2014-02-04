// Copyright (c) 2003-2014, Jodd Team (jodd.org). All Rights Reserved.

package jodd.servlet.map;

import jodd.util.CollectionUtil;
import jodd.util.collection.StringKeyedMapAdapter;

import javax.servlet.http.HttpServletRequest;
import java.util.Iterator;

/**
 * Map backed by the Servlet HTTP request parameters for accessing request local attributes.
 * Map is read-only as setting or removing parameters is not possible.
 */
public class HttpServletRequestParamMap extends StringKeyedMapAdapter {

	/**
	 * The wrapped HTTP request.
	 */
	private final HttpServletRequest request;

	/**
	 * Create a new map wrapping the attributes of given request.
	 */
	public HttpServletRequestParamMap(HttpServletRequest request) {
		this.request = request;
	}

	@Override
	protected Object getAttribute(String key) {
		String[] values = request.getParameterValues(key);
		if (values == null) {
			return null;
		}
		if (values.length == 1) {
			return values[0];
		}
		return values;
	}

	@Override
	protected void setAttribute(String key, Object value) {
		throw new UnsupportedOperationException("Map is read-only");
	}

	@Override
	protected void removeAttribute(String key) {
		throw new UnsupportedOperationException("Map is read-only");
	}

	@Override
	@SuppressWarnings({"unchecked"})
	protected Iterator<String> getAttributeNames() {
		return CollectionUtil.asIterator(request.getParameterNames());
	}

}