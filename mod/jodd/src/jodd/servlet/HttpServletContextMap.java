// Copyright (c) 2003-2011, Jodd Team (jodd.org). All Rights Reserved.

package jodd.servlet;

import jodd.util.collection.StringKeyedMapAdapter;
import jodd.util.CollectionUtil;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import java.util.Iterator;

/**
 * Map backed by the Servlet context for accessing application scoped attributes.
 */
public class HttpServletContextMap extends StringKeyedMapAdapter {

	/**
	 * The wrapped servlet context.
	 */
	private final ServletContext context;

	/**
	 * Create a map wrapping given servlet context.
	 */
	public HttpServletContextMap(ServletContext context) {
		this.context = context;
	}

	public HttpServletContextMap(HttpServletRequest request) {
		this(request.getSession().getServletContext());
	}

	@Override
	protected Object getAttribute(String key) {
		return context.getAttribute(key);
	}

	@Override
	protected void setAttribute(String key, Object value) {
		context.setAttribute(key, value);
	}

	@Override
	protected void removeAttribute(String key) {
		context.removeAttribute(key);
	}

	@Override
	@SuppressWarnings({"unchecked"})
	protected Iterator<String> getAttributeNames() {
		return CollectionUtil.toIterator(context.getAttributeNames());
	}

}
