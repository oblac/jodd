// Copyright (c) 2003-2010, Jodd Team (jodd.org). All Rights Reserved.

package jodd.servlet;

import jodd.util.collection.StringKeyedMapAdapter;
import jodd.util.CollectionUtil;

import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpServletRequest;
import java.util.Iterator;

/**
 * Map backed by the Servlet session for accessing session scoped attributes.
 */
public class HttpSessionMap extends StringKeyedMapAdapter {

	private final HttpSession session;

	public HttpSessionMap(HttpSession session) {
		this.session = session;
	}

	public HttpSessionMap(HttpServletRequest request) {
		this(request.getSession());
	}

	@Override
	protected Object getAttribute(String key) {
		return session.getAttribute(key);
	}

	@Override
	protected void setAttribute(String key, Object value) {
		session.setAttribute(key, value);
	}

	@Override
	protected void removeAttribute(String key) {
		session.removeAttribute(key);
	}

	@Override
	@SuppressWarnings({"unchecked"})
	protected Iterator<String> getAttributeNames() {
		return CollectionUtil.toIterator(session.getAttributeNames());
	}

}