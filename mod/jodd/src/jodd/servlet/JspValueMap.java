// Copyright (c) 2003-2012, Jodd Team (jodd.org). All Rights Reserved.

package jodd.servlet;

import jodd.util.collection.EnumerationIterator;
import jodd.util.collection.StringKeyedMapAdapter;
import jodd.util.collection.CompositeIterator;

import javax.servlet.http.HttpServletRequest;
import java.util.Iterator;

/**
 * Resolves JSP values from all scopes.
 */
public class JspValueMap extends StringKeyedMapAdapter {

	private final HttpServletRequest request;

	public JspValueMap(HttpServletRequest request) {
		this.request = request;
	}

	/**
	 * @see jodd.servlet.ServletUtil#value(javax.servlet.jsp.PageContext, String) 
	 */
	@Override
	protected Object getAttribute(String key) {
		return ServletUtil.value(request, key);
	}

	@Override
	protected void setAttribute(String key, Object value) {
		request.setAttribute(key, value);
	}

	@Override
	protected void removeAttribute(String key) {
		request.removeAttribute(key);
		request.getSession().removeAttribute(key);
		request.getSession().getServletContext().removeAttribute(key);
	}

	@Override
	@SuppressWarnings({"unchecked"})
	protected Iterator<String> getAttributeNames() {
		return new CompositeIterator(
				new EnumerationIterator<String>(request.getAttributeNames()),
				new EnumerationIterator<String>(request.getSession().getAttributeNames()),
				new EnumerationIterator<String>(request.getSession().getServletContext().getAttributeNames())
		);
	}

}

