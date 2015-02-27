// Copyright (c) 2003-present, Jodd Team (jodd.org). All Rights Reserved.

package jodd.servlet;

import javax.servlet.ServletRequestListener;
import javax.servlet.ServletRequestEvent;
import javax.servlet.http.HttpServletRequest;

/**
 * Bounds request to the current thread (and all children threads).
 */
public class RequestContextListener implements ServletRequestListener {

	private static final ThreadLocal<HttpServletRequest> requestHolder = new InheritableThreadLocal<HttpServletRequest>();

	public void requestInitialized(ServletRequestEvent requestEvent) {
		HttpServletRequest request = (HttpServletRequest) requestEvent.getServletRequest();
		requestHolder.set(request);
	}

	public void requestDestroyed(ServletRequestEvent servletRequestEvent) {
		requestHolder.remove();
	}

	/**
	 * Returns current HTTP servlet request. May return <code>null</code>
	 * is request was not bound to the thread.
	 */
	public static HttpServletRequest getRequest() {
		return requestHolder.get();
	}

}