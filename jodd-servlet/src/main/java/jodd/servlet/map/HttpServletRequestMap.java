// Copyright (c) 2003-2014, Jodd Team (jodd.org). All Rights Reserved.

package jodd.servlet.map;

import jodd.util.CollectionUtil;
import jodd.util.collection.StringKeyedMapAdapter;

import javax.servlet.http.HttpServletRequest;
import java.util.Iterator;

/**
 * Map backed by the Servlet HTTP request attribute map for accessing request local attributes.
 */
public class HttpServletRequestMap extends StringKeyedMapAdapter {

	/**
	 * The wrapped HTTP request.
	 */
	private final HttpServletRequest request;

	/**
	 * Create a new map wrapping the attributes of given request.
	 */
	public HttpServletRequestMap(HttpServletRequest request) {
		this.request = request;
	}

	@Override
	protected Object getAttribute(String key) {
		return request.getAttribute(key);
	}

	@Override
	protected void setAttribute(String key, Object value) {
		request.setAttribute(key, value);
	}

	@Override
	protected void removeAttribute(String key) {
		request.removeAttribute(key);
	}

	@Override
	@SuppressWarnings({"unchecked"})
	protected Iterator<String> getAttributeNames() {
		return CollectionUtil.asIterator(request.getAttributeNames());
	}
}
